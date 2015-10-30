#/bin/bash

cd `dirname $0`
source ./shmack_env
pwd

run apt-get update
run apt-get -y install git-cola git-gui awscli python-setuptools python-virtualenv geany 
run easy_install pip
run pip install virtualenv
