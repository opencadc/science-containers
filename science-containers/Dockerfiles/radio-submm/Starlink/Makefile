
REPO = images.canfar.net
PROJECT = radio-submm
DEVNAME = starlink
VERSION = 2023A

NAME = $(REPO)/$(PROJECT)/$(DEVNAME)

production: dependencies Dockerfile
	docker build --target deploy -t $(NAME):$(VERSION) -f Dockerfile .

deploy: production
	docker push $(NAME):$(VERSION)

dev: dependencies Dockerfile
	docker build --target test -t $(NAME):$(VERSION) -f Dockerfile .

dependencies: 

init:
	mkdir -p build

.PHONY: clean
clean:
	\rm -rf build
