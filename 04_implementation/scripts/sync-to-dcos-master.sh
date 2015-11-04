#/bin/bash

cd `dirname $0`
. ./shmack_env

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <src> <dest>"
    exit 1
fi


SSH_IP_ADDRESS=`getSshIpAddress.sh`
SRC="$1"
DEST="$2"

echo "Source     : $SRC"
echo "Destination: $DEST"
echo "Syncing with SSH to ${SSH_IP_ADDRESS}..."

# see http://stackoverflow.com/questions/5527068/how-do-you-use-an-identity-file-with-rsync
eval $(ssh-agent) # Create agent and environment variables
ssh-add ${SSH_KEY_LOCATION}

rsync -avzh --delete "${SRC}" core@${SSH_IP_ADDRESS}:"${DEST}" 
