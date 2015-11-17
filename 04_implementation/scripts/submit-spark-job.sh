#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

# This script is required to be invoked from Java Testcases as there is a problem with passing of apostrophes from java to unix

SUBMIT_ARGS="$@"
echo "Submitting Spark Job with $# arguments: ${SUBMIT_ARGS}"
run dcos spark run --submit-args="${SUBMIT_ARGS}"
