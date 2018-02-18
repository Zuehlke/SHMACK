#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <IP>"
    exit 1
fi

IP="$1"

run dcos node ssh --master-proxy --private-ip=${IP}
