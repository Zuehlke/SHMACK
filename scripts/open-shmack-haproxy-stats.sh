#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

# Opens a browser to HAProxy stats page of marathon-lb running on DC/OS.
open-browser.sh http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}`:9090/haproxy?stats