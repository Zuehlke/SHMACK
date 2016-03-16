#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$1" == "--cli" ]
	then
		CLI_OPTION="--cli"
else
		CLI_OPTION=""
fi		

run pip install --upgrade pip virtualenv dcoscli
run mkdir -p ${HOME}/.dcos/
run dcos config set core.reporting true
run dcos config set core.dcos_url http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`
run dcos config set core.ssl_verify false
run dcos config set core.timeout 5
run dcos package repo add Multiverse https://github.com/mesosphere/multiverse/archive/version-2.x.zip

for package in `cat ${CURRENT_STACK_INSTALL_PACKAGES_FILE}`
do
	run dcos package install ${package} ${CLI_OPTION} --yes
done

for package in `cat ${CURRENT_STACK_OPTIONAL_PACKAGES_FILE}`
do
	run dcos package install ${package} ${CLI_OPTION}
done

for package in `cat ${CURRENT_STACK_INSTALL_APPS_FILE}`
do
	run dcos package install --app ${package} ${CLI_OPTION}
done

date

run update-node-info.sh

echo
read -p "Press Enter to confirm ssh-identities (and logout from cluster after confirmations by typing 'exit')." 
ssh-into-dcos-master.sh
ssh-into-dcos-slave.sh 0

run open-shmack-master-console.sh

echo
echo 
echo "Master URL: http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`"
echo "Public Slave URL: http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_FILE}`"

echo "see also: open-shmack-master-console.sh"
echo "see also: open-shmack-client.sh"
echo