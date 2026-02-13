#!/usr/bin/env node

/**
 * @fileoverview Maven Proxy Wrapper for Restricted Sandbox Environments
 *
 * This script enables Maven to access Maven Central in a restricted sandbox by creating
 * a three-layer proxy chain:
 *
 * ```
 * Maven -> Local Proxy -> Upstream Sandbox Proxy -> Maven Central
 *        (HTTP)        (HTTP CONNECT)         (HTTP/2 over TLS)
 * ```
 *
 * ## Architecture
 *
 * ### Layer 1: Maven → Local Proxy (localhost:8080)
 * - Protocol: Plain HTTP
 * - Maven configured via ~/.m2/settings.xml to use http://localhost:8080
 * - No authentication required (localhost trust)
 * - Maven makes standard HTTP requests
 *
 * ### Layer 2: Local Proxy → Upstream Proxy
 * - Protocol: HTTP CONNECT tunneling with Basic authentication
 * - Authentication: HTTP Basic with username and JWT token password
 * - Creates TCP tunnel through authenticated proxy
 * - Upstream proxy performs DNS resolution (sandbox has no DNS)
 *
 * ### Layer 3: Local Proxy → Maven Central (through tunnel)
 * - Protocol: HTTP/2 over TLS
 * - TLS handshake with ALPN negotiation
 * - HTTP/2 required by Maven Central (Cloudflare)
 * - Certificate validation for security
 *
 * ## Why This Architecture?
 *
 * - **Why not Maven → Upstream Proxy directly?**
 *   Maven only supports Basic/Digest auth, can't handle JWT tokens
 *
 * - **Why not Maven → Maven Central directly?**
 *   Sandbox blocks outbound HTTPS and has no DNS resolution
 *
 * - **Why HTTP/2?**
 *   Maven Central (Cloudflare) requires HTTP/2; HTTP/1.1 gets 301 redirects
 *
 * ## Usage
 *
 * ```bash
 * node run-mvn.js clean install
 * node run-mvn.js package
 * node run-mvn.js test
 * ```
 *
 * ## How It Works
 *
 * 1. Ensures ~/.m2/settings.xml exists with proxy mirror configuration
 * 2. Starts HTTP server on localhost:8080
 * 3. For each Maven request:
 *    - Receives HTTP request from Maven
 *    - Establishes HTTP CONNECT tunnel to upstream proxy with JWT auth
 *    - Wraps tunnel with TLS (ALPN: h2)
 *    - Makes HTTP/2 request to Maven Central
 *    - Streams response back to Maven
 * 4. Runs Maven with provided arguments
 * 5. Shuts down proxy server when complete
 *
 * ## Security
 *
 * - JWT token from HTTPS_PROXY env variable
 * - Local proxy only accessible on localhost
 * - TLS certificate validation for Maven Central
 * - End-to-end encryption maintained through tunnel
 *
 * ## Requirements
 *
 * - Node.js with http2 support
 * - Maven 3.x
 * - HTTPS_PROXY environment variable with format: http://username:jwt_token@host:port
 */

const { spawn } = require('child_process');
const http = require('http');
const https = require('https');
const http2 = require('http2');
const tls = require('tls');
const url = require('url');
const path = require('path');
const fs = require('fs');
const os = require('os');

const PROXY_PORT = 8080;
const MAVEN_CENTRAL_URL = 'https://repo.maven.apache.org/maven2';
const OXYGENXML_URL = 'https://www.oxygenxml.com/maven';

let proxyServer = null;

/**
 * Parse HTTPS_PROXY to get proxy connection details
 */
function getProxyInfo() {
  const proxyUrl = process.env.HTTPS_PROXY || process.env.https_proxy;
  if (!proxyUrl) {
    return null;
  }

  const parsed = new url.URL(proxyUrl);
  const auth = parsed.username && parsed.password
    ? Buffer.from(`${parsed.username}:${parsed.password}`).toString('base64')
    : null;

  return {
    host: parsed.hostname,
    port: parsed.port || 15004,
    auth: auth,
    username: parsed.username
  };
}

