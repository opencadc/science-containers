
VERSIONS = \
	22.04


DOCKER_REPO_BASE=images.canfar.net/skaha/ubuntu

.PHONY: build clean run 

all: build

build: 
	@- $(foreach V,$(VERSIONS), \
		docker build -t ${DOCKER_REPO_BASE}:$(V) .; \
    	)

clean:
	@- $(foreach V,$(VERSIONS), \
	  docker rmi ${DOCKER_REPO_BASE}:$(V) ; \
	)


upload: build
	@- $(foreach V,$(VERSIONS), \
	  docker push ${DOCKER_REPO_BASE}:$(V) ; \
	)
clean_all: clean
upload_all: upload
