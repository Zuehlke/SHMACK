#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <path-to-stack-description.json>"
    exit 1
fi

# Legal values from http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-describing-stacks.html
# CREATE_COMPLETE
# CREATE_IN_PROGRESS
# CREATE_FAILED
# DELETE_COMPLETE
# DELETE_FAILED
# DELETE_IN_PROGRESS
# ROLLBACK_COMPLETE
# ROLLBACK_FAILED
# ROLLBACK_IN_PROGRESS
# UPDATE_COMPLETE
# UPDATE_COMPLETE_CLEANUP_IN_PROGRESS
# UPDATE_IN_PROGRESS
# UPDATE_ROLLBACK_COMPLETE
# UPDATE_ROLLBACK_COMPLETE_CLEANUP_IN_PROGRESS
# UPDATE_ROLLBACK_FAILED
# UPDATE_ROLLBACK_IN_PROGRESS

run python ${SCRIPTS_DIR}/helpers/get-single-json-value.py StackStatus $1 


