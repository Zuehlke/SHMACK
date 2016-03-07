#/bin/bash

cd `dirname ${BASH_SOURCE[0]}`
. ./shmack_env

run apt-get update
run apt-get -y install git-gui awscli python-setuptools curl wget gradle openjdk-8-jdk scala scala-library apt-transport-https ca-certificates
## On Ubuntu 14.04 LTS, it was necessary to remove some Python libs and replace them with pip. 
#run apt-get -y remove python-pip python-pip-whl python-virtualenv
run easy_install pip
run pip install virtualenv --upgrade

