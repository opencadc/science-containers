# NB: before using, uncomment the version to be built
# NB2: One manual edit is also required in the extract_casaviewer.sh script,
# about halfway down, to again select the command associated with setting up
# plotms for the desired version of CASA


VERSIONS_PIPELINE = \
#	casa-6.1.1-15-pipeline-2020.1.0.40

#!NB >casa6.5.6 versions have a different directory naming structure again
VERSIONS = \
	casa-6.6.3-22-py3.8.el7 
#	casa-6.6.0-20-py3.8.el7
#	casa-6.5.6-22-py3.8.el7

DOCKER_REPO_BASE=images.canfar.net/casa-6/casa

.PHONY: build clean run 

all: build-pipeline build

build-pipeline: 
	@- $(foreach V,$(VERSIONS_PIPELINE), \
                ./download.sh $(V) pipeline ; \
                docker build --build-arg CASA_RELEASE=$(V) -t ${DOCKER_REPO_BASE}:$(V) .; \
	)

build: 
	@- $(foreach V,$(VERSIONS), \
                ./download.sh $(V) current ; \
                docker build --build-arg CASA_RELEASE=$(V) -t ${DOCKER_REPO_BASE}:$(V) .; \
	)

clean-pipeline:
	@- $(foreach V,$(VERSIONS_PIPELINE), \
                docker rmi ${DOCKER_REPO_BASE}:$(V) ; \
	)

clean:
	@- $(foreach V,$(VERSIONS), \
                docker rmi ${DOCKER_REPO_BASE}:$(V) ; \
	)

clean-all: clean-old clean-pipeline clean 

upload-pipeline: build-pipeline
	@- $(foreach V,$(VERSIONS_PIPELINE), \
                docker push ${DOCKER_REPO_BASE}:$(V) ; \
	)

upload: build
	@- $(foreach V,$(VERSIONS), \
                docker push ${DOCKER_REPO_BASE}:$(V) ; \
	)

upload-all: upload-pipeline upload 
