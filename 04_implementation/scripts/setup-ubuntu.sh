#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env


run apt-get update
run apt-get -y install git-cola git-gui awscli python-setuptools  synaptic geany curl gitk gradle
run apt-get -y remove python-pip python-pip-whl python-virtualenv
run easy_install pip
run pip install virtualenv --upgrade
