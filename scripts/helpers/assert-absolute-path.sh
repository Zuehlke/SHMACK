#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <path>"
    exit 1
fi

PATH_TO_CHECK="$1"

if [[ "${PATH_TO_CHECK}" =~ ^[^/].* ]]; then
    echo "Must be an absolute path (start with '/'): ${PATH_TO_CHECK}"
    exit 1
fi


