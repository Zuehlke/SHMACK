#/bin/bash

cd `dirname $0`
. ./shmack_env

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <0-based index of slave>"
    exit 1
fi

# taken from https://docs.mesosphere.com/services/sshcluster/

SLAVE_ID=`getSlaveAttribute.sh ${CURRENT_NODE_INFO_FILE} 0 "id"`
SLAVE_INTERNAL_IP=`getSlaveAttribute.sh ${CURRENT_NODE_INFO_FILE} 0 "hostname"`
echo "SSH into SLAVE ID ${SLAVE_ID} with internal IP-Address ${SLAVE_INTERNAL_IP}..."

dcos node ssh --master-proxy --slave=${SLAVE_ID}

