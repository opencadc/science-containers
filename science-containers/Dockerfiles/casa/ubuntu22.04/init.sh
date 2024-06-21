#!/bin/bash

echo "INIT START"
echo "sourcing admit_start.sh"
source /opt/admit/admit_start.sh
echo "setting up analysisUtils path"
echo 'import sys' > $HOME/.casa/init.py
echo 'sys.path.append("/opt/casa/analysisUtils/analysis_scripts/")' >> $HOME/.casa/init.py
echo 'import analysisUtils as au' >> $HOME/.casa/init.py
echo 'import analysisUtils as AU' >> $HOME/.casa/init.py
echo "INIT DONE"
