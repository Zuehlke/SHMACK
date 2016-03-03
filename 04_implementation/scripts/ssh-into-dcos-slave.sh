#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <0-based index of slave>"
    exit 1
fi

SLAVE_INDEX="$1"

# taken from https://docs.mesosphere.com/services/sshcluster/

MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`
SLAVE_ID=`getSlaveAttribute.sh ${CURRENT_NODE_INFO_FILE} ${SLAVE_INDEX} "id"`
SLAVE_INTERNAL_IP=`getSlaveAttribute.sh ${CURRENT_NODE_INFO_FILE} ${SLAVE_INDEX} "hostname"`
echo "SSH into SLAVE ID ${SLAVE_ID} with internal IP-Address ${SLAVE_INTERNAL_IP}..."
echo "  (If this blocks, make sure the network does allow ssh. Most Corporate Networks don't!)"

## according to https://docs.mesosphere.com/administration/sshcluster/ you could use dcos node ssh also for that
# dcos node ssh --master-proxy --slave=${SLAVE_ID}
## but the SLAVE_ID is not just an index, but the true ID ... and there's not yet an easy way to determine that :-( 
ssh -A -t -i ${SSH_KEY_LOCATION} core@${MASTER_IP_ADDRESS} ssh -A -t core@${SLAVE_INTERNAL_IP}

# we do NOT want to use "dcos node ssh --master-proxy --slave=${SLAVE_ID}" as we cannot reliably determine which slave Adress or IP is used.
# to know this is important for sync-to-dcos-master-and-slave.sh to work without a new authentification.

