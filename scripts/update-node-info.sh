#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

run mkdir --parents ${TMP_OUTPUT_DIR}

MASTER_NODE_METADATA_FILE=${TMP_OUTPUT_DIR}/master-node-metadata.json
# as of 2015-11-04 this file contains exactly this JSON: 
# {"PUBLIC_IPV4": "54.183.250.79"}
run curl --silent "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`/metadata" > ${MASTER_NODE_METADATA_FILE} 
MASTER_NODE_IP=`run python ${SCRIPTS_DIR}/helpers/get-single-json-value.py PUBLIC_IPV4 "${MASTER_NODE_METADATA_FILE}"`
echo "${MASTER_NODE_IP}" > ${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}
echo "Written IP Address of master node ${MASTER_NODE_IP} to ${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}" 


NODE_OVERVIEW_FILE=${TMP_OUTPUT_DIR}/node-overview.json
run dcos node --json > ${CURRENT_NODE_INFO_FILE}
echo "Written slave node details to ${CURRENT_NODE_INFO_FILE}"
