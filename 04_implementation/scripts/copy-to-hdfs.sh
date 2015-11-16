#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <src> <dest>"
    exit 1
fi

SRC="$1"
DEST="$2"

if [[ "$DEST" =~ ^[^/].* ]]; then
    echo "DEST (second argument) must start with /"
    exit 1
fi

echo "Remote Source        : $SRC"
echo "Local Destination    : $DEST"


HDFS_PATH="hdfs://hdfs${DEST}"

INTERMEDIATE_DIR="/tmp/hdfs-xchange/to-hdfs/${DEST}"
run sync-to-dcos-master.sh "${SRC}/" "${INTERMEDIATE_DIR}/"
run run-on-dcos-master.sh hadoop fs -mkdir -p "${HDFS_PATH}/"
run run-on-dcos-master.sh hadoop fs -copyFromLocal -f -p "${INTERMEDIATE_DIR}/*" "${HDFS_PATH}/"

