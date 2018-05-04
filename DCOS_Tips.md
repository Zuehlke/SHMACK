DCOS Command Collection
=======================

Here are some handy snippets to help yourself when operating with DC/OS.


SETUP KAFKA LOCALLY
-------------------
`./bin/zookeeper-server-start.sh config/zookeeper.properties`

`./bin/kafka-server-start.sh config/server.properties`

`./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test`

`./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic test`

`./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test`


RUN INTERACTIVE CQL
-------------------

`docker run -it cassandra:3.10 cqlsh node-0-server.cassandra.autoip.dcos.thisdcos.directory --request-timeout=20000`


ZOOKEPER CLEANUP
----------------
`dcos node ssh --master-proxy --leader`

`docker run mesosphere/janitor /janitor.py -r kafka-role -p kafka-principal -z dcos-service-kafka`

`docker run mesosphere/janitor /janitor.py -r cassandra-role -p cassandra-principal -z dcos-service-cassandra`


KAFKA CONSUMER
--------------
`docker run -it mesosphere/kafka-client`

`./kafka-console-consumer.sh --zookeeper leader.mesos:2181/dcos-service-kafka --topic data-analytics --from-beginning`


COPY FROM DS/OS NODE
--------------------
`ssh -A -t core@13.57.245.86 ssh -A -t core@10.0.3.116  "cat dump.csv " > ~/Downloads/dump.csv`



SSH TUNNEL eg. SPARK JOB
------------------------
`ssh -A -t -L 4040:localhost:4040 core@13.57.186.137 ssh -L 4040:localhost:4040 core@10.0.3.49`

DCOS LIST ALL PACKAGE VERSIONS
------------------------------
`dcos package describe spark --package-versions`


