#/bin/bash

if [ "$#" -ne 3 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <path-to-node-info-file.json> <0-based slave index> <attribute>"
    exit 1
fi


run python ${SCRIPTS_DIR}/helpers/getSlaveAttribute.py "$@"


