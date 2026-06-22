#!/bin/bash

# Configuration
APP_NAME="techmart-ecommerce-1.0-SNAPSHOT" # Must end in .war even for exploded
DEPLOYMENT_DIR="target/$APP_NAME"  # The directory to deploy (exploded)

# Check if .env file exists
if [ -f ".env" ]; then
    # Enable automatic export of all variables
    set -a
    # Source the .env file
    source .env
    # Disable automatic export
    set +a
else
    echo "Error: .env file not found." >&2
    exit 1
fi

CLI_USER=$WILDFLY_CLI_USERNAME
CLI_PASS=$WILDFLY_CLI_PASSWORD
CLI_PATH=$WILDFLY_CLI_PATH

# --- Resolve WildFly CLI Path ---
CLI_BIN=($CLI_PATH)
if [ -z "${CLI_BIN[0]}" ]; then
    echo "❌ WildFly CLI not found."
    exit 1
fi
CLI_BIN="${CLI_BIN[0]}"

echo "🚀 Building Exploded Web Structure..."
# CRITICAL CHANGE: Use 'war:exploded' instead of 'compile'
# This creates target/techmart-.../WEB-INF/classes, lib, web.xml, etc.
mvn clean package war:exploded -q -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed."
    exit 1
fi

# Verify structure exists
if [ ! -d "$DEPLOYMENT_DIR/WEB-INF" ]; then
    echo "❌ Error: WEB-INF not found in $DEPLOYMENT_DIR. Build failed."
    exit 1
fi

echo "📡 Connecting to WildFly..."

# --- STEP 1: UNDEPLOY ---
echo "🛑 Stopping existing deployment..."
$CLI_BIN --connect --controller=localhost:9990 --user=$CLI_USER --password=$CLI_PASS <<EOF
/deployment=$APP_NAME:undeploy()
EOF

# --- STEP 2: REMOVE ---
echo "🧹 Removing old deployment definition..."
$CLI_BIN --connect --controller=localhost:9990 --user=$CLI_USER --password=$CLI_PASS <<EOF
/deployment=$APP_NAME:remove()
EOF

# --- STEP 3: ADD NEW ---
echo "✨ Creating new exploded deployment..."
$CLI_BIN --connect --controller=localhost:9990 --user=$CLI_USER --password=$CLI_PASS <<EOF
/deployment=$APP_NAME:add(content=[{path="$PWD/$DEPLOYMENT_DIR", archive=false}], enabled=true, runtime-name="$APP_NAME.war")
EOF

if [ $? -eq 0 ]; then
    echo "✅ Deployment successful!"
    echo "🌐 Access at: http://localhost:8080/$APP_NAME/hello-servlet"
else
    echo "❌ Deployment failed."
    exit 1
fi