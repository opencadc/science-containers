# copying after the CASA versions, probably overkill here

VERSIONS = \
	galario-1.2.2

DOCKER_REPO_BASE=images.canfar.net/radio-submm/galario

.PHONY: build clean run 

build: 
	@- $(foreach V,$(VERSIONS), \
                docker build --build-arg RELEASE=$(V) -t ${DOCKER_REPO_BASE}:$(V) .; \
	)

clean:
	@- $(foreach V,$(VERSIONS), \
                docker rmi ${DOCKER_REPO_BASE}:$(V) ; \
	)

upload: build
	@- $(foreach V,$(VERSIONS), \
                docker push ${DOCKER_REPO_BASE}:$(V) ; \
	)

