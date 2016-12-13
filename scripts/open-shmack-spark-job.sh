#!/bin/bash

if [ -z "$1" ]
	then
		echo "No spark job name (main class) supplied"
		echo "Usage open-shmack-spark-job.sh <MainClass>"
		exit 1
fi

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

open-browser.sh "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`/service/$1" 
