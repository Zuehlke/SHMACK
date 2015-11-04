#/bin/bash


MASTER_NODE_METADATA_FILE=${TMP_OUTPUT_DIR}/master-node-metadata.json

# as of 2015-11-04 this file contains exactly this JSON: 
# {"PUBLIC_IPV4": "54.183.250.79"}
run curl --silent "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`/metadata" > ${MASTER_NODE_METADATA_FILE} 

RESULT=`run python ${SCRIPTS_DIR}/helpers/get-single-json-value.py PUBLIC_IPV4 "${MASTER_NODE_METADATA_FILE}"`
echo "$RESULT"
