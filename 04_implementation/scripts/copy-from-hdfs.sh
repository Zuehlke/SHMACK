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

run assert-absolute-path.sh "${SRC}" 
run assert-absolute-path.sh "${DEST}" 

echo "Remote Source        : $SRC"
echo "Local Destination    : $DEST"


HDFS_SRC="hdfs://hdfs${SRC}"
INTERMEDIATE_DIR="/tmp/hdfs-xchange/from-hdfs/${SRC}"

run run-on-dcos-master.sh rm -rf "${INTERMEDIATE_DIR}"
run run-on-dcos-master.sh mkdir -p "${INTERMEDIATE_DIR}"
echo run run-on-dcos-master.sh hadoop fs -copyToLocal "${HDFS_SRC}" "${INTERMEDIATE_DIR}/"
run run-on-dcos-master.sh hadoop fs -copyToLocal "${HDFS_SRC}" "${INTERMEDIATE_DIR}/"

echo "Creating local x-change directory"
# needed to check whether src is file or folder
run mkdir -p "${INTERMEDIATE_DIR}/"
run sync-from-master-to-local.sh "${INTERMEDIATE_DIR}/" "${INTERMEDIATE_DIR}/"

FILE_TO_COPY="${INTERMEDIATE_DIR}/`basename ${SRC}`"
# echo run mkdir -p "`dirname "${DEST}"`"
run mkdir -p "`dirname "${DEST}"`"
run cp -r "${FILE_TO_COPY}" "${DEST}"


