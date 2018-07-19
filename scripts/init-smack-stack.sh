#!/bin/bash

#need to do this at first as we want an instance per node
dcos marathon app add /home/bewe/smack/src/jmxExtractor/jmxExtractor_marathon.json
#wait a bit and then continue to install the rest of the stack
sleep 40

# dcos package install marathon --yes
dcos package install marathon-lb --yes
dcos package install spark --yes --options=service-configs/spark.json --package-version=2.1.0-2.2.1-1
#dcos package install spark --yes --options=service-configs/spark_new.json --package-version=2.3.0-2.2.1-2
dcos package install kafka --yes --package-version=1.1.19.1-0.10.1.0 --options=service-configs/kafka.json
dcos package install cassandra --yes
#dcos package install zeppelin --package-version=0.6.0 --yes

