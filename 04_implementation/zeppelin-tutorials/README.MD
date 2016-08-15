Some tutorials for [Apache Zeppelin](https://zeppelin.apache.org/).

Need to install Zeppelin and make it accessible. 
Unfortunately, preferred ways to do so do not work yet through options, so need to reconfigure after installing

> dcos package install marathon

> dcos package install marathon-lb

> dcos package install zeppelin

> open-shmack-marathon-ui.sh 

Select application "zeppelin", open the configuration tab, click Edit, and add the following two labels:
> HAPROXY_GROUP : external
> HAPROXY_0_PORT : 8082

Change values and restart, then you can start using Zeppelin

> open-shmack-zeppelin.sh 

... and finally import the tutorials from this directory.