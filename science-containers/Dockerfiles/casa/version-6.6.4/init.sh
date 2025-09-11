#!/bin/bash

echo "INIT START"
echo "sourcing admit_start.sh"
source /opt/admit/admit_start.sh
echo "setting up analysisUtils path"
echo 'import sys' > $HOME/.casa/startup.py
echo 'sys.path.append("/opt/casa/analysisUtils/analysis_scripts/")' >> $HOME/.casa/startup.py
echo 'import analysisUtils as au' >> $HOME/.casa/startup.py
echo 'import analysisUtils as AU' >> $HOME/.casa/startup.py
echo "INIT DONE"
echo "INIT DONE"
