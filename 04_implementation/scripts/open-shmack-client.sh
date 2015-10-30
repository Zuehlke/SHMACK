#/bin/bash

cd `dirname $0`
. ./shmack_env

xdg-open "http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_FILE}`" &
echo "Done."
