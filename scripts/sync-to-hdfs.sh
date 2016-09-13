#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <src> <dest>"
    exit 1
fi

SRC="$1"
DEST="$2"

run assert-absolute-path.sh "${SRC}" 
run assert-absolute-path.sh "${DEST}" 

echo "Remote Source        : $SRC"
echo "Local Destination    : $DEST"


HDFS_PATH="hdfs://hdfs${DEST}"
run run-on-dcos-master.sh hadoop fs -rm -f -r "${HDFS_PATH}"

run copy-to-hdfs.sh "$SRC" "$DEST"
