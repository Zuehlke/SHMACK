#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <src> <dest>"
    exit 1
fi

MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`
SRC="$1"
DEST="$2"

echo "Source     : $SRC"
echo "Destination: $DEST"
echo "Syncing with SSH to ${MASTER_IP_ADDRESS}..."

rsync -avzh --delete "${SRC}" core@${MASTER_IP_ADDRESS}:"${DEST}" 
