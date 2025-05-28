# CANFAR Documentation Restructuring Proposal

## 🎯 New Navigation Structure

### 1. **Quick Start** (5-10 minutes to first session)
```
quick-start/
├── get-access.md           # Account setup & project joining  
├── first-session.md        # Launch your first notebook/desktop
└── essential-concepts.md   # Storage, containers, sessions basics
```

### 2. **Core Concepts** (Essential understanding)
```
concepts/
├── storage-systems.md      # 🎯 NEW: Comprehensive storage guide
├── container-basics.md     # What containers are, how they work
├── session-types.md        # Notebook vs Desktop vs CARTA vs Headless
└── project-collaboration.md # Groups, permissions, sharing
```

### 3. **Data Management** (All transfer methods in one place)  
```
data/
├── overview.md            # 🎯 NEW: Storage comparison (vault/arc/scratch)
├── web-interface.md       # Browser upload/download
├── command-line.md        # 🎯 NEW: sshfs, vostools, curl comprehensive guide
├── notebook-transfers.md  # Small file uploads in notebooks
└── large-datasets.md      # Best practices for TB+ data
```

### 4. **Interactive Sessions** (User interfaces)
```
sessions/
├── notebooks/
│   ├── getting-started.md  # Jupyter basics
│   ├── data-analysis.md    # Python workflows 
│   └── customization.md    # Extensions, kernels
├── desktop/
│   ├── getting-started.md  # VNC desktop basics
│   ├── applications.md     # CASA, DS9, SAO Image, etc.
│   └── workflows.md        # ALMA reduction examples
└── carta/
    ├── getting-started.md  # CARTA radio astronomy viewer
    └── advanced-features.md # Scripting, analysis
```

### 5. **Container Development** (Build your own)
```
containers/
├── basics.md              # 🎯 NEW: Container fundamentals
├── types/
│   ├── notebook.md        # 🎯 NEW: Jupyter container requirements
│   ├── desktop-app.md     # 🎯 NEW: GUI application containers  
│   ├── headless.md        # 🎯 NEW: Command-line/batch containers
│   └── contributed.md     # Custom web applications
├── building.md            # Local development, testing
├── publishing.md          # Push to CANFAR registry
└── examples/              # Real container examples
    ├── astronomy-tools.md
    ├── machine-learning.md
    └── data-processing.md
```

### 6. **Batch & Automation** (Headless processing)
```
automation/
├── headless-overview.md   # 🎯 NEW: When to use headless containers
├── api-access.md          # 🎯 NEW: curl commands, authentication
├── python-client.md       # 🎯 NEW: skaha Python client guide
├── batch-workflows.md     # Large-scale processing patterns
└── monitoring.md          # Job status, logs, debugging
```

### 7. **Administration** (Permissions & management)
```
admin/
├── groups-permissions.md  # User/group management
├── storage-quotas.md      # Limits, monitoring usage
├── troubleshooting.md     # Common issues, solutions
└── resource-requests.md   # Getting more storage/compute
```

### 8. **Reference** (Quick lookup)
```
reference/
├── faq.md                 # Frequently asked questions
├── api-docs.md            # Complete API reference
├── container-catalog.md   # Available containers
├── storage-paths.md       # File system layout
└── support.md             # Contact info, community
```

## 🎯 Key New Content Requirements

### Storage Systems Comparison (data/overview.md)
```markdown
# Storage Systems on CANFAR

## Storage Types Comparison

| Storage | Path | Purpose | Speed | Persistence | Backup | Quota |
|---------|------|---------|-------|-------------|--------|-------|
| **ARC** | `/arc/projects/` | Active project data | Fast | ✅ Persistent | ✅ Daily | Project-based |
| **ARC Home** | `/arc/home/` | Personal configs | Fast | ✅ Persistent | ✅ Daily | 10GB default |
| **Scratch** | `/scratch/` | Temporary processing | Fastest | ❌ Cleared nightly | ❌ None | No limit |
| **VOSpace** | `vos:` (external) | Long-term archive | Medium | ✅ Permanent | ✅ Geo-redundant | User-based |

## When to Use Each

### `/arc/projects/` - Main Research Data
- ✅ Raw datasets, analysis results
- ✅ Code repositories, workflows  
- ✅ Shared with team members
- ✅ Backed up daily

### `/scratch/` - High-Speed Processing
- ✅ Intermediate files during analysis
- ✅ Large temporary downloads
- ⚠️ Files deleted every night at midnight
- ⚠️ Not suitable for anything important

### VOSpace - Long-term Archive
- ✅ Published datasets
- ✅ Final results for papers
- ✅ Multi-site replication
- ⚠️ Slower access than /arc
```

### Container Building Guide (containers/types/notebook.md)
```markdown
# Building Notebook Containers

## Requirements for Notebook Containers

Your container must:
1. **Base Image**: Start from `images.canfar.net/skaha/notebook:latest`
2. **Jupyter Lab**: Must have `jupyter lab` executable
3. **User Setup**: Run as UID 1000 (skaha user)  
4. **Port 8888**: Jupyter must listen on this port
5. **Base URL**: Support `--NotebookApp.base_url` parameter

## Example Dockerfile

```dockerfile
FROM images.canfar.net/skaha/notebook:latest

