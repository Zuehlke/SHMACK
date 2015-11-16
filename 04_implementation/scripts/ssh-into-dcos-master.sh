#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


# taken from https://docs.mesosphere.com/services/sshcluster/

SSH_IP_ADDRESS=`cat ${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}`
echo "SSH into ${SSH_IP_ADDRESS}..."
echo "  (If this blocks, make sure the network does allow ssh. Most Corporate Networks don't!)"

# we intentionally use the direct SSH syntax to be able to check whether the ssh connection is OK. 
# This is required for the rsync in sync-to-dcos-master.sh
# another option with the same result is: "dcos node ssh --master-proxy --master"
ssh -i ${SSH_KEY_LOCATION} core@${SSH_IP_ADDRESS}


