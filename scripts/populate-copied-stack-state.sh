#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ -e ${STATE_TAR_FILE} ]
	then 
		if [ -e ${HOME}/.dcos/ ]
			then
				run rm -r ${HOME}/.dcos/
		fi
		if [ -e ${CURRENT_STATE_DIR} ]
			then
				run rm -r ${CURRENT_STATE_DIR}
		fi
		echo "Populating state..."
		run tar xzvf ${STATE_TAR_FILE}

		run init-dcos-stack.sh --cli		
		exit 0;
else
		echo "Can't find ${STATE_TAR_FILE}. Copy that first."
		exit 1;
fi