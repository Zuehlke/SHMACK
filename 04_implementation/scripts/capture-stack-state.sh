#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

tar czvf ${STATE_TAR_FILE} ${CURRENT_STATE_DIR_RELATIVE}

echo "Packed state in stack-state.tgz; use populate-copied-stack-state.sh to use that state of the running stack on a different machine."