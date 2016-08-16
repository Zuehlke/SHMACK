#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


###################################################################################################
########  See also shmack_env for shared parameters
###################################################################################################

# Number of worker nodes. Need significant memory and fast I/O, so usually configured to
# use m3.xlarge instances (4 vCPU, 15 GB Memory, 2x40 GB SSD; about 0.15 USD per hour each)
# Theoretically, a minimum of 3 could work, but so far, 5 was needed to run a cluster without problems.
# Without special setup with Amazon, you can run up to 40 nodes per region; 
# so leaving aside the 3 infrastructure nodes (master + backup, public slave), 
# this leaves you the potential to start at most 37 worker slave instances. 
#
# If memory becomes the bottleneck, better switch to memory optimized instances like r3.xlarge
# (https://aws.amazon.com/ec2/instance-types/) instead of cranking up generic instance count.
SLAVE_INSTANCE_COUNT=5

# Instance type as defined in https://aws.amazon.com/ec2/pricing/ used for all worker nodes / slaves
# Default is m3.xlarge, which should remain the lowest setting.
# Other options could be m3.2xlarge with same number of nodes but a bit more "beefy", 
# or the corresponding c3 or r3 instances, depending if you need more computing power (c3) or memory (r3).
#SLAVE_INSTANCE_TYPE="m3.xlarge"
SLAVE_INSTANCE_TYPE="r3.2xlarge"

# Space-separated list of packages to install (without asking)
# 'hdfs' and 'spark' are needed to run the spark unit tests.
# You can later install additional packages using 'dcos package install'
# The list of available packages can be retrieved with 'dcos package search'
INSTALL_PACKAGES="hdfs spark chronos marathon marathon-lb"

# Space-separated list of optional packages to install if user confirms with 'yes' 
# You can later install additional packages using 'dcos package install'
OPTIONAL_PACKAGES=""

# Space-separated list of apps to install (without asking)
# 'zeppelin' provides a nice UI for getting started - requires packages 'marathon' and 'marathon-lb', 
# but you may have to also configure marathon labels for zeppelin.
# You can later install additional packages using 'dcos package install --app'
INSTALL_APPS=""
#INSTALL_APPS="zeppelin"

###################################################################################################
########  NOTHING to change below this line (no hardcoded values)
###################################################################################################

run mkdir --parents ${TMP_OUTPUT_DIR}
run mkdir --parents ${CURRENT_STATE_DIR}

TEMPLATE_PARAMETERS="${TEMPLATE_PARAMETERS} ParameterKey=SlaveInstanceCount,ParameterValue=${SLAVE_INSTANCE_COUNT} "

if [ "${OFFICIAL_TEMPLATE_URL}Unchanged" != "${TEMPLATE_URL}Unchanged" ] 
	then 
		TEMPLATE_PARAMETERS="${TEMPLATE_PARAMETERS} ParameterKey=MasterInstanceType,ParameterValue=${SLAVE_INSTANCE_TYPE} ParameterKey=SlaveInstanceType,ParameterValue=${SLAVE_INSTANCE_TYPE}  ParameterKey=PublicSlaveInstanceType,ParameterValue=${SLAVE_INSTANCE_TYPE} ParameterKey=SpotInstancePrice,ParameterValue=${MAX_SPOT_INSTANCE_PRICE} "
fi

CLOUD_FORMATION_OUTPUT_FILE=${TMP_OUTPUT_DIR}/cloud-formation-result.json

echo "${STACK_NAME}" > ${CURRENT_STACK_NAME_FILE}

echo "Creating stack ${STACK_NAME} in aws... with ${TEMPLATE_PARAMETERS}"
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

run aws ec2 describe-security-groups --filters "Name=description,Values=Mesos Slaves Public" --query 'SecurityGroups[].GroupId' --output text > ${CURRENT_PUBLIC_SLAVE_SECGROUP_FILE}
run aws ec2 describe-instances --filters "Name=instance-state-code,Values=16" "Name=instance.group-id,Values=`cat ${CURRENT_PUBLIC_SLAVE_SECGROUP_FILE}`" --query 'Reservations[].Instances[].[PublicDnsName,Tags[?Key==Name].Value[]]' --output text > ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}

echo ${INSTALL_PACKAGES} > ${CURRENT_STACK_INSTALL_PACKAGES_FILE}
echo ${OPTIONAL_PACKAGES} > ${CURRENT_STACK_OPTIONAL_PACKAGES_FILE}
echo ${INSTALL_APPS} > ${CURRENT_STACK_INSTALL_APPS_FILE}
echo ${OPTIONAL_APPS} > ${CURRENT_STACK_OPTIONAL_APPS_FILE}

run init-dcos-stack.sh