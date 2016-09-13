#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


# supported now by dcos node ssh, see https://docs.mesosphere.com/administration/sshcluster/
# and https://mesosphere.com/blog/2015/07/22/new-dcos-cli-commands-for-logs-and-ssh/

dcos node ssh --master-proxy --leader


