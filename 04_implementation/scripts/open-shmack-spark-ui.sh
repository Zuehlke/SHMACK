#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

open-browser.sh `dcos spark webui`

