#!/bin/bash

.  /opt/lsst/software/stack/loadLSST.bash
setup lsst_distrib
setup -r .
scons -j 8
