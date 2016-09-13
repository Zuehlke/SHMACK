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


HDFS_DEST="hdfs://hdfs${DEST}"
INTERMEDIATE_DIR="/tmp/hdfs-xchange/to-hdfs/${DEST}"

# Directory
if [ -d "$SRC" ] ; then
  HDFS_DEST_PARENT="`dirname "${HDFS_DEST}"`"
  run sync-to-dcos-master.sh "${SRC}/" "${INTERMEDIATE_DIR}/"
  run run-on-dcos-master.sh hadoop fs -mkdir -p "${HDFS_DEST_PARENT}/"
  run run-on-dcos-master.sh hadoop fs -copyFromLocal -f -p "${INTERMEDIATE_DIR}/" "${HDFS_DEST_PARENT}/"
else
# files --> we use a sync folders approach here to avoid copying files already present
  FILENAME=`basename "$SRC"`
  TARGET_DIR=`dirname "$DEST"`
  run rm -rf "${INTERMEDIATE_DIR}/"
  run mkdir -p "${INTERMEDIATE_DIR}/"
  run ln "$SRC" "${INTERMEDIATE_DIR}/${FILENAME}"
  run sync-to-dcos-master.sh "${INTERMEDIATE_DIR}/" "${INTERMEDIATE_DIR}/"
  run run-on-dcos-master.sh hadoop fs -mkdir -p "hdfs://hdfs${TARGET_DIR}/"
  run run-on-dcos-master.sh hadoop fs -copyFromLocal -f -p "${INTERMEDIATE_DIR}/${FILENAME}" "${HDFS_DEST}"	
fi

