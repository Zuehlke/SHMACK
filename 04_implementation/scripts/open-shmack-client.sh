#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

xdg-open "http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_FILE}`" &
echo "Done."
