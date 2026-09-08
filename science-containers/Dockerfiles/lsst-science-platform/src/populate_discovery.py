#!/usr/bin/env python3
"""Populate Nublado discovery JSON from CADC IVOA resource-caps.

Reads a dataset map, resolves IVOID services via the CADC registry
resource-caps file (or accepts literal service URL entries), and writes
``/etc/nublado/discovery/v1.json`` for ``lsst.rsp.RSPDiscovery``.

Service map values may be:

* an IVOID (``ivo://...``) resolved through resource-caps + VOSI capabilities
* a literal ``https://...`` URL (e.g. Rubin services mirrored into CANFAR discovery)
* a dict with ``url`` and optional ``versions`` (passed through as-is)
"""

from __future__ import annotations

import argparse
import json
import sys
import urllib.error
import urllib.request
import xml.etree.ElementTree as ET
from pathlib import Path
from typing import Any

try:
    import yaml
except ImportError:  # pragma: no cover - sciplat images include PyYAML
    yaml = None

# Shared ARC config (not baked into the image). Synced via Argo CD; see
# arc-projects-LSST/README.md in this Dockerfile tree.
DEFAULT_MAP = Path("/arc/projects/LSST/cadc_dataset_map.yaml")
USER_AGENT = "canfar-populate-discovery/1.0"

# Extra version keys to attach when the matching VOSI capability exists.
SERVICE_VERSION_STANDARDS: dict[str, list[tuple[str, tuple[str, ...]]]] = {
    "tap": [
        (
            "tables",
            (
                "ivo://ivoa.net/std/VOSI#tables-1.1",
                "ivo://ivoa.net/std/VOSI#tables",
            ),
        ),
    ],
    "sia": [
        ("sia-query-2.0", ("ivo://ivoa.net/std/SIA#query-2.0",)),
    ],
    "datalink": [
        (
            "datalink-links-1.1",
            (
                "ivo://ivoa.net/std/DataLink#links-1.1",
                "ivo://ivoa.net/std/DataLink#links-1.0",
            ),
        ),
    ],
    "cutout": [
        ("soda-sync-1.0", ("ivo://ivoa.net/std/SODA#sync-1.0",)),
        ("soda-async-1.0", ("ivo://ivoa.net/std/SODA#async-1.0",)),
    ],
    "gms": [
        (
            "gms-search-1.0",
            (
                "ivo://ivoa.net/std/GMS#search-1.0",
                "ivo://ivoa.net/std/GMS#search-0.1",
            ),
        ),
    ],
}


def _load_map(path: Path) -> dict[str, Any]:
    text = path.read_text(encoding="utf-8")
    if path.suffix.lower() in {".yaml", ".yml"}:
        if yaml is None:
            raise RuntimeError(
                "PyYAML is required to read cadc_dataset_map.yaml; "
                "install pyyaml or provide a JSON map"
            )
        data = yaml.safe_load(text)
    else:
        data = json.loads(text)
    if not isinstance(data, dict):
        raise ValueError(f"dataset map root must be a mapping: {path}")
    return data


def _http_get(url: str, timeout: float = 30.0) -> bytes:
    request = urllib.request.Request(
        url,
        headers={"User-Agent": USER_AGENT, "Accept": "*/*"},
    )
    with urllib.request.urlopen(request, timeout=timeout) as response:
        return response.read()


def parse_resource_caps(text: str) -> dict[str, str]:
    """Parse CADC resource-caps into IVOID → capabilities URL."""
    mapping: dict[str, str] = {}
    for raw_line in text.splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#"):
            continue
        if "=" not in line:
            continue
        ivid, caps_url = line.split("=", 1)
        ivid = ivid.strip()
        caps_url = caps_url.strip()
        if ivid and caps_url:
            mapping[ivid] = caps_url
    return mapping


def _local(tag: str) -> str:
    if "}" in tag:
        return tag.rsplit("}", 1)[-1]
    return tag


def _find_access_url(capability: ET.Element) -> str | None:
    """Return the preferred accessURL for a VOSI capability."""
    interfaces = [c for c in capability if _local(c.tag) == "interface"]
    ordered = sorted(
        interfaces,
        key=lambda el: 0 if el.attrib.get("role") == "std" else 1,
    )
    for interface in ordered:
        for child in interface:
            if _local(child.tag) != "accessURL":
                continue
            url = (child.text or "").strip()
            if url:
                return url
    return None


def parse_capabilities(xml_bytes: bytes) -> dict[str, dict[str, str]]:
    """Map capability standardID → {url}."""
    root = ET.fromstring(xml_bytes)
    by_standard: dict[str, dict[str, str]] = {}
    for capability in root.iter():
        if _local(capability.tag) != "capability":
            continue
        standard_id = capability.attrib.get("standardID")
        if not standard_id:
            continue
        access_url = _find_access_url(capability)
        if access_url:
            by_standard[standard_id] = {"url": access_url}
    return by_standard


def _match_standard(
    capabilities: dict[str, dict[str, str]], candidates: list[str] | tuple[str, ...]
) -> tuple[str, dict[str, str]] | None:
    for candidate in candidates:
        if candidate in capabilities:
            return candidate, capabilities[candidate]
        for standard_id, info in capabilities.items():
            if standard_id == candidate or standard_id.startswith(
                candidate + "#"
            ):
                return standard_id, info
    return None