# Install Python packages
RUN pip install --no-cache-dir \
    astropy \
    aplpy \
    your-analysis-package

# Install system packages (as root)
USER root
RUN apt-get update && apt-get install -y \
    your-system-dependency \
    && rm -rf /var/lib/apt/lists/*

# Copy notebooks and data
USER skaha
COPY notebooks/ /opt/notebooks/
COPY data/ /opt/data/

# Set working directory
WORKDIR /home/skaha
```

## Testing Locally

```bash
# Build container
docker build -t myproject/analysis:v1.0 .

# Test locally
docker run -p 8888:8888 \
  -v /path/to/data:/arc/projects/myproject \
  myproject/analysis:v1.0
```
```

### Headless Containers & API Guide (automation/api-access.md)
```markdown
# Running Headless Containers

## Authentication Setup

```bash
# Get CADC certificate
cadc-get-cert -u yourusername

# Verify certificate location
ls ~/.ssl/cadcproxy.pem
```

## Launch Headless Session with curl

```bash
# Basic headless job
curl -E ~/.ssl/cadcproxy.pem \
  https://ws-uv.canfar.net/skaha/v0/session \
  -d "name=my-analysis" \
  -d "image=images.canfar.net/skaha/astroconda:latest" \
  -d "kind=headless" \
  --data-urlencode "cmd=python" \
  --data-urlencode "args=/arc/projects/myproject/analysis.py"

# With custom resources
curl -E ~/.ssl/cadcproxy.pem \
  https://ws-uv.canfar.net/skaha/v0/session \
  -d "name=big-analysis" \
  -d "image=images.canfar.net/myproject/processor:latest" \
  -d "kind=headless" \
  -d "cores=4" \
  -d "ram=16" \
  --data-urlencode "cmd=/opt/scripts/process.sh"
```

## Using Python Client

```python
from skaha.session import SessionManager

# Initialize client
session = SessionManager()

# Launch headless job
job_id = session.create(
    name="data-processing",
    image="images.canfar.net/myproject/processor:latest",
    kind="headless",
    cmd="/opt/scripts/process.sh",
    args=["/arc/projects/myproject/input", "/arc/projects/myproject/output"],
    cores=2,
    ram=8
)

# Monitor job
status = session.get_status(job_id)
logs = session.get_logs(job_id)

# Cleanup when done
session.delete(job_id)
```

## Common Patterns

### File Processing Pipeline
```bash
# Process all FITS files in directory
curl -E ~/.ssl/cadcproxy.pem \
  https://ws-uv.canfar.net/skaha/v0/session \
  -d "name=fits-processing" \
  -d "image=images.canfar.net/astro/processor:latest" \
  -d "kind=headless" \
  --data-urlencode "cmd=find" \
  --data-urlencode "args=/arc/projects/myproject/data -name '*.fits' -exec process_fits.py {} \;"
```

### Parameter Sweep
```bash
# Run analysis with different parameters
for param in 0.1 0.5 1.0 2.0; do
  curl -E ~/.ssl/cadcproxy.pem \
    https://ws-uv.canfar.net/skaha/v0/session \
    -d "name=analysis-${param}" \
    -d "image=images.canfar.net/myproject/analysis:latest" \
    -d "kind=headless" \
    --data-urlencode "cmd=python" \
    --data-urlencode "args=/opt/scripts/analyze.py --param ${param}"
done
```
```

## 🔄 Migration Plan

### Phase 1: New Content Creation (Week 1-2)
1. Create missing storage comparison guide
2. Write comprehensive data transfer guide  
3. Build complete container development guide
4. Document headless containers and API usage

### Phase 2: Content Reorganization (Week 3)
1. Restructure navigation in mkdocs.yml
2. Move existing content to new locations
3. Fix all internal links
4. Update index.md with new structure

### Phase 3: Testing & Polish (Week 4)
1. Test all examples and code snippets
2. Add missing screenshots/diagrams
3. Review for consistency and clarity
4. User testing with new/experienced users

## ✅ Benefits of New Structure

1. **Progressive Learning**: Clear path from beginner to expert
2. **Task-Oriented**: Organized by what users want to accomplish  
3. **Complete Coverage**: All your required topics included
4. **Easy Navigation**: Related content grouped together
5. **Maintainable**: Clear ownership of different sections
6. **Searchable**: Better keyword coverage and organization

## 🎯 Immediate Priorities

Based on your requirements, I recommend starting with:

1. **`data/overview.md`** - Storage comparison (vault/arc/scratch)
2. **`data/command-line.md`** - Complete sshfs/vos/curl guide  
3. **`containers/types/`** - All three container types (notebook/desktop/headless)
4. **`automation/api-access.md`** - curl and Python client examples

This will address your immediate missing content while setting up the foundation for the full restructure.
