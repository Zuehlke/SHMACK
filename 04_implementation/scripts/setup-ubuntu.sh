#/bin/bash

cd `dirname $0`
. ./shmack_env


run apt-get update
run apt-get -y install git-cola git-gui awscli python-setuptools python-virtualenv geany curl gitk
run easy_install pip
run pip install virtualenv --upgrade
