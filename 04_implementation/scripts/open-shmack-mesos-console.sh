#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

xdg-open "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`/mesos" > /dev/null 2>&1 &
echo "Done."
