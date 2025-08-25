
DEVNAME = sciplat-lab

NAME = images.canfar.net/lsst/$(DEVNAME)
VERSION = latest_release
DATE := $(shell date '+%Y%m%d')

build: dependencies Dockerfile
	docker build --build-arg VERSION=$(VERSION) -t $(NAME):$(DATE) -f Dockerfile .

push: build
	docker push $(NAME):$(DATE)

dependencies: 

init:
	mkdir -p build

.PHONY: clean
clean:
	\rm -rf build
