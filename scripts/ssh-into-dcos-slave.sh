#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <0-based index of slave>"
    exit 1
fi

SLAVE_INDEX="$1"

SLAVE_MESOS_ID=`dcos node | grep --only-matching --word-regexp "[0123456789abcdef-]*S${SLAVE_INDEX}"`
run dcos node ssh --master-proxy --mesos-id=${SLAVE_MESOS_ID}