#!/bin/bash
#
# Build the Oxygen Batch Converter web application.
#
# This script builds the web module using run-mvn.js (which handles
# the Maven proxy for resolving internal oxygenxml dependencies).
#
# Usage:
#   ./build-web.sh              # build the web app
#   ./build-web.sh package      # explicit goal (default)
#   ./build-web.sh clean package
#

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WEB_DIR="$SCRIPT_DIR/web"
JAR_FILE="$WEB_DIR/target/app.jar"

GOALS="${@:-package}"

echo "Building web module..."
cd "$WEB_DIR"
node "$SCRIPT_DIR/run-mvn.js" $GOALS -DskipTests

if [ $? -ne 0 ]; then
  echo "Build failed."
  exit 1
fi

if [ -f "$JAR_FILE" ]; then
  echo ""
  echo "Build successful: $JAR_FILE"
  echo "Run with: java -jar $JAR_FILE"
else
  echo ""
  echo "Build completed but JAR not found at $JAR_FILE"
fi