def resolve_ivoa_service(
    capabilities: dict[str, dict[str, str]],
    service_name: str,
    standard_ids: list[str],
) -> dict[str, Any] | None:
    """Build an RSPDiscovery service entry from VOSI capabilities."""
    matched = _match_standard(capabilities, standard_ids)
    if not matched:
        return None
    _, info = matched
    entry: dict[str, Any] = {"url": info["url"]}

    versions: dict[str, dict[str, str]] = {}
    for version_name, version_ids in SERVICE_VERSION_STANDARDS.get(
        service_name, []
    ):
        version_match = _match_standard(capabilities, version_ids)
        if version_match:
            _, version_info = version_match
            versions[version_name] = {"url": version_info["url"]}
    if versions:
        entry["versions"] = versions

    return entry


def _normalize_literal_service(spec: Any) -> dict[str, Any]:
    """Accept a URL string or {url, versions} dict for non-IVOA entries."""
    if isinstance(spec, str):
        return {"url": spec}
    if isinstance(spec, dict) and "url" in spec:
        entry: dict[str, Any] = {"url": str(spec["url"])}
        if versions := spec.get("versions"):
            entry["versions"] = {
                name: {"url": str(info["url"])}
                if isinstance(info, dict)
                else {"url": str(info)}
                for name, info in versions.items()
            }
        return entry
    raise ValueError(f"invalid literal service spec: {spec!r}")


def resolve_service_spec(
    *,
    dataset: str,
    service_name: str,
    spec: Any,
    resource_caps: dict[str, str],
    standard_map: dict[str, list[str]],
    caps_cache: dict[str, dict[str, dict[str, str]]],
) -> dict[str, Any]:
    """Resolve one service map entry to an RSPDiscovery service object."""
    if isinstance(spec, dict) and "url" in spec:
        return _normalize_literal_service(spec)

    if not isinstance(spec, str):
        raise ValueError(
            f"dataset {dataset!r} service {service_name!r}: "
            f"expected IVOID, URL, or {{url: ...}} dict, got {spec!r}"
        )

    if spec.startswith("http://") or spec.startswith("https://"):
        return _normalize_literal_service(spec)

    if not spec.startswith("ivo://"):
        raise ValueError(
            f"dataset {dataset!r} service {service_name!r}: "
            f"unrecognized service reference {spec!r}"
        )

    if spec not in resource_caps:
        raise KeyError(
            f"dataset {dataset!r} service {service_name!r}: "
            f"IVOID {spec!r} not found in resource-caps"
        )
    caps_url = resource_caps[spec]
    if caps_url not in caps_cache:
        caps_cache[caps_url] = parse_capabilities(_http_get(caps_url))
    capabilities = caps_cache[caps_url]

    standard_ids = standard_map.get(service_name, [])
    if not standard_ids:
        raise KeyError(
            f"no service_standard_ids configured for {service_name!r}"
        )
    entry = resolve_ivoa_service(capabilities, service_name, standard_ids)
    if entry is None:
        raise RuntimeError(
            f"dataset {dataset!r} service {service_name!r}: "
            f"no capability matching {standard_ids} at {caps_url}"
        )
    return entry


def build_discovery(config: dict[str, Any]) -> dict[str, Any]:
    resource_caps_url = config["resource_caps_url"]
    standard_map: dict[str, list[str]] = config.get("service_standard_ids", {})
    datasets_cfg: dict[str, Any] = config.get("datasets", {})

    caps_text = _http_get(resource_caps_url).decode("utf-8", errors="replace")
    resource_caps = parse_resource_caps(caps_text)
    caps_cache: dict[str, dict[str, dict[str, str]]] = {}

    datasets: dict[str, Any] = {}
    for dataset, meta in datasets_cfg.items():
        services_cfg = meta.get("services") or {}
        services: dict[str, Any] = {}
        for service_name, spec in services_cfg.items():
            services[service_name] = resolve_service_spec(
                dataset=dataset,
                service_name=service_name,
                spec=spec,
                resource_caps=resource_caps,
                standard_map=standard_map,
                caps_cache=caps_cache,
            )

        dataset_entry: dict[str, Any] = {}
        if butler := meta.get("butler_config"):
            dataset_entry["butler_config"] = butler
        if services:
            dataset_entry["services"] = services
        datasets[dataset] = dataset_entry

    discovery: dict[str, Any] = {"datasets": datasets, "services": {}}
    if env_name := config.get("environment_name"):
        discovery["environment_name"] = env_name
    return discovery


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--map",
        type=Path,
        default=DEFAULT_MAP,
        help=f"dataset map YAML/JSON (default: {DEFAULT_MAP})",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=None,
        help="override discovery output path from the map file",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="print discovery JSON to stdout instead of writing a file",
    )
    args = parser.parse_args(argv)

    try:
        config = _load_map(args.map)
        discovery = build_discovery(config)
    except FileNotFoundError as exc:
        print(
            f"populate_discovery: dataset map not found: {exc.filename}\n"
            "  Expected /arc/projects/LSST/cadc_dataset_map.yaml "
            "(deploy via Argo CD; see arc-projects-LSST/README.md).",
            file=sys.stderr,
        )
        return 1
    except (
        OSError,
        ValueError,
        KeyError,
        RuntimeError,
        urllib.error.URLError,
        ET.ParseError,
    ) as exc:
        print(f"populate_discovery: {exc}", file=sys.stderr)
        return 1

    payload = json.dumps(discovery, indent=2, sort_keys=True) + "\n"
    if args.dry_run:
        sys.stdout.write(payload)
        return 0

    output = args.output or Path(
        config.get("discovery_path", "/etc/nublado/discovery/v1.json")
    )
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(payload, encoding="utf-8")
    print(f"populate_discovery: wrote {output}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
