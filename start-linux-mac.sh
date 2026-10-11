#!/usr/bin/env bash
# Routines AI - Linux & Apple macOS Desktop Launcher
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR/desktop" || exit 1

echo "========================================================"
echo "  Routines AI - Genesis OS Family Kiosk (Linux & macOS) "
echo "========================================================"
echo ""

if [ -f "node_modules/.bin/electron" ]; then
    echo "Launching Electron Desktop Application..."
    ./node_modules/.bin/electron .
elif command -v node >/dev/null 2>&1; then
    echo "Launching local kiosk runner on Node.js..."
    node server.js --open
else
    echo "Error: Node.js was not detected. Please install Node.js (https://nodejs.org)."
    exit 1
fi
