| ID     | Status | Text   | Comment |
|--------|--------|--------|--------|
| T-0    | In Progress | Install Unix packages | on fresh ubuntu |
| T-1    | In Progress | Install AWS console | with own access credentials |
| T-2    | In Progress | Read Console Input from User  | all parameters and AWS credentials |
| T-3    | In Progress | Create Stack with AWS console |  |
| T-4    | In Progress | Show URLs to connect to in shell | PublicSlave + Master (Dashboard) |
| T-5    | In Progress | Delete Stack with AWS console  |  |
| T-6    | In Progress |   |  |


# T-0: Install Unix packages


```

    1  pwd
    2  mkdir -p dcos && cd dcos &&   curl -O https://downloads.mesosphere.io/dcos-cli/install.sh &&   bash ./install.sh . http://meso-tuto-elasticl-p7b6n0qcblus-554225263.us-west-1.elb.amazonaws.com && \
    3  mkdir -p dcos && cd dcos &&   curl -O https://downloads.mesosphere.io/dcos-cli/install.sh &&   bash ./install.sh . http://meso-tuto-elasticl-p7b6n0qcblus-554225263.us-west-1.elb.amazonaws.com &&   source ./bin/env-setup
    4  mkdir -p dcos && cd dcos 
    5  cd
    6  mkdir -p dcos && cd dcos 
    7  l
    8  ll
    9  rm -rf dcos
   10  sudo easy_install pip
   11  virtualenv
   12  virtualenv --version
   13  sudo pip install virtualenv
   14  sudo pip install virtualenv --upgrade
   15  virtualenv --version
   16  virtualenv
   17  virtualenv-3.4 
   18  history 
   19  dcos
   20  dcos
   21  dcos package install cassandra
   22  dcos package install cassandra --log-level=ERROR
   23  dcos --log-level=ERROR package install cassandra 
   24  pwd
   25  ll
   26  ll dcos/
   27  curl -o oinker.json https://raw.githubusercontent.com/mesosphere/oinker/shakespeare/marathon.json
   28  ll
   29  more oinker.json 
   30  dcos marathon app add oinker.json
   31  curl -o router.json https://raw.githubusercontent.com/mesosphere/oinker/shakespeare/router/marathon.json
   32  more router.json 
   33  dcos marathon app add router.json
   34  dcos marathon app update /oinker instances=8
   35  dcos marathon app add oinker.json
   36  dcos marathon app add router.json
   37  dcos --log-level=ERROR package install cassandra 
   38  dcos --log-level=ERROR package uninstall cassandra 
   39  dcos --log-level=ERROR package install cassandra 
   40  dcos marathon app add router.json
   41  dcos marathon app add oinker.json
   42  wget http://s3.amazonaws.com/ec2-downloads/ec2-api-tools.zip
   43  curl -O http://s3.amazonaws.com/ec2-downloads/ec2-api-tools.zip
   44  sudo mkdir /usr/local/ec2
   45  sudo unzip ec2-api-tools.zip -d /usr/local/ec2
   46  java -version
   47  set
   48  echo $JAVA_HOME
   49  which java
   50  file $(which java)
   51  file /etc/alternatives/java
   52  file /usr/lib/jvm/java-7-openjdk-amd64/jre/bin/java
   53  cd
   54  ll
   55  sudo vi /etc/environment 
   56  sudo vim.tiny /etc/environment 
   57  echo $JAVA_HOME
   58  more /etc/environment 
   59  cd Documents/dcos-tutorial/tutorial_2/
   60  ls -la
   61  cd oinker/
   62  ll
   63  cd ..
   64  ll
   65  cd oinker/
   66  git status
   67  gitk &
   68  sudo apt-get install gitk
   69  gitk &
   70  git gui &
   71  cd router/
   72  ll
   73  more app.lua 
   74  docker build -t <mydockerhubusername>/demo-router .
   75  docker build -t slartibartfast27/demo-router .
   76  docker push Slartibartfast27/demo-router
   77  docker push Slartibartfast27/demoRouter
   78  docker push slartibartfast27/demo-router
   79  docker login
   80  docker push slartibartfast27/demo-router
   81  cd ..
   82  ll
   83  more definition.json 
   84  dcos marathon app add definition.json
   85  ll
   86  more definition.json 
   87  dcos package install cassandra
   88  /home/cb/.docker/config.json
   89  more /home/cb/.docker/config.json
   90  cd Documents/dcos-tutorial/tutorial_2/
   91  dcos package install cassandra
   92  history 
   93  dcos package install cassandra
   94  grep -R meso-tuto-elasticl-p7b6n0qcblus-554225263.us-west-1.elb.amazonaws.com ~
   95  grep -R meso-tuto-elasticl-p7b6n0qcblus-554225263.us-west-1.elb.amazonaws.com ~/.dcos/
   96  cd
   97  curl -O https://downloads.mesosphere.io/dcos-cli/install.sh
   98  bash install.sh . http://DCOS-Tuto-ElasticL-12KXN0NSNCCD3-1608198517.us-west-1.elb.amazonaws.com
   99  cd Documents/dcos-tutorial/tutorial_2/
  100  dcos package install cassandra
  101  history 
  102  dcos marathon app add definition.json
  103  dcos marathon app list
  104  dcos marathon app update demo instances=3
  105  dcos marathon deployment list
  106  dcos marathon app list
  107  dcos marathon app update demo instances=3
  108  dcos marathon deployment list
  109  dcos marathon app version list demo
  110  cd oinker/router/
  111  ll
  112  more marathon.json 
  113  dcos marathon app add marathon.json
  114  dcos marathon app list
  115  more marathon.json 
  116  vi marathon.json 
  117  dcos marathon app add marathon.json
  118  dcos package install cassandra
  119  history | more
  120  mkdir ${HOME}/smack
  121  cd ${HOME}/smack
  122  git clone
  123  git clone https://github.com/Zuehlke/SHMACK.git
  124  ll
  125  ll SHMACK/
  126  rm -rf SHMACK
  127  cd ..
  128  rm -rf smack
  129  mkdir ${HOME}/shmack
  130  cd ${HOME}/shmack && git clone https://github.com/Zuehlke/SHMACK.git repo
  131  ll
  132  ll repo/
  133  ll
  134  cd repo
  135  git pull
  136  history
  
  
```
