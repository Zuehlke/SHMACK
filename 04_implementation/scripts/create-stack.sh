#/bin/bash

cd `dirname $0`
mkdir -p target || exit 1

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
# AWS::IAM::AccessKey, AWS::IAM::InstanceProfile, AWS::IAM::Role, AWS::IAM::User
PARAMETERS=""
PARAMETERS="${PARAMETERS} ParameterKey=AcceptEULA,ParameterValue=Yes"
PARAMETERS="${PARAMETERS} ParameterKey=KeyName,ParameterValue=${KEY_PAIR_NAME}"
PARAMETERS="${PARAMETERS} ParameterKey=PublicSlaveInstanceCount,ParameterValue=${PUBLIC_SLAVE_INSTANCE_COUNT}"
PARAMETERS="${PARAMETERS} ParameterKey=SlaveInstanceCount,ParameterValue=${SLAVE_INSTANCE_COUNT}"

echo aws cloudformation create-stack --stack-name ${STACK_NAME} --template-url ${TEMPLATE_URL} --parameters ${PARAMETERS}
aws cloudformation create-stack --stack-name ${STACK_NAME} --template-url ${TEMPLATE_URL} --parameters ${PARAMETERS} --capabilities CAPABILITY_IAM
