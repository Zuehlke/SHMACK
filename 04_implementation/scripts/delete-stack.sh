#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

STACK_NAME=`cat ${CURRENT_STACK_NAME_FILE}`
STACK_ID=`cat ${CURRENT_STACK_ID_FILE}`

echo "Deleting stack ${STACK_NAME} in aws, (ID=${STACK_ID})..."
# use ${STACK_NAME}  for deletion as this is more robust as the name of a stack never changes
run aws cloudformation delete-stack --output json --stack-name ${STACK_NAME} 

echo -n "Stack deletion initialized. Waiting for all deleted..."
sleep 5

function waitForStackDeleteComplete {
	STACK_DESCRIPTION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/stack-description.json
	while true; do 
		# use ${STACK_ID} for query as we cannot query deleted stacks by name
		run aws cloudformation describe-stacks --output json --stack-name ${STACK_ID} > ${STACK_DESCRIPTION_OUTPUT_FILE}		
		STATUS=`get-stack-status.sh ${STACK_DESCRIPTION_OUTPUT_FILE}`
		case "$STATUS" in
			"DELETE_COMPLETE" )
				echo
				echo "Stack $STATUS"
				return 0
				;;
			"DELETE_IN_PROGRESS" )
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
waitForStackDeleteComplete


