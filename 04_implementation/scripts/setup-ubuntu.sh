#/bin/bash

apt-get -y install git-cola git-gui awscli python-setuptools python-virtualenv || exit -1
easy_install pip
pip install virtualenv