/**
 * Forward request through HTTPS_PROXY using HTTP CONNECT tunneling and HTTP/2
 */
function forwardThroughProxy(requestUrl, callback, errorCallback) {
  const parsedUrl = new url.URL(requestUrl);
  const proxyInfo = getProxyInfo();

  if (!proxyInfo || !proxyInfo.auth) {
    // Direct connection (no proxy)
    const req = https.get(requestUrl, callback);
    if (errorCallback) req.on('error', errorCallback);
    return req;
  }

  // Step 1: Establish HTTP CONNECT tunnel
  const connectReq = http.request({
    host: proxyInfo.host,
    port: proxyInfo.port,
    method: 'CONNECT',
    path: `${parsedUrl.hostname}:${parsedUrl.port || 443}`,
    headers: {
      'Host': `${parsedUrl.hostname}:${parsedUrl.port || 443}`,
      'Proxy-Authorization': `Basic ${proxyInfo.auth}`,
      'Proxy-Connection': 'Keep-Alive'
    }
  });

  connectReq.on('connect', (res, socket) => {
    if (res.statusCode !== 200) {
      const error = new Error(`Tunnel failed: ${res.statusCode}`);
      if (errorCallback) errorCallback(error);
      socket.destroy();
      return;
    }

    // Step 2: Wrap with TLS and enable HTTP/2 via ALPN
    const tlsSocket = tls.connect({
      socket: socket,
      servername: parsedUrl.hostname,
      ALPNProtocols: ['h2', 'http/1.1']
    });

    tlsSocket.on('secureConnect', () => {
      const protocol = tlsSocket.alpnProtocol;
      const requestPath = parsedUrl.pathname + parsedUrl.search;

      if (protocol === 'h2') {
        // Step 3: HTTP/2 request
        const client = http2.connect(`https://${parsedUrl.hostname}`, {
          createConnection: () => tlsSocket
        });

        const req = client.request({
          ':method': 'GET',
          ':path': requestPath
        });

        req.on('response', (headers) => {
          // Convert HTTP/2 headers to HTTP/1.1 format
          const responseHeaders = {};
          for (const [key, value] of Object.entries(headers)) {
            if (!key.startsWith(':')) {
              responseHeaders[key] = value;
            }
          }

          callback({
            statusCode: headers[':status'],
            headers: responseHeaders,
            pipe: (dest) => req.pipe(dest),
            on: (event, handler) => req.on(event, handler)
          });
        });

        req.on('error', (err) => {
          if (errorCallback) errorCallback(err);
          client.close();
        });

        req.end();
      } else {
        // HTTP/1.1 fallback
        const httpsReq = https.request({
          host: parsedUrl.hostname,
          port: parsedUrl.port || 443,
          path: requestPath,
          method: 'GET',
          socket: tlsSocket,
          createConnection: () => tlsSocket
        }, callback);

        if (errorCallback) httpsReq.on('error', errorCallback);
        httpsReq.end();
      }
    });

    tlsSocket.on('error', (err) => {
      if (errorCallback) errorCallback(err);
    });
  });

  connectReq.on('error', (err) => {
    if (errorCallback) errorCallback(err);
  });

  connectReq.end();
  return connectReq;
}

/**
 * Start the Maven proxy server
 */
