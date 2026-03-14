#!/bin/bash

# bigquery-schema-select installer
# Usage: curl -fsSL https://raw.githubusercontent.com/fpopic/bigquery-schema-select/main/install.sh | bash

set -e

# Colors for "cool" output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}==>${NC} Installing bigquery-schema-select..."

# 1. Dependency check
if ! command -v jq >/dev/null 2>&1; then
    echo -e "${RED}Error:${NC} 'jq' is not installed but is required."
    echo -e "Install it with: ${BLUE}brew install jq${NC}"
    exit 1
fi

# 2. Determine installation directory
# Favor ~/.local/bin if it exists in PATH, otherwise fallback to /usr/local/bin
INSTALL_DIR="$HOME/.local/bin"
if [[ ! ":$PATH:" == *":$INSTALL_DIR:"* ]]; then
    if [[ ":$PATH:" == *":/usr/local/bin:"* ]]; then
        INSTALL_DIR="/usr/local/bin"
    else
        echo -e "${RED}Warning:${NC} Neither ~/.local/bin nor /usr/local/bin are in your PATH."
        INSTALL_DIR="$HOME/.local/bin"
    fi
fi

mkdir -p "$INSTALL_DIR"

# 3. Download the script
REPO_URL="https://raw.githubusercontent.com/fpopic/bigquery-schema-select/main"
SCRIPT_URL="$REPO_URL/bin/bigquery-schema-select"
DEST="$INSTALL_DIR/bigquery-schema-select"

echo -e "${BLUE}==>${NC} Downloading to $DEST..."
if ! curl -sSL "$SCRIPT_URL" -o "$DEST"; then
    echo -e "${RED}Error:${NC} Failed to download script. Check your internet connection or the URL."
    exit 1
fi

chmod +x "$DEST"

# 4. Success message
echo -e "${GREEN}SUCCESS!${NC} bigquery-schema-select is now installed."
echo -e ""
echo -e "Usage example:"
echo -e "  ${BLUE}bq show --schema --format=prettyjson my_project:my_dataset.my_table | bigquery-schema-select${NC}"
echo -e ""
