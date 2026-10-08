DEVNAME = sciplat-lab

NAME = images.canfar.net/lsst/$(DEVNAME)
VERSION = latest_daily
DATE := $(shell date '+%Y%m%d')

LSST_BASE_REPO = lsstsqre/sciplat-lab
# Digest-pin the floating tag so probe and FROM always use the same image.
BASE_DIGEST ?= $(shell docker buildx imagetools inspect $(LSST_BASE_REPO):$(VERSION) --raw | python3 -c 'import sys,json;d=json.load(sys.stdin);ms=d.get("manifests")or[];print(next(m["digest"] for m in ms if m.get("platform")=={"architecture":"amd64","os":"linux"}))')
BASE_IMAGE ?= $(LSST_BASE_REPO)@$(BASE_DIGEST)

# CADC-patched packages. Forks are ijiraq/lsst_<name>. Install order is
# Dockerfile order only. To add a package: append here + ARG/RUN in Dockerfile.
CADC_PACKAGES = resources daf_butler
CADC_KIND_resources = Required
CADC_KIND_daf_butler = Optional
pkg_ver_arg = $(shell echo $(1) | tr '[:lower:]' '[:upper:]')_VER

# Per-package EUPS pin from lsst_distrib.table (setupRequired/setupOptional).
# Used only as a Docker cache key; the install script always reads the pin
# from the image being built. Do not recurse through $(MAKE) here — a nested
# make drops command-line VERSION and falls back to latest_daily.
probe_eups = docker run --rm -u 0 -e SHELL=bash -e HOME=/tmp --entrypoint bash $(BASE_IMAGE) -c 'source /opt/lsst/software/stack/loadLSST.bash >/dev/null 2>&1 && setup lsst_distrib >/dev/null 2>&1 && TABLE=$$(eups list -s -d lsst_distrib)/ups/lsst_distrib.table && awk "/setup$(2)\\($(1)/ { gsub(/)/, \"\", \$$NF); print \$$NF; exit }" "$$TABLE"'

RESOURCES_VER ?= $(shell $(call probe_eups,resources,$(CADC_KIND_resources)))
DAF_BUTLER_VER ?= $(shell $(call probe_eups,daf_butler,$(CADC_KIND_daf_butler)))

BUILD_ARGS = \
	--build-arg BASE_IMAGE=$(BASE_IMAGE) \
	$(foreach p,$(CADC_PACKAGES),--build-arg $(call pkg_ver_arg,$(p))=$($(call pkg_ver_arg,$(p))))

CACHE_FROM ?=
CACHE_TO ?=
CACHE_FLAGS =
ifneq ($(CACHE_FROM),)
CACHE_FLAGS += --cache-from=$(CACHE_FROM)
endif
ifneq ($(CACHE_TO),)
CACHE_FLAGS += --cache-to=$(CACHE_TO)
endif

METADATA_FILE ?=
METADATA_FLAGS =
ifneq ($(METADATA_FILE),)
METADATA_FLAGS += --metadata-file=$(METADATA_FILE)
endif

BUILDX_TAGS = \
	-t $(NAME):$(DATE) \
	-t $(NAME):$(VERSION)

BUILDX_BASE = docker buildx build \
	--platform linux/amd64 \
	--pull \
	$(BUILD_ARGS) \
	$(CACHE_FLAGS) \
	$(BUILDX_TAGS) \
	-f Dockerfile .

build: dependencies Dockerfile
	@echo "BASE_IMAGE=$(BASE_IMAGE)"
	@echo "RESOURCES_VER=$(RESOURCES_VER)"
	@echo "DAF_BUTLER_VER=$(DAF_BUTLER_VER)"
	$(BUILDX_BASE) --target deploy --load

# BuildKit cannot attach an SPDX SBOM larger than 40MiB; the LSST stack's
# inventory exceeds that. Provenance stays here; GitHub attest + cosign
# still run in the workflow.
deploy: dependencies Dockerfile
	@echo "BASE_IMAGE=$(BASE_IMAGE)"
	@echo "RESOURCES_VER=$(RESOURCES_VER)"
	@echo "DAF_BUTLER_VER=$(DAF_BUTLER_VER)"
	$(BUILDX_BASE) \
		--target deploy \
		--provenance=mode=max \
		$(METADATA_FLAGS) \
		--push

dev: dependencies Dockerfile
	@echo "BASE_IMAGE=$(BASE_IMAGE)"
	$(BUILDX_BASE) --target dev --load

# Manual helpers: make resources-ver VERSION=latest_release
resources-ver:
	@$(call probe_eups,resources,$(CADC_KIND_resources))

daf-butler-ver:
	@$(call probe_eups,daf_butler,$(CADC_KIND_daf_butler))

dependencies:

init:
	mkdir -p build

.PHONY: build deploy dev clean resources-ver daf-butler-ver
clean:
	\rm -rf build
