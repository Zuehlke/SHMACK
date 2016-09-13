#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <parameter-name> <path-to-stack-description-output-file.json>"
    exit 1
fi


run python ${SCRIPTS_DIR}/helpers/getStackOutputValue.py "$@"


