#!/bin/bash
# CANFAR Skaha startup script for Globus Connect Personal
# Contributed session - launches web terminal (ttyd) on port 5000
# Auto-starts Globus if already configured

GLOBUS_HOME=/opt/globusconnectpersonal

# Use HOME directly - CANFAR sets it to /arc/home/{username}
USER_HOME="$HOME"
CONFIG_DIR="$USER_HOME/.globusonline/lta"

echo "========================================="
echo "  Globus Connect Personal for CANFAR"
echo "========================================="
echo "Home directory: $USER_HOME"

# Ensure config directory exists
mkdir -p "$CONFIG_DIR"

# Set config-paths to expose CANFAR /arc for read-write access
# Always overwrite to ensure correct path (not /home)
cat > "$CONFIG_DIR/config-paths" << 'PATHS'
/arc,0,1
PATHS
echo "Globus config-paths set to: /arc"

# Check if Globus is already configured (has client-id.txt from previous setup)
GLOBUS_CONFIGURED=false
if [ -f "$CONFIG_DIR/client-id.txt" ]; then
    GLOBUS_CONFIGURED=true
    echo "Globus configuration found - will auto-start"
fi

# Auto-start Globus Connect Personal as a background daemon if configured
if [ "$GLOBUS_CONFIGURED" = true ]; then
    echo "Starting Globus Connect Personal daemon..."
    nohup $GLOBUS_HOME/globusconnectpersonal -start > "$USER_HOME/.globus-daemon.log" 2>&1 &
    GLOBUS_PID=$!
    sleep 2

    # Check if it started successfully
    if $GLOBUS_HOME/globusconnectpersonal -status 2>/dev/null | grep -q "connected"; then
        echo "Globus Connect Personal: RUNNING (PID: $GLOBUS_PID)"
    else
        echo "Globus Connect Personal: Starting... (check globus-status)"
    fi
else
    echo "Globus not configured - run 'globus-wizard' to set up"
fi

echo "========================================="

# Create shell initialization script with Globus helpers
INIT_SCRIPT="$USER_HOME/.globus-init.sh"
cat > "$INIT_SCRIPT" << 'INITEOF'
#!/bin/bash

export GLOBUS_HOME=/opt/globusconnectpersonal
export PATH=$PATH:$GLOBUS_HOME

# Check Globus status on terminal open
_show_globus_status() {
    local status_output
    status_output=$($GLOBUS_HOME/globusconnectpersonal -status 2>/dev/null)

    if echo "$status_output" | grep -q "connected"; then
        echo "┌─────────────────────────────────────────────────┐"
        echo "│  Globus Connect Personal: RUNNING               │"
        echo "└─────────────────────────────────────────────────┘"

        # Try to get endpoint name from globus CLI if available
        if command -v globus &>/dev/null; then
            local endpoint_id endpoint_name
            endpoint_id=$(globus endpoint local-id 2>/dev/null)
            if [ -n "$endpoint_id" ]; then
                endpoint_name=$(globus endpoint show "$endpoint_id" --format text 2>/dev/null | grep "^Display Name:" | cut -d: -f2- | xargs)
                if [ -n "$endpoint_name" ]; then
                    echo "  Collection: $endpoint_name"
                    echo "  ID: $endpoint_id"
                else
                    echo "  Endpoint ID: $endpoint_id"
                fi
            fi
        fi

        echo ""
        echo "  Sharing: /arc (home, projects, share)"
        echo ""
        echo "  ► Open File Manager: https://app.globus.org/file-manager"
        echo ""
        echo "  Commands: globus-status | globus-stop | globus-help"

        # Show globus CLI availability
        if command -v globus &>/dev/null; then
            echo "  Globus CLI: Available (try 'globus whoami')"
        fi
    else
        echo "┌─────────────────────────────────────────────────┐"
        echo "│  Globus Connect Personal: NOT RUNNING           │"
        echo "└─────────────────────────────────────────────────┘"
        echo ""
        if [ -f "$HOME/.globusonline/lta/client-id.txt" ]; then
            echo "  Already configured. Run 'globus-start' to connect."
        else
            echo "  Not configured yet. Run 'globus-wizard' to set up."
        fi
        echo ""
        echo "  Type 'globus-help' for available commands."
    fi
}
_show_globus_status
echo ""

# Step 1: Login to Globus
globus-login() {
    globus login
}

# Step 2: Create a personal endpoint (GCP mapped collection)
globus-create-endpoint() {
    local name="${1:-canfar-endpoint}"
    echo "Creating personal endpoint: $name"
    globus gcp create mapped "$name"
    echo ""
    echo "Copy the Setup Key above and run:"
    echo "  globus-configure <SETUP_KEY>"
}

# Step 3: Configure with setup key
globus-configure() {
    if [ -z "$1" ]; then
        echo "Usage: globus-configure <SETUP_KEY>"
        echo ""
        echo "Get a setup key by running: globus-create-endpoint <name>"
        return 1
    fi
    $GLOBUS_HOME/globusconnectpersonal -setup "$1"
}

# Step 4: Start Globus Connect Personal
globus-start() {
    echo "Starting Globus Connect Personal..."
    nohup $GLOBUS_HOME/globusconnectpersonal -start > ~/.globus-daemon.log 2>&1 &
    sleep 2
    globus-status
}

