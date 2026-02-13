#!/bin/bash
#
# Build script for Oxygen Batch Converter CLI.
#
# Several dependencies are not published on Maven Central and must be
# extracted from the official Oxygen Batch Converter addon distribution.
# This script automates that process:
#
#   1. Downloads the addon plugin JAR from oxygenxml.com
#   2. Extracts the bundled library JARs
#   3. Installs them into the local Maven repository
#   4. Downloads oxygen-basic-utilities from the oxygenxml Maven repo
#   5. Builds the CLI shaded JAR
#
# Prerequisites: Java 17+, Maven 3.8+, curl
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OXYGEN_VERSION="28.0-SNAPSHOT"
PLUGIN_VERSION="6.1.0"
PLUGIN_URL="https://www.oxygenxml.com/InstData/Addons/default/com/oxygenxml/oxygen-batch-converter-addon/${PLUGIN_VERSION}/oxygen-batch-converter-addon-${PLUGIN_VERSION}-plugin.jar"
OXYGENXML_REPO="https://www.oxygenxml.com/maven"
OXYGENXML_RELEASE="28.0.0.3"

WORK_DIR=$(mktemp -d)
trap 'rm -rf "$WORK_DIR"' EXIT

echo "=== Oxygen Batch Converter CLI - Build ==="
echo

# ---------------------------------------------------------------
# Step 1: Download the addon plugin JAR
# ---------------------------------------------------------------
PLUGIN_JAR="$WORK_DIR/plugin.jar"
echo "Downloading addon plugin JAR..."
curl -fSL "$PLUGIN_URL" -o "$PLUGIN_JAR"
echo "  Downloaded $(du -h "$PLUGIN_JAR" | cut -f1) plugin JAR."

# ---------------------------------------------------------------
# Step 2: Extract the bundled library JARs
# ---------------------------------------------------------------
echo "Extracting library JARs..."
(cd "$WORK_DIR" && jar xf "$PLUGIN_JAR" "oxygen-batch-converter-addon-${PLUGIN_VERSION}/lib/")
LIB_DIR="$WORK_DIR/oxygen-batch-converter-addon-${PLUGIN_VERSION}/lib"

# ---------------------------------------------------------------
# Step 3: Install oxygenxml JARs into the local Maven repository
# ---------------------------------------------------------------

install_jar() {
  local file="$1" groupId="$2" artifactId="$3" version="$4"
  echo "  Installing ${artifactId}:${version}"
  mvn -q install:install-file \
    -Dfile="$file" \
    -DgroupId="$groupId" \
    -DartifactId="$artifactId" \
    -Dversion="$version" \
    -Dpackaging=jar \
    -DgeneratePom=true
}

echo "Installing oxygenxml dependencies into local Maven repository..."
# Run installs from WORK_DIR so Maven doesn't try to resolve the root pom.xml
# (which references a parent POM not available outside the oxygenxml build).
pushd "$WORK_DIR" > /dev/null

# From the plugin distribution
install_jar "$LIB_DIR/oxygen-batch-converter-core-${OXYGEN_VERSION}.jar" \
  com.oxygenxml oxygen-batch-converter-core "$OXYGEN_VERSION"

install_jar "$LIB_DIR/oxygen-patched-mammoth-for-batch-converter-${OXYGEN_VERSION}.jar" \
  com.oxygenxml oxygen-patched-mammoth-for-batch-converter "$OXYGEN_VERSION"

install_jar "$LIB_DIR/oxygen-patched-jtidy-for-batch-converter-${OXYGEN_VERSION}.jar" \
  com.oxygenxml oxygen-patched-jtidy-for-batch-converter "$OXYGEN_VERSION"

install_jar "$LIB_DIR/oxygen-patched-lwdita-for-batch-converter-${OXYGEN_VERSION}.jar" \
  com.oxygenxml oxygen-patched-lwdita-for-batch-converter "$OXYGEN_VERSION"

install_jar "$LIB_DIR/oxygen-yaml-validator-and-converter-${OXYGEN_VERSION}.jar" \
  com.oxygenxml oxygen-yaml-validator-and-converter "$OXYGEN_VERSION"

install_jar "$LIB_DIR/oxygen-patched-jaxb-core-1.0.0-SNAPSHOT.jar" \
  com.oxygenxml oxygen-patched-jaxb-core "1.0.0-SNAPSHOT"

install_jar "$LIB_DIR/oxygen-patched-jsonix-schema-compiler-1.2.0-SNAPSHOT.jar" \
  com.oxygenxml oxygen-patched-jsonix-schema-compiler "1.2.0-SNAPSHOT"

install_jar "$LIB_DIR/dost-4.3.0.jar" \
  org.dita-ot dost "4.3.0"

# From the oxygenxml Maven repository
echo "  Downloading oxygen-basic-utilities from oxygenxml repo..."
curl -fSL "${OXYGENXML_REPO}/com/oxygenxml/oxygen-basic-utilities/${OXYGENXML_RELEASE}/oxygen-basic-utilities-${OXYGENXML_RELEASE}.jar" \
  -o "$WORK_DIR/oxygen-basic-utilities.jar"
install_jar "$WORK_DIR/oxygen-basic-utilities.jar" \
  com.oxygenxml oxygen-basic-utilities "$OXYGEN_VERSION"

# oxygen-patched-json is not publicly available; install an empty placeholder.
# (Only needed to satisfy the Maven dependency; the standard org.json library
# included in the POM provides the actual classes at runtime.)
echo "  Creating oxygen-patched-json placeholder..."
mkdir -p "$WORK_DIR/empty-jar/META-INF"
echo "Manifest-Version: 1.0" > "$WORK_DIR/empty-jar/META-INF/MANIFEST.MF"
(cd "$WORK_DIR" && jar cf empty-placeholder.jar -C empty-jar .)
install_jar "$WORK_DIR/empty-placeholder.jar" \
  com.oxygenxml oxygen-patched-json "$OXYGEN_VERSION"

popd > /dev/null

# ---------------------------------------------------------------
# Step 4: Build the CLI shaded JAR
# ---------------------------------------------------------------
echo
echo "Building CLI shaded JAR..."
mvn -f "$SCRIPT_DIR/cli/pom.xml" package -DskipTests
echo
echo "=== Build complete ==="
echo "Shaded JAR: cli/target/oxygen-batch-converter-cli-6.2.0-SNAPSHOT.jar"
echo "Run:        ./batch-converter --help"
