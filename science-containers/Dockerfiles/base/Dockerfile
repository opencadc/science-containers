ARG UBUNTU_VERSION=22.04
ARG PYTHON_VERSION=3.13

FROM ubuntu:${UBUNTU_VERSION} as OS

LABEL maintainer="CANFAR Project <support@canfar.net>"

USER root

WORKDIR /tmp

# Set timezone and locale for headless environments
ENV TZ=PST
ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8
ENV TERM=xterm-256color

RUN apt-get update --yes --quiet --fix-missing \
    && apt-get install --yes --quiet acl \
        bzip2 \
        curl \
        git \
        locales \
        nano \
        rsync \
        openssh-client \
        sudo \
        tzdata \
        vim \
        wget \
        zsh \
        tcsh \
    && apt-get clean --yes \
    && apt-get autoremove --purge --quiet --yes \
    && rm -rf /var/lib/apt/lists/* /var/tmp/* \
    && ln -fs /usr/share/zoneinfo/${TZ} /etc/localtime \
    && dpkg-reconfigure --frontend noninteractive tzdata \
    && locale-gen en_US.UTF-8

FROM OS as python

ADD nsswitch.conf /etc/

# Mircomamba Installation
ENV CONDA_DIR=/opt/conda
ENV PATH="${CONDA_DIR}/bin:${PATH}"
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

COPY condarc "${CONDA_DIR}/.condarc"
RUN set -x \
    && curl -Ls https://micromamba.snakepit.net/api/micromamba/linux-64/latest | tar -xvj bin/micromamba \
    && ./bin/micromamba install \
        --root-prefix="${CONDA_DIR}" \
        --prefix="${CONDA_DIR}" \
        --yes \
        python=${PYTHON_VERSION} mamba

# conda profile scripts by default
RUN ln -s ${CONDA_DIR}/etc/profile.d/conda.sh /etc/profile.d/conda.sh

RUN mamba update --all --yes \
    && mamba install \
    conda \
    pip \
    conda-libmamba-solver \
    pip-tools \
    && mamba clean --all --quiet --yes