
DEVNAME = sciplat-lab

NAME = images.canfar.net/lsst/$(DEVNAME)
VERSION = latest_daily
DATE := $(shell date '+%Y%m%d')

deploy: dependencies Dockerfile
	docker build --target deploy --build-arg VERSION=$(VERSION) -t $(NAME):$(DATE) -f Dockerfile .
	docker push $(NAME):$(DATE)

dev: dependencies Dockerfile
	docker build --target dev --build-arg VERSION=$(VERSION) -t $(NAME):$(DATE) -f Dockerfile .

dependencies: 

init:
	mkdir -p build

.PHONY: clean
clean:
	\rm -rf build
