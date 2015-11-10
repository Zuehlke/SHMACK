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

MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`

echo "Remote Source        : $SRC"
echo "Local Destination    : $DEST"

echo "Syncing with SSH from Master to local: ${MASTER_IP_ADDRESS}..."
run rsync -avzh --delete core@${MASTER_IP_ADDRESS}:"${SRC}" "${DEST}" 


