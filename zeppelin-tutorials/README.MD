Some tutorials for [Apache Zeppelin](https://zeppelin.apache.org/).

Need to install Zeppelin and make it accessible. 
Unfortunately, preferred ways to do so do not work yet through options, so need to reconfigure after installing

> dcos package install marathon

> dcos package install marathon-lb

DC/OS 1.7 installs by default currently Zeppelin 0.5.6 which does not work well with all examples.
Better get the more recent version 0.6.0

> dcos package install --package-version=0.6.0zeppelin

> open-shmack-marathon-ui.sh 

Select application "zeppelin", open the configuration tab, click Edit, and add the following two labels:
> HAPROXY_GROUP : external
> HAPROXY_0_PORT : 8082

Change values and restart, then you can start using Zeppelin

> open-shmack-zeppelin.sh 

... and finally import the tutorials from this directory.