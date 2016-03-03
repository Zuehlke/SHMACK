#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


# supported now by dcos node ssh, see https://docs.mesosphere.com/administration/sshcluster/

dcos node ssh --master-proxy --master


