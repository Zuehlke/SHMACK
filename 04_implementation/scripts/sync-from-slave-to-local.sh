#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 3 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <src> <dest> <0-based-slave-index>"
    exit 1
fi

SRC="$1"
DEST="$2"
SLAVE_INDEX="$3"

MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`
SLAVE_IP_ADDRESS=`getSlaveAttribute.sh ${CURRENT_NODE_INFO_FILE} ${SLAVE_INDEX} "hostname"`

SRC_MD5_SUM=($(echo "${SRC}" | md5sum))
TMP_MASTER_DIR="/tmp/sync-to-local-${SRC_MD5_SUM}/"
echo "Remote Source        : $SRC"
echo "Intermediate (Master): $TMP_MASTER_DIR"
echo "Local Destination    : $DEST"

echo 
echo "Syncing from Slave to Master..."
./sync-from-slave-to-master.sh "${SRC}" "${TMP_MASTER_DIR}" "${SLAVE_INDEX}"  

echo "Syncing with SSH from Master to local: ${MASTER_IP_ADDRESS}..."
run rsync -avzh --delete core@${MASTER_IP_ADDRESS}:"${TMP_MASTER_DIR}" "${DEST}" 


