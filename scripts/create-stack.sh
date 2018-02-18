#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


###################################################################################################
########  See also shmack_env for shared parameters
###################################################################################################

# Space-separated list of packages to install (without asking)
# 'hdfs' and 'spark' are needed to run the spark unit tests.
# You can later install additional packages using 'dcos package install'
# The list of available packages can be retrieved with 'dcos package search'
INSTALL_PACKAGES=""
#INSTALL_PACKAGES="hdfs spark chronos marathon marathon-lb"

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

STACK_RESSOURCES_FILE=${TMP_OUTPUT_DIR}/stack-ressources.json

run aws cloudformation describe-stack-resources --stack `cat ${CURRENT_STACK_ID_FILE}` > ${STACK_RESSOURCES_FILE}

run cat  ${STACK_RESSOURCES_FILE} | grep --extended-regexp --only-matching `aws ec2 describe-security-groups --filters "Name=description,Values=Mesos Slaves Public" --query 'SecurityGroups[].GroupId' --output text | sed --regexp-extended "s/\\s+/|/g"` > ${CURRENT_PUBLIC_SLAVE_SECGROUP_FILE}
run aws ec2 describe-instances --filters "Name=instance-state-code,Values=16" "Name=instance.group-id,Values=`cat ${CURRENT_PUBLIC_SLAVE_SECGROUP_FILE}`" --query 'Reservations[].Instances[].[PublicDnsName,Tags[?Key==Name].Value[]]' --output text > ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}

echo ${INSTALL_PACKAGES} > ${CURRENT_STACK_INSTALL_PACKAGES_FILE}
echo ${OPTIONAL_PACKAGES} > ${CURRENT_STACK_OPTIONAL_PACKAGES_FILE}
echo ${INSTALL_APPS} > ${CURRENT_STACK_INSTALL_APPS_FILE}
echo ${OPTIONAL_APPS} > ${CURRENT_STACK_OPTIONAL_APPS_FILE}

run init-dcos-stack.sh
