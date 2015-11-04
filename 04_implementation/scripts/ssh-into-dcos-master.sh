#/bin/bash

cd `dirname $0`
. ./shmack_env


# taken from https://docs.mesosphere.com/services/sshcluster/

SSH_IP_ADDRESS=`getSshIpAddress.sh`
echo "SSH into ${SSH_IP_ADDRESS}..."


