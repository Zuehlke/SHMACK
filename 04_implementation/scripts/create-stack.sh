#/bin/bash

cd `dirname $0`
. ./shmack_env


###################################################################################################
########  All parameters are here, maybe change them to command line options later
###################################################################################################

STACK_NAME="MyShmackStack-1"
TEMPLATE_URL="https://s3.amazonaws.com/downloads.mesosphere.io/dcos/stable/cloudformation/single-master.cloudformation.json"
KEY_PAIR_NAME="shmack-key-pair-01"
PUBLIC_SLAVE_INSTANCE_COUNT=1
SLAVE_INSTANCE_COUNT=3

###################################################################################################
########  NOTHING to change below this line (no hardcoded values)
###################################################################################################

run mkdir -p ${DCOS_INSTALL_DIR}
run mkdir -p ${TMP_OUTPUT_DIR}
run mkdir -p ${CURRENT_STATE_DIR}

# AWS::IAM::AccessKey, AWS::IAM::InstanceProfile, AWS::IAM::Role, AWS::IAM::User
PARAMETERS=""
PARAMETERS="${PARAMETERS} ParameterKey=AcceptEULA,ParameterValue=Yes"
PARAMETERS="${PARAMETERS} ParameterKey=KeyName,ParameterValue=${KEY_PAIR_NAME}"
PARAMETERS="${PARAMETERS} ParameterKey=PublicSlaveInstanceCount,ParameterValue=${PUBLIC_SLAVE_INSTANCE_COUNT}"
PARAMETERS="${PARAMETERS} ParameterKey=SlaveInstanceCount,ParameterValue=${SLAVE_INSTANCE_COUNT}"

CLOUD_FORMATION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/cloud-formation-result.json

echo "${STACK_NAME}" > ${CURRENT_STACK_NAME_FILE}

echo "Creating stack ${STACK_NAME} in aws..."
run aws cloudformation create-stack --output json --stack-name ${STACK_NAME} --template-url ${TEMPLATE_URL} --parameters ${PARAMETERS} --capabilities CAPABILITY_IAM | tee ${CLOUD_FORMATION_OUTPUT_FILE}

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

function deploySmackStack {
	run cd ${DCOS_INSTALL_DIR}
	run curl -O https://downloads.mesosphere.io/dcos-cli/install.sh
	run bash install.sh . http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`
	run dcos package install cassandra
	run dcos package install chronos
	run dcos package install hdfs
	run dcos package install marathon
	run dcos package install spark
}
run deploySmackStack
date

run open-shmack-master-console.sh

echo
echo 
echo "Master URL: http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`"
echo "Public Slave URL: http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_FILE}`"

echo "see also: open-shmack-master-console.sh"
echo "see also: open-shmack-client.sh"


