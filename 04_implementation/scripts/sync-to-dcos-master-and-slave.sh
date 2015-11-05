#/bin/bash

cd `dirname $0`
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


echo "Source     : $SRC"
echo "Destination: $DEST"
echo "Syncing with SSH to Master: ${MASTER_IP_ADDRESS}..."

# see http://stackoverflow.com/questions/5527068/how-do-you-use-an-identity-file-with-rsync
eval $(ssh-agent) # Create agent and environment variables
ssh-add ${SSH_KEY_LOCATION}

run rsync -avzh --delete "${SRC}" core@${MASTER_IP_ADDRESS}:"${DEST}" 

echo "Syncing from Master ${MASTER_IP_ADDRESS} to Slave ${SLAVE_IP_ADDRESS}..."
run ssh -A -t core@${MASTER_IP_ADDRESS} rsync -avzh --delete "${DEST}" core@${SLAVE_IP_ADDRESS}:"${DEST}" 
