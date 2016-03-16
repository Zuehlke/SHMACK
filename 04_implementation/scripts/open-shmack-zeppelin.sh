#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

HAPROXY_STATS_FILE="${TMP_OUTPUT_DIR}/HaProxyStats.html"

curl http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}`:9090/haproxy?stats > ${HAPROXY_STATS_FILE}

ZEPPELIN_PORT=`cat ${HAPROXY_STATS_FILE} | grep --perl-regexp --max-count 1 --only-matching ">zeppelin_[0123456789]{2,5}<" | sed s/">zeppelin_"// | sed s/"<"//`


open-browser.sh http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}`:${ZEPPELIN_PORT}

