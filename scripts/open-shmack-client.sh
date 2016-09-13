#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

open-browser.sh "http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_FILE}`"
