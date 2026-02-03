#!/bin/bash

set -euo pipefail

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | awk -F '.' '{print $1}')

if [ "$JAVA_VERSION" != "25" ]; then
    echo "Error: Java version 25 is required, but found version $JAVA_VERSION"
    exit 1
fi

echo "Java version 25 detected. Proceeding..."

# Navigate to client directory
cd client || { echo "Error: client directory not found"; exit 1; }

# Run the application
echo "Starting application..."
java -jar target/SC6103.jar "$@"
