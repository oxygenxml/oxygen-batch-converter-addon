package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class App {
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));

    var xmlMapper = new XmlMapper();
    var jsonMapper = new ObjectMapper();

    Javalin app = Javalin.create(config -> {
      // Fly proxy reaches your service via a private IPv4 address, so bind to 0.0.0.0
      config.jetty.defaultHost = "0.0.0.0";
      config.jetty.defaultPort = port;

      // Serve files from src/main/resources/public/*
      config.staticFiles.add("/public", Location.CLASSPATH);
    });

    // Basic health check endpoint for Fly
    app.get("/health", ctx -> ctx.result("ok"));

    // Optional: make "/" go to your static index
    app.get("/", ctx -> ctx.redirect("/index.html"));

    // POST: XML -> JSON example
    app.post("/api/xml-to-json", ctx -> {
      String xml = ctx.body();
      JsonNode asTree = xmlMapper.readTree(xml);
      ctx.json(asTree);
    });

    app.start(port);
  }
}
