# Globus Connect Personal for CANFAR/Skaha

Docker image enabling Globus file transfers on the CANFAR/Skaha platform using the official Globus CLI workflow.

## Features

- **Globus CLI** pre-installed for authentication and endpoint management
- **Globus Connect Personal** for file transfers
- **Interactive wizard** for guided setup
- **Helper commands** for easy management
- **Web terminal** (ttyd) for browser-based access
- `/arc` directory configured for read/write access (CANFAR home, projects, share)

## Quick Start

### Inside the Container

Run the interactive wizard:
```bash
globus-wizard
```

This will guide you through:
1. `globus login` - Authenticate with Globus (opens URL for browser auth)
2. `globus gcp create mapped <name>` - Create endpoint, get setup key
3. Configure and start the service

### Manual Setup

```bash
# Step 1: Login to Globus
globus-login
# Follow the URL, authenticate, paste the code back

# Step 2: Create a personal endpoint
globus-create-endpoint my-canfar-endpoint
# This outputs: Endpoint ID and Setup Key

# Step 3: Configure with the setup key
globus-configure <SETUP_KEY>

# Step 4: Start Globus
globus-start
```

## Available Commands

| Command | Description |
|---------|-------------|
| `globus-wizard` | Interactive setup wizard (recommended) |
| `globus-login` | Authenticate with Globus |
| `globus-create-endpoint <name>` | Create personal endpoint |
| `globus-configure <KEY>` | Configure with setup key |
| `globus-start` | Start Globus Connect Personal |
| `globus-stop` | Stop Globus Connect Personal |
| `globus-status` | Check connection status |
| `globus-endpoint-id` | Show endpoint UUID |
| `globus-help` | Show all commands |

### Additional Globus CLI Commands

```bash
globus whoami                      # Show logged-in user
globus endpoint search <query>     # Search for endpoints
globus ls <endpoint_id>:<path>     # List files on an endpoint
globus transfer <src> <dst>        # Initiate a transfer
```

## Local Testing

```bash
# Build the image
docker build -t globus-ft:latest .

# Run interactively
docker run -it --rm globus-ft:latest

# With persistent home directory
docker run -it --rm \
  -v $HOME/globus-home:/arc/home/testuser \
  -e HOME=/arc/home/testuser \
  globus-ft:latest
```

## CANFAR/Skaha Deployment

### Build and Push

```bash
# Build
docker build -t globus-ft:latest .

# Tag for CANFAR Harbor
docker tag globus-ft:latest images.canfar.net/<your-project>/globus-ft:1.0.0

# Login to Harbor
docker login images.canfar.net

# Push
docker push images.canfar.net/<your-project>/globus-ft:1.0.0
```

### Run on CANFAR

This is a **contributed session** that runs a web terminal on port 5000.

```bash
# Using curl to create a contributed session
curl -X POST "https://ws-uv.canfar.net/skaha/v0/session" \
  -H "Authorization: Bearer $SKAHA_TOKEN" \
  -d "name=globus-session" \
  -d "image=images.canfar.net/<your-project>/globus-ft:1.0.0" \
  -d "type=contributed"
```

## Directory Access

By default, `/arc` is configured for read-write access, which includes:
- `/arc/home/<username>` - Your home directory
- `/arc/projects` - Shared project spaces
- `/arc/share` - Shared data

To modify accessible directories, edit `~/.globusonline/lta/config-paths`:

```
# Format: <path>,<sharing_flag>,<R/W_flag>
# sharing: 0=disabled, 1=enabled
# R/W: 0=read-only, 1=read-write

/arc,0,1
```

Restart after changes:
```bash
globus-stop && globus-start
```

## Network Requirements

Globus requires outbound access to:
- TCP 2223 to 54.237.254.192/29 (control channel)
- TCP 50000-51000 to any (data transfer to GCS)
- UDP 32768-65535 to any (peer transfers)
- TCP 443 to any (HTTPS for auth and version check)

## Workflow Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Your Browser                         │
│  1. globus login → opens auth URL                      │
│  2. Authenticate with institution/Globus ID            │
│  3. Copy authorization code back to terminal           │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│              CANFAR Container (globus-ft)               │
│                                                         │
│  $ globus-wizard                                        │
│  $ globus-create-endpoint canfar-session                │
│    → Endpoint ID: xxxxx-xxxx-xxxx                      │
│    → Setup Key: yyyyy-yyyy-yyyy                        │
│  $ globus-configure yyyyy-yyyy-yyyy                     │
│  $ globus-start                                         │
│                                                         │
│  Globus Connect Personal running...                     │
│  /arc directory accessible for transfers               │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│              https://app.globus.org                     │
│                                                         │
│  File Manager:                                          │
│  [Source: University Server] ──► [Dest: canfar-session]│
│  Select files → Start Transfer                         │
│                                                         │
│  Files land in /arc inside the container               │
└─────────────────────────────────────────────────────────┘
```

## Development

### Project Structure

```
globus-ft/
├── Dockerfile           # Container image definition
├── skaha/
│   └── startup.sh       # Entrypoint script (must be executable)
├── .dockerignore        # Files excluded from Docker build
└── README.md
```

### Important: File Permissions

The `skaha/startup.sh` script **must have execute permissions**. If you edit it and the container fails with "Permission denied":

```bash
# Ensure the script is executable locally
chmod +x skaha/startup.sh

# Rebuild with --no-cache to pick up changes
docker build --no-cache -t globus-ft:latest .
```

The Dockerfile sets permissions during build, but Docker caches layers. Use `--no-cache` when the startup script changes.

### Making Changes

After modifying `skaha/startup.sh`:

```bash
# Always rebuild with --no-cache for startup script changes
docker build --no-cache -t images.canfar.net/<project>/globus-ft:<version> .

# Test locally before pushing
docker run -it --rm images.canfar.net/<project>/globus-ft:<version>
```

## Troubleshooting

### "globus login" shows URL but can't open browser
Copy the URL manually and paste into your local browser. After authenticating, paste the authorization code back into the terminal.

### Setup key not working
- Keys expire after a short time - generate a new one
- Ensure the key is copied completely without extra spaces

### Endpoint shows "offline"
- Run `globus-start` to start the service
- Check `globus-status` for errors
- Verify network connectivity

### Files not accessible after transfer
- Check `~/.globusonline/lta/config-paths` includes your directory
- Verify Unix file permissions on the directory

### Container fails with "Permission denied"
- Ensure `skaha/startup.sh` has execute permissions: `chmod +x skaha/startup.sh`
- Rebuild with `--no-cache`: `docker build --no-cache -t <image> .`

## References

- [Globus Connect Personal Linux Installation](https://docs.globus.org/globus-connect-personal/install/linux/)
- [Globus CLI Documentation](https://docs.globus.org/cli/)
- [Guide to setting up Globus Personal Endpoint](https://gist.github.com/wtbarnes/d4383b3b890ab3c3726f4e9e4c7905e7)
