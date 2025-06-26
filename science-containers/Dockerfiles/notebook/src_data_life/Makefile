
DEVNAME = datatest

NAME = images.canfar.net/skaha/$(DEVNAME)
BASE = jupyter/scipy-notebook
VERSION = x86_64-latest
DATE := $(shell date '+%Y%m%d')

build: dependencies Dockerfile
	docker build --build-arg VERSION=$(VERSION) -t $(NAME):$(VERSION) -f Dockerfile .

push: build
	docker push $(NAME):$(VERSION)

dependencies: 

init:
	mkdir -p build

.PHONY: clean
clean:
	\rm -rf build
