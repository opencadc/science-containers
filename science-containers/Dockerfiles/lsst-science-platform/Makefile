
DEVNAME = sciplat-lab

NAME = images.canfar.net/lsst/$(DEVNAME)
VERSION = latest_release
MINOR = 

build: dependencies Dockerfile
	docker build --build-arg VERSION=$(VERSION) -t $(NAME):$(VERSION)$(MINOR) -f Dockerfile .

push: build
	docker push $(NAME):$(VERSION)$(MINOR)

dependencies: 

init:
	mkdir -p build

.PHONY: clean
clean:
	\rm -rf build
