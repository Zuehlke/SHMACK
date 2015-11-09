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

echo "Source     : $SRC"
echo "Destination: $DEST"

# see http://stackoverflow.com/questions/5527068/how-do-you-use-an-identity-file-with-rsync
eval $(ssh-agent) # Create agent and environment variables
ssh-add ${SSH_KEY_LOCATION}

echo "Syncing from Slave ${SLAVE_IP_ADDRESS} to Master ${MASTER_IP_ADDRESS}..."
run ssh -A -t core@${MASTER_IP_ADDRESS} rsync -avzh --delete core@${SLAVE_IP_ADDRESS}:"${SRC}" "${DEST}"  

echo "Syncing with SSH from Master to local: ${MASTER_IP_ADDRESS}..."
run rsync -avzh --delete core@${MASTER_IP_ADDRESS}:"${DEST}" "${DEST}" 


