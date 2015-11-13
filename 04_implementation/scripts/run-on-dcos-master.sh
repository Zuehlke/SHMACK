#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -lt 1 ]; then
    echo "Missing arguments"
    echo "Usage: `basename $0` <commands>"
    exit 1
fi

MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`
ssh -i ${SSH_KEY_LOCATION} core@${MASTER_IP_ADDRESS} "source /etc/profile.d/dcos.sh ; "$@" "

