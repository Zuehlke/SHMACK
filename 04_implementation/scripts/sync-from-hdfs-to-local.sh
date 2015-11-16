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

if [[ "$SRC" =~ ^/.* ]]; then
    echo "SRC starts with / --> ;-)"
else 
    echo "SRC must start with /"
    exit 1
fi

MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`

echo "Remote Source        : $SRC"
echo "Local Destination    : $DEST"

INTERMEDIATE_DIR="/tmp/hdfs-xchange/from-hdfs/${DEST}"
run run-on-dcos-master.sh rm -rf "${INTERMEDIATE_DIR}"
run run-on-dcos-master.sh mkdir -p "${INTERMEDIATE_DIR}"
HDFS_PATH="hdfs://hdfs${SRC}"
run run-on-dcos-master.sh hadoop fs -copyToLocal "${HDFS_PATH}/*" "${INTERMEDIATE_DIR}/"
run sync-from-master-to-local.sh "${INTERMEDIATE_DIR}" "${DEST}"

