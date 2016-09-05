#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

open-browser.sh "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`/exhibitor" 
