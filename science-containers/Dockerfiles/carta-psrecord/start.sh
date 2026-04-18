#!/bin/bash

dtm=$(date +%Y%m%d%H%M%S)
logdir="${HOME}/.carta_logs"
mkdir -p "${logdir}"

psrecord "carta --no_browser" \
    --log "${logdir}/${dtm}_carta-backend.log" \
    --plot "${logdir}/${dtm}_carta-backend.png" \
    --include-io \
    --interval 1 \
    --include-children