function startProxyServer() {
  return new Promise((resolve, reject) => {
    proxyServer = http.createServer((req, res) => {
      const centralUrl = `${MAVEN_CENTRAL_URL}${req.url}`;
      const oxygenUrl = `${OXYGENXML_URL}${req.url}`;

      forwardThroughProxy(
        centralUrl,
        (proxyRes) => {
          if (proxyRes.statusCode === 404 || proxyRes.statusCode === 403) {
            // Drain the response body before trying fallback
            proxyRes.on('data', () => {});
            proxyRes.on('end', () => {
              forwardThroughProxy(
                oxygenUrl,
                (oxyRes) => {
                  Object.keys(oxyRes.headers).forEach(key => {
                    res.setHeader(key, oxyRes.headers[key]);
                  });
                  res.writeHead(oxyRes.statusCode);
                  oxyRes.pipe(res);
                },
                (err) => {
                  console.error(`OxygenXML proxy error: ${err.message}`);
                  res.writeHead(502, { 'Content-Type': 'text/plain' });
                  res.end(`Bad Gateway: ${err.message}`);
                }
              );
            });
          } else {
            Object.keys(proxyRes.headers).forEach(key => {
              res.setHeader(key, proxyRes.headers[key]);
            });
            res.writeHead(proxyRes.statusCode);
            proxyRes.pipe(res);
          }
        },
        (err) => {
          console.error(`Proxy error: ${err.message}`);
          res.writeHead(502, { 'Content-Type': 'text/plain' });
          res.end(`Bad Gateway: ${err.message}`);
        }
      );
    });

    proxyServer.on('error', reject);

    proxyServer.listen(PROXY_PORT, () => {
      console.log(`Maven proxy ready on http://localhost:${PROXY_PORT}`);
      resolve();
    });
  });
}

/**
 * Stop the proxy server
 */
function stopProxyServer() {
  if (proxyServer) {
    console.log('Stopping proxy server...');
    proxyServer.close();
    proxyServer = null;
  }
}

/**
 * Check if proxy server is responding
 */
async function checkProxyReady() {
  return new Promise((resolve) => {
    const req = http.get(`http://localhost:${PROXY_PORT}/`, () => resolve(true));
    req.on('error', () => resolve(false));
    req.end();
  });
}

/**
 * Wait for proxy to be ready
 */
async function waitForProxy(maxAttempts = 30) {
  for (let i = 0; i < maxAttempts; i++) {
    if (await checkProxyReady()) return true;
    await new Promise(resolve => setTimeout(resolve, 100));
  }
  return false;
}

/**
 * Ensure Maven settings.xml exists with proxy configuration
 */
function ensureMavenSettings() {
  const m2Dir = path.join(os.homedir(), '.m2');
  const settingsFile = path.join(m2Dir, 'settings.xml');

  if (!fs.existsSync(m2Dir)) {
    fs.mkdirSync(m2Dir, { recursive: true });
  }

  if (!fs.existsSync(settingsFile)) {
    const settingsContent = `<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>local-maven-proxy</id>
      <url>http://localhost:${PROXY_PORT}</url>
      <mirrorOf>*</mirrorOf>
    </mirror>
  </mirrors>
</settings>
`;
    fs.writeFileSync(settingsFile, settingsContent, 'utf8');
    console.log(`Created ${settingsFile}`);
  }
}

/**
 * Run Maven with the provided arguments
 */
function runMaven(args) {
  return new Promise((resolve, reject) => {
    console.log(`\nRunning: mvn ${args.join(' ')}\n`);

    const mvn = spawn('mvn', args, {
      stdio: 'inherit',
      env: process.env
    });

    mvn.on('exit', (code) => {
      if (code === 0) {
        resolve();
      } else {
        reject(new Error(`Maven exited with code ${code}`));
      }
    });

    mvn.on('error', reject);
  });
}

/**
 * Main function
 */
async function main() {
  const mavenArgs = process.argv.slice(2);

  if (mavenArgs.length === 0) {
    console.error('Usage: run-mvn.js <maven-arguments>');
    console.error('Example: run-mvn.js clean install');
    process.exit(1);
  }

  try {
    ensureMavenSettings();
    await startProxyServer();

    if (!await waitForProxy()) {
      throw new Error('Proxy failed to start');
    }

    await runMaven(mavenArgs);
    console.log('\n✓ Maven build completed');
    process.exit(0);
  } catch (err) {
    console.error(`\n✗ Error: ${err.message}`);
    process.exit(1);
  } finally {
    stopProxyServer();
  }
}

// Signal handlers for cleanup
process.on('SIGINT', () => {
  stopProxyServer();
  process.exit(130);
});

process.on('SIGTERM', () => {
  stopProxyServer();
  process.exit(143);
});

main();
