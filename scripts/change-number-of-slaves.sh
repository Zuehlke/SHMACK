#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <new number of slaves>"
    exit 1
fi

########  See shmack_env for shared parameters

STACK_NAME=`cat ${CURRENT_STACK_NAME_FILE}`
SLAVE_INSTANCE_COUNT="$1"

TEMPLATE_PARAMETERS="${TEMPLATE_PARAMETERS} ParameterKey=SlaveInstanceCount,ParameterValue=${SLAVE_INSTANCE_COUNT}"

CLOUD_FORMATION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/cloud-formation-update-result.json

echo "${STACK_NAME}" > ${CURRENT_STACK_NAME_FILE}

echo "Updating stack ${STACK_NAME} in aws to ${SLAVE_INSTANCE_COUNT} slave instances..."
run aws cloudformation update-stack --output json --stack-name ${STACK_NAME} --template-url ${TEMPLATE_URL} --parameters ${TEMPLATE_PARAMETERS} --capabilities CAPABILITY_IAM | tee ${CLOUD_FORMATION_OUTPUT_FILE}

date
echo -n "Stack update initialized. Waiting for all EC2 instances ready..."
function waitForStackUpdateComplete {
	STACK_DESCRIPTION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/stack-description.json
	while true; do 
		run aws cloudformation describe-stacks --output json --stack-name ${STACK_NAME} > ${STACK_DESCRIPTION_OUTPUT_FILE}		
		STATUS=`get-stack-status.sh ${STACK_DESCRIPTION_OUTPUT_FILE}`
		case "$STATUS" in
			"UPDATE_COMPLETE" )
				echo
				echo "Stack $STATUS"
				return 0
				;;
			"UPDATE_IN_PROGRESS" )
				echo -n "."
				sleep 5
				;;
                        "UPDATE_COMPLETE_CLEANUP_IN_PROGRESS" )
				echo -n "."
				sleep 5
				;;
			*)
				echo "Invalid status for AWS Stack: $STATUS"
				exit 1 
				;;
		esac		
	done
}
waitForStackUpdateComplete
date

run update-node-info.sh

run open-shmack-master-console.sh


