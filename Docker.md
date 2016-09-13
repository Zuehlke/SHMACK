# Why Docker?
[Docker](https://www.docker.com/) provides a nice way to create containers with your application and all of its needed dependencies.
Changes can be performed and transmitted incrementally, so reducing the need to transfer huge packages all the time - you just do that once.

[Mesos](http://mesos.apache.org/documentation/latest/docker-containerizer/) has built-in support for Docker, which makes it a piece of cake to use it.
And on [DC/OS](https://mesosphere.com/blog/2013/09/26/docker-on-mesos/), using [Marathon](https://docs.mesosphere.com/1.7/usage/tutorials/docker-app/) it is really simple to run your own application 
if you publish that as a Docker image for instance on [DockerHub](https://hub.docker.com/)

# Installing Docker on Ubuntu 
Mainly follow the steps in <https://docs.docker.com/engine/installation/linux/ubuntulinux/>

* Add the package repository
  * `sudo apt-get update && sudo apt-get install apt-transport-https ca-certificates`
  * `sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D`
  * `sudo gedit /etc/apt/sources.list.d/docker.list &` and make sure, 
     it contains the following entry: `deb https://apt.dockerproject.org/repo ubuntu-xenial main` and save and close the file. 
  * `sudo apt-get update && sudo apt-get purge lxc-docker && apt-cache policy docker-engine`

* Install Docker and start demon
  * `sudo apt-get install docker-engine`
  * `sudo service docker start`
  * Verify that `sudo docker run hello-world` works.
  
* **Important** for having a reasonable workflow: Give permissions to your user
  It is pain to have to run every docker command with sudo, and there is a simple way to fix that.
  This does imply [some security concerns](https://docs.docker.com/engine/security/security/#docker-daemon-attack-surface), 
  but if developing in a VM, running your own docker images may impose almost no risk. 
  * `sudo groupadd docker`
  * `sudo usermod -aG docker $USER`
  * `sudo service docker restart`
  * **Log off and on again.** This is needed for your user to have the added permissions from the group it now belongs to.
    You may also restart Ubuntu, if you prefer / have updated some packages during the process.
  * Verify that `docker run hello-world` works - without any `sudo`. 
  
* Set up [DockerHub](https://hub.docker.com/) account for pushing your images
  * If you don't have a [DockerHub](https://hub.docker.com/) account, create one!
  * `docker login`, so you can run `push` and `pull` from the shell or within scripts.