#/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <path-to-cloud-formation-output.json>"
    exit 1
fi

`dirname $0`/get-single-json-value.sh StackId $1 || exit 1


