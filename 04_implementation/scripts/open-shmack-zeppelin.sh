#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

HAPROXY_STATS_FILE="${TMP_OUTPUT_DIR}/HaProxyStats.html"

curl --silent "http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}`:9090/haproxy?stats" > ${HAPROXY_STATS_FILE}

ZEPPELIN_PORT=`cat ${HAPROXY_STATS_FILE} | grep --perl-regexp --max-count 1 --only-matching ">zeppelin_[0123456789]{2,5}<" | sed s/">zeppelin_"// | sed s/"<"//`

if [ "Noport" == "${ZEPPELIN_PORT}Noport" ]
	then
		echo "Could not determine forwarded port on public slave :-("
		echo "Trying to connect via SSH tunnel".
		ZEPPELIN_STATE_FILE="${TMP_OUTPUT_DIR}/ZeppelinState.html"
		curl --silent "http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`/service/marathon/v2/apps/%2Fzeppelin" > ${ZEPPELIN_STATE_FILE}
		ZEPPELIN_REMOTE_HOST=`cat ${ZEPPELIN_STATE_FILE} | grep --perl-regexp --only-matching "(?<=host\":\")[0123456789\\.]{8,20}"`
		ZEPPELIN_REMOTE_PORT=`cat ${ZEPPELIN_STATE_FILE} | grep --perl-regexp --only-matching "(?<=ipAddresses\":\\[\\],\"ports\":\\[)[0123456789]{2,5}(?=,)"`
		MASTER_IP_ADDRESS=`cat "${CURRENT_MASTER_NODE_SSH_IP_ADDRESS_FILE}"`
		ZEPPELIN_LOCAL_FORWARD_PORT=38083
		
		RUNNING_PID=`ps -e --format pid,command  | grep --perl-regexp --only-matching "[0123456789]{2,6}(?= ssh.*-L${ZEPPELIN_LOCAL_FORWARD_PORT})"`
		if [ "Nopid" != "${RUNNING_PID}Nopid" ]
			then
				kill ${RUNNING_PID}
		fi
		
		ssh -fN -A -t -i ${SSH_KEY_LOCATION} core@${MASTER_IP_ADDRESS} -L${ZEPPELIN_LOCAL_FORWARD_PORT}:${ZEPPELIN_REMOTE_HOST}:${ZEPPELIN_REMOTE_PORT}
		open-browser.sh http://localhost:${ZEPPELIN_LOCAL_FORWARD_PORT}
else
	open-browser.sh http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_NAME_FILE}`:${ZEPPELIN_PORT}
fi