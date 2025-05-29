# Phase 3: Content Quality Improvements Plan

## Issues Identified

### 1. Critical Terminology Inconsistency: "Science Portal" vs "Science Platform"

**Problem**: The documentation inconsistently uses "Science Portal" and "Science Platform" to refer to the same system.

**Analysis**: 
- **"Science Portal"**: Used 35+ times in docs, typically referring to the web interface
- **"Science Platform"**: Used 22+ times in docs, typically referring to the overall system
- **Mixed usage**: Same documents sometimes use both terms interchangeably

**Proposed Solution**: Standardize terminology as follows:
- **"CANFAR Science Platform"**: The overall system/infrastructure
- **"Science Portal"**: The web interface/UI (https://www.canfar.net/science-portal/)
- **Context-specific usage**: Make it clear when referring to the UI vs the system

### 2. Capitalization Inconsistencies

**Examples found**:
- "science portal" (lowercase) vs "Science Portal" (capitalized)
- "science platform" (lowercase) vs "Science Platform" (capitalized)

### 3. Outdated References

**Issues to investigate**:
- Version references that may be outdated
- Deprecated URLs or endpoints
- Old feature descriptions

### 4. Content Clarity Issues

**Areas needing improvement**:
- Inconsistent voice/tone across documents
- Varying levels of technical detail
- Missing context or explanations

## Implementation Strategy

### Phase 3A: Terminology Standardization
1. Create terminology guide/standards
2. Systematically update all instances to use consistent terms
3. Ensure clarity between "Platform" (system) and "Portal" (web UI)

### Phase 3B: Content Clarity Improvements  
1. Review and improve unclear explanations
2. Standardize writing style and tone
3. Add missing context where needed

### Phase 3C: Currency Check
1. Verify all version references are current
2. Check all external links and references
3. Update outdated screenshots or procedures

## Files Requiring Terminology Updates

Based on grep results, the following files need terminology review:

**High Priority (most instances)**:
- `docs/user-guide/overview.md` - Multiple instances, foundational content
- `docs/user-guide/launch-*.md` - User-facing tutorials
- `docs/help/faq.md` - Frequently referenced
- `docs/reference-material/container-publishing-guide.md` - Technical documentation

**Medium Priority**:
- `docs/user-guide/access.md`
- `docs/user-guide/vospace-tools.md`
- `docs/developer-guide/containers.md`

**Low Priority**:
- Various other files with single instances

## Success Criteria

- ✅ Consistent use of "Science Platform" vs "Science Portal"
- ✅ Standardized capitalization
- ✅ Clear, consistent tone across all documentation
- ✅ Current and accurate information
- ✅ Improved user experience and clarity

---

**Next Steps**: Start with Phase 3A - Terminology Standardization
