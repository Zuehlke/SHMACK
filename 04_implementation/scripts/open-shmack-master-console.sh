#/bin/bash

cd `dirname $0`
. ./shmack_env

xdg-open "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`" &
echo "Done."
