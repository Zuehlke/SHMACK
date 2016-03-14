#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


###################################################################################################
########  See also shmack_env for shared parameters
###################################################################################################

# Number of worker nodes. Need significant memory and fast I/O, so usually configured to
# use m3.xlarge instances (4 vCPU, 15 GB Memory, 2x40 GB SSD; about 0.15 USD per hour each)
# Theoretically, a minimum of 3 could work, but so far, 5 was needed to run a cluster without problems.
# Wihtout special setup with Amazon, you can run up to 40 nodes per region; 
# so leaving aside the 3 infrastructure nodes (master + backup, public slave), 
# this leaves you the potential to start at most 37 worker slave instances. 
SLAVE_INSTANCE_COUNT=5

###################################################################################################
########  NOTHING to change below this line (no hardcoded values)
###################################################################################################

run mkdir -p ${TMP_OUTPUT_DIR}
run mkdir -p ${CURRENT_STATE_DIR}

TEMPLATE_PARAMETERS="${TEMPLATE_PARAMETERS} ParameterKey=SlaveInstanceCount,ParameterValue=${SLAVE_INSTANCE_COUNT}"

CLOUD_FORMATION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/cloud-formation-result.json

echo "${STACK_NAME}" > ${CURRENT_STACK_NAME_FILE}

echo "Creating stack ${STACK_NAME} in aws..."
run aws cloudformation create-stack --output json --stack-name ${STACK_NAME} --template-url ${TEMPLATE_URL} --parameters ${TEMPLATE_PARAMETERS} --capabilities CAPABILITY_IAM | tee ${CLOUD_FORMATION_OUTPUT_FILE}

echo "Extracting StackId:"
run get-stack-id.sh ${CLOUD_FORMATION_OUTPUT_FILE} | tee ${CURRENT_STACK_ID_FILE} 

date
echo -n "Stack creation initialized. Waiting for all EC2 instances ready..."
function waitForStackCreateComplete {
	STACK_DESCRIPTION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/stack-description.json
	while true; do 
		run aws cloudformation describe-stacks --output json --stack-name ${STACK_NAME} > ${STACK_DESCRIPTION_OUTPUT_FILE}		
		STATUS=`get-stack-status.sh ${STACK_DESCRIPTION_OUTPUT_FILE}`
		case "$STATUS" in
			"CREATE_COMPLETE" )
				echo
				echo "Stack $STATUS"
				return 0
				;;
			"CREATE_IN_PROGRESS" )
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
waitForStackCreateComplete
date

getStackOutputValue.sh DnsAddress            ${STACK_DESCRIPTION_OUTPUT_FILE} > ${CURRENT_MESOS_MASTER_DNS_FILE}
getStackOutputValue.sh PublicSlaveDnsAddress ${STACK_DESCRIPTION_OUTPUT_FILE} > ${CURRENT_PUBLIC_SLAVE_DNS_FILE}

run init-dcos-stack.sh