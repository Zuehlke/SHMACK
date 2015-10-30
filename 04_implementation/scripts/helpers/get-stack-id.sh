#/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <path-to-cloud-formation-output.json>"
    exit 1
fi


run python ${SCRIPTS_DIR}/helpers/get-single-json-value.py StackId $1 


