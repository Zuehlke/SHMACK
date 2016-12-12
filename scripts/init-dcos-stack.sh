#!/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

if [ "$1" == "--cli" ]
	then
		CLI_OPTION="--cli"
else
		CLI_OPTION=""
fi		

run sudo -H pip install --upgrade pip virtualenv dcoscli==0.4.11
run mkdir -p ${HOME}/.dcos/
run dcos config set core.reporting true
run dcos config set core.dcos_url http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`
run dcos config set core.ssl_verify false
run dcos config set core.timeout 5

if [ "MultiversePresent" != "`dcos package repo list | grep --only-matching Multiverse`Present" ]
	then
		run dcos package repo add Multiverse https://github.com/mesosphere/multiverse/archive/version-2.x.zip
fi

for package in `cat ${CURRENT_STACK_INSTALL_PACKAGES_FILE}`
do
	run dcos package install ${package} ${CLI_OPTION} --yes
done

for package in `cat ${CURRENT_STACK_OPTIONAL_PACKAGES_FILE}`
do
	run dcos package install ${package} ${CLI_OPTION}
done

if [ "Noop" == "${CLI_OPTION}Noop" ]
	then
		# In contrast to packages, apps don't have a --cli option and need to be installed only once
		# They also don't ask for confirmation, so --yes makes no difference 
		for package in `cat ${CURRENT_STACK_INSTALL_APPS_FILE}`
		do
			run dcos package install --app ${package}
		done
fi
date

run open-shmack-master-console.sh

echo
echo 
echo "Master URL: http://`cat ${CURRENT_MESOS_MASTER_DNS_FILE}`"
echo "Public Slave URL: http://`cat ${CURRENT_PUBLIC_SLAVE_DNS_FILE}`"

echo "see also: open-shmack-master-console.sh"
echo "see also: open-shmack-client.sh"
echo