# Stop Globus Connect Personal
globus-stop() {
    echo "Stopping Globus Connect Personal..."
    $GLOBUS_HOME/globusconnectpersonal -stop
}

# Check status with detailed info
globus-status() {
    local status_output
    status_output=$($GLOBUS_HOME/globusconnectpersonal -status 2>/dev/null)

    echo "─────────────────────────────────────────────────"
    echo "Globus Connect Personal Status"
    echo "─────────────────────────────────────────────────"

    if echo "$status_output" | grep -q "connected"; then
        echo "Status: ● CONNECTED"
    else
        echo "Status: ○ DISCONNECTED"
    fi

    # Show raw status from GCP
    echo ""
    echo "Details:"
    echo "$status_output" | sed 's/^/  /'

    # Show endpoint info if globus CLI available
    if command -v globus &>/dev/null; then
        local endpoint_id endpoint_name
        endpoint_id=$(globus endpoint local-id 2>/dev/null)
        if [ -n "$endpoint_id" ]; then
            echo ""
            echo "Endpoint:"
            endpoint_name=$(globus endpoint show "$endpoint_id" --format text 2>/dev/null | grep "^Display Name:" | cut -d: -f2- | xargs)
            if [ -n "$endpoint_name" ]; then
                echo "  Name: $endpoint_name"
            fi
            echo "  ID:   $endpoint_id"
        fi
    fi

    # Show accessible paths
    echo ""
    echo "Accessible Paths:"
    if [ -f "$HOME/.globusonline/lta/config-paths" ]; then
        cat "$HOME/.globusonline/lta/config-paths" | while read line; do
            path=$(echo "$line" | cut -d, -f1)
            echo "  $path"
        done
    else
        echo "  (not configured)"
    fi

    echo ""
    echo "Web Interface: https://app.globus.org/file-manager"
    echo "─────────────────────────────────────────────────"
}

# Show endpoint ID
globus-endpoint-id() {
    globus endpoint local-id
}

# Full setup wizard
globus-wizard() {
    echo "========================================="
    echo "  Globus Setup Wizard"
    echo "========================================="
    echo ""

    # Check login status
    if globus whoami &>/dev/null; then
        echo "Already logged in as: $(globus whoami)"
    else
        echo "Step 1: Login to Globus"
        echo "-----------------------"
        globus login
        echo ""
    fi

    echo ""
    echo "Step 2: Create Personal Endpoint"
    echo "---------------------------------"
    read -p "Enter endpoint name [canfar-session]: " epname
    epname="${epname:-canfar-session}"

    echo ""
    output=$(globus gcp create mapped "$epname" 2>&1)
    echo "$output"

    # Extract setup key from output
    setup_key=$(echo "$output" | grep "Setup Key:" | awk '{print $3}')

    if [ -n "$setup_key" ]; then
        echo ""
        echo "Step 3: Configuring endpoint..."
        echo "--------------------------------"
        $GLOBUS_HOME/globusconnectpersonal -setup "$setup_key"

        echo ""
        echo "Step 4: Starting Globus..."
        echo "--------------------------"
        globus-start

        echo ""
        echo "========================================="
        echo "  Setup Complete!"
        echo "========================================="
        echo ""
        echo "Your endpoint is now available at:"
        echo "  https://app.globus.org/file-manager"
        echo ""
        echo "Accessible directory: /arc (home, projects, share)"
        echo ""
        echo "Globus will auto-start on next session launch."
        echo ""
    else
        echo "Could not extract setup key. Please run steps manually:"
        echo "  1. globus-create-endpoint <name>"
        echo "  2. globus-configure <SETUP_KEY>"
        echo "  3. globus-start"
    fi
}

# Help
globus-help() {
    cat << 'HELPTEXT'
Globus Connect Personal Commands
=================================

Quick Setup:
  globus-wizard          - Interactive setup wizard (recommended)

Manual Setup Steps:
  1. globus-login                    - Authenticate with Globus
  2. globus-create-endpoint <name>   - Create GCP mapped collection
  3. globus-configure <SETUP_KEY>    - Configure with setup key
  4. globus-start                    - Start the service

Management:
  globus-status          - Check connection status
  globus-stop            - Stop the service
  globus-endpoint-id     - Show endpoint UUID
  globus-help            - Show this help

Globus CLI (additional):
  globus whoami          - Show logged-in user
  globus endpoint search <query>  - Search endpoints
  globus ls <endpoint_id>:<path>  - List files on endpoint

Note: Globus auto-starts on session launch if previously configured.

HELPTEXT
    echo "Accessible directories:"
    cat "$HOME/.globusonline/lta/config-paths" 2>/dev/null || echo "  (not configured yet)"
    echo ""
}

# Export functions
export -f globus-login globus-create-endpoint globus-configure globus-start globus-stop globus-status globus-endpoint-id globus-wizard globus-help
INITEOF

chmod +x "$INIT_SCRIPT"

# Launch ttyd web terminal on port 5000
# -W: Writable (allow input)
# -p 5000: Port 5000 (required by CANFAR contributed sessions)
# -t fontSize=16: Larger font
# -t theme: Dark theme
# bash with init script sourced
echo ""
echo "Starting web terminal on port 5000..."
exec ttyd -W -p 5000 \
    -t fontSize=16 \
    -t 'theme={"background":"#1e1e1e","foreground":"#d4d4d4"}' \
    bash --rcfile "$INIT_SCRIPT"
