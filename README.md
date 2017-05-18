# S.H.M.A.C.K

S. = Spark

H. = Hatch

M. = Mesos

A. = Akka

C. = Cassandra

K. = Kafka

## A modern stack for Big Data applications

SHMACK is open source under terms of Apache License 2.0 (see **[License Details](#license)**).
For now, it provides a quick start to set up a Mesos cluster with Spark and Cassandra on Amazon Web Services (AWS) 
using [Mesosphere DC/OS template](https://dcos.io/docs/1.7/administration/installing/cloud/aws/), 
with the intention to cover the full SMACK stack (Spark, Mesos, Akka, Cassandra, Kafka - also known as 
[Mesosphere Infinity](https://mesosphere.com/infinity/) [stack](https://mesosphere.com/blog/2015/08/20/mesosphere-infinity-youre-4-words-away-from-a-complete-big-data-system/))
and being enriched by Hatch applications (closed source).

#<font color="red">WARNING: things can get expensive $$$$$ !</font>
When setting up the tutorial servers on Amazon AWS and letting them running, there will be monthly costs of approx **1700 $** !
Please make sure that servers are only used as required. See [FAQ](#avoidBill) section in this document.

Don't get scared too much - for temporary use, this is fine as 1700$ per month is still less than 60$ a day. 
If the days are limited, e.g. for just a few days of experimentation, than this is fine - but better keep an eye on your [AWS costs](https://console.aws.amazon.com/billing/home).
For production, there would be many things needed to be done first anyway (see [Limitations](#limitations)) - so 
running costs would be a rather minor issue.

# Vision
* We want to be fast when ramping up cloud infrastructure.
* We do not want to answer "we never did this" to customers when asked.
* We want to know what the issues and traps are when setting up cloud infrastructure with the SHMACK stack.
* We want to create a reusable asset for Big Data/Fast Data combined with cloud-scale Data Analytics and Machine Learning 
  to acquire customers and be able to show competence not only on paper, but running in the cloud.

# Installation
Everything can be performed free of charge until you start up nodes in the cloud (called [Stack creation](#stackCreation)). 


## Register accounts (as needed)
If you have existing accounts, they can be used. If not:
* Create GitHub account: https://github.com/join
* Create AWS account: https://aws.amazon.com/de/


<a name="devEnvSetup" />
## Development Environment setup
You will need a (for now) a Linux machine to control and configure the running SHMACK stack. 
You will also need that in order to develop and contribute. 

### Create a Virtual Machine
* assign at least 4 GB RAM and 30 GB HDD! 
* Recommended: **[Ubuntu 16.04 LTS](http://www.ubuntu.com/download/desktop)** with VMWare-Player
  * You may use other basically every other combination you prefer, 
    but make sure there is at least 4 GB RAM assigned to your VM and more than a single CPU assigned!
    In particular, some of the default settings of VMWare and VirtualBox will give 
    only 1 GB to the VM and that will let your slow down your IDE and crash it occasionally. 
* Alternative: any other recent Linux (native, or virtualized - VirtualBox is also fine) 
  * **ATTENTION**: The AWS and DC/OS Commandline Tools (CLI) use Python with many dependencies installed and maintained through pip. 
    This may cause problems when the OS provides already some of the used libraries in older version - why it is not always possible to mix those. 
    For instance, CoreOS and OS X unfortunately don't get along right now.

### In the Virtual machine
* Install git: `sudo apt-get install git`
* Clone the SHMACK repository: `mkdir ${HOME}/shmack && cd ${HOME}/shmack && git clone https://github.com/Zuehlke/SHMACK.git shmack-repo`
* Run the setup script: `cd ${HOME}/shmack/shmack-repo/scripts && sudo -H bash ./setup-ubuntu.sh`
  * This will install among others the AWS Commandline Tools, OpenJDK 8, and Scala
  * If you don't start with a fresh image, it's probably better to have a look in `setup-ubuntu.sh` and see yourself what is missing - and install only missing bits.
* Optional: DC/OS provides the cluster on AWS currently with Oracle java version "1.8.0_51", so better use same or newer; 
  for installing the same version, follow http://askubuntu.com/questions/56104/how-can-i-install-sun-oracles-proprietary-java-jdk-6-7-8-or-jre
* Optional: DC/OS provides the cluster on AWS currently with Scala version "2.11.8", so better use same or newer
* Optional: When you like working on shell, append the following lines at the **end** of your `${HOME}/.profile`
```
PATH=${PATH}:${HOME}/shmack/shmack-repo/scripts
export PATH
```
* Optional: setup additonal tools to better work with git as described [here](./GitHelp.md)
* Optional: When intending to create new packages for the stack on DC/OS to deploy applications, 
    install [Docker](https://www.docker.com/) as described [here](./Docker.md)

### Setup AWS console 
Details can be found in: http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html
* Create AWS user including AWS Access Key
  This access key will be used on command-line to tell AWS what to do. 
  It is recommended to create distinct keys if you have different needs; so even if you have an AWS account already, better create a key just for SHMACK. That way, you can also later safely delete the key if you no longer need it.
  * https://console.aws.amazon.com/iam/home?#users
  * Username: `shmack`
  * Access type: Programmatic access
  * Click next.
  * Set permissions for shmack
  * Attach existing policies directly: AdministratorAccess
  * **DON'T TOUCH mouse or keyboard - LEAVE THE BROWSER OPEN** (credentials are shown only here, optionally download credentials and store them in a safe place only for you)
* Run `aws configure`
  * `AWS Access Key ID [None]: [from browser page]`
  * `AWS Secret Access Key [None]: [from browser page]`
  * `Default region name [None]: us-west-1`  (VERY important, DO NOT change this!)
  * `Default output format [None]: json`
* Assign Admin-Permissions to user `shmack` (Tab "Permissions", --> "Attach Policy" --> "Administrator Access"): https://console.aws.amazon.com/iam/home?#users/shmack 
  * Create a AWS Key-Pair in region **us-west-1**: 
https://us-west-1.console.aws.amazon.com/ec2/v2/home?region=us-west-1#KeyPairs:sort=keyName
    * Key pair name: `shmack-key-pair-01` 
      **Attention: Use exactly this Key pair name as it is referenced in the scripts!**
    * Create .ssh directory `mkdir ${HOME}/.ssh`
    * Save the key pair. Most likely, your browser will download the file automatically to the Downloads folder. 
      If not, and the key pair appears in the browser, copy-paste is OK, open an editor to copy to and save it with: `gedit ${HOME}/.ssh/shmack-key-pair-01.pem`
      **Attention: Use exactly this filename as it is referenced in the scripts!**
    * `chmod 600 ${HOME}/.ssh/shmack-key-pair-01.pem`
    * `ssh-add ${HOME}/.ssh/shmack-key-pair-01.pem`
    * **WARNING**: Keep your credentials and keypair safe. <font color="red">**Never, ever commit them** into a public github repo.</font>
    If someone steals and abuses them, the costs may easily exceed anything [you were afraid of](#avoidBill).
	And there shouldn't be a need to commit them in first place, so don't do it!
	Your local installation knows your credentials through `aws configure`, 
	so you don't need to store them for SHMACK. 
	The only case in which you may need some AWS credentials otherwise, may probably be to copy data from S3.
	Make sure you always perform this as one-time operations you do not need to commit! 
* Optional: For the impatient, you may now directly proceed with [Stack Creation](#stackCreation) and setup your IDE while CloudFormation takes place which will take some minutes anyway.
* http://aws.amazon.com/contact-us/ec2-request TODO!!!

### Download, install, and configure Eclipse for the use in SHMACK
* Download `Eclipse IDE for Java EE Developers` as 64 bit for Linux from https://www.eclipse.org/downloads/ 
* Extract eclipse: `cd ${HOME}; tar xvfz Downloads/eclipse-jee-neon-R-linux-gtk-x86_64.tar.gz` 
* Warning: On Ubuntu 16.04, you need to work around [a known issue with eclipse](#eclipseUbuntu1604) 
* Add gradle support to eclipse
  * open `eclipse/eclipse`
  * Open `Help --> Eclipse Marketplace`
  * Install `Gradle IDE Pack`
* Import Gradle projects from `${HOME}/shmack/shmack-repo/` into eclipse
  * "Import" --> "Gradle (STS) Project"
  * Click "Build model"
  * Select all projects
* Optional: In case you [created your stack](#stackCreation) already, you may now perform [infrastructure tests](#checkStackSetup) and [some Spark test jobs](#sparkTests) in Sparkjobs. 
* Optional: Install [DLTK ShellEd](#http://www.eclipse.org/dltk/install.php) for Eclipse
  * Provides a nice support for editing shell scripts in Eclipse
  * Install new software... Add `http://download.eclipse.org/technology/dltk/updates-dev/latest/`
  * Select "ShellEd IDE" and "Python IDE" and "next" to install
* Optional: Install Eclipse support for Scala from http://scala-ide.org/download/current.html
  * Install new software... Add `http://download.scala-ide.org/sdk/lithium/e44/scala211/stable/site`
  * Select "Scala IDE for Eclipse" which will install sbt, Scala 2.11, and the IDE plugins

### Optional: Use IntelliJ IDEA for SHMACK
* Download and install [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) ... so far, the Community Edition should be sufficient.
* Open IntelliJ and make sure, you have a JVM configured. If not, first create a new empty project to configure it!
  * In new project, set for Java the Project SDK to a new JDK and select `/usr/lib/jvm/java-1.8.0-openjdk-amd64`
  * You may also select Scala with use new library, download scala version 2.11.8
* Select File -> New -> Project from existing sources... -> `${HOME}/shmack/shmack-repo/`
  * Import project from external model -> Gradle
  * Use gradle wrapper task configuration
  * Finish -> This Window -> confirm selection of three modules with `Ok` -> And `Add root` when prompted
* It wil take quite some time until scanning for files to index finishes.
* Optional: In case you [created your stack](#stackCreation) already, you may now perform [infrastructure tests](#checkStackSetup) and [some Spark test jobs](#sparkTests) in Sparkjobs. 

    
## Stack Creation and Deletion 
Mesosphere provides AWS CloudFormation templates to create a stack with several EC2 instances in autoscaling groups, 
some of directly accessible (acting as gateways), others only accessible through the gateway nodes. 
See [DC/OS Network Security Documentation](https://docs.mesosphere.com/overview/security/) for details.

The scripts for SHMACK will not only create/delete such a stack, but also maintain the necessary IDs to communicate and 
setup DC/OS packeges to form SHMACK. 
It therefore makes the process described in https://mesosphere.com/amazon/setup/ even simpler and repeatable, 
and by that, more appropriate for forming short-lived clusters for quick experiments or demonstrations. 

<a name="spotinstances" />
### Optional: Use [spot](https://aws.amazon.com/ec2/spot/) instances
To lower costs you can use spot instances. To do this, change this line in shmack_env:

    TEMPLATE_URL="https://s3-us-west-1.amazonaws.com/shmack/single-master.cloudformation.spot.json"

This is currently hosted on a private s3 bucket, for details see [here](./cloud-templates/README.MD).

<a name="stackCreation" />
### Stack Creation (from now on, you pay for usage)
  * Execute `${HOME}/shmack/shmack-repo/scripts/create-stack.sh`
    * Wait approx. 10 Minutes
    * **Do NOT interrupt the script!** (especially do **NOT** press Ctrl-C to copy the instructed URL!)
    * In case of failures see [Troubleshoting Section](#setupFailing)
  * In order to automatically install, update, and configure the [DC/OS Commandline Interface](https://docs.mesosphere.com/usage/cli/install/), 
    you will be prompted to enter your password as part of the installation process needs to run via sudo.
  * Some versions of DC/OS will require activation when first used on a system. 
    If this is the case: 
    Open URL as instructed in `Go to the following link in your browser:` and enter verification code.
    Whenever you are asked to authenticate using a cloud provider, it works well to use your Github account.
  * DC/OS will now install the packages defined in `create-stack.sh`
  	* Confirm optional installations (if desired): `Continue installing? [yes/no]` --> yes
    * Even after the command returns, it will still take some time until every package is fully operational
    * In the Mesos Master UI you will see them initially in status "Idle" or "Unhealthy" until they converge to "Healthy",
      in particular Spark, HDFS, and Cassandra will need time until replications is properly initialized 
  * The script will now <a name="confirmSsh" />Login once using ssh 
    * This is necessary to add mesos master to known hosts, so that scripts and unit tests can run without manual interaction
    * Performs `${HOME}/shmack/shmack-repo/scripts/ssh-into-dcos-master.sh`and `${HOME}/shmack/shmack-repo/scripts/ssh-into-dcos-slave.sh 0`
    * You have to confirm SSH security prompts
    * Logout from the cluser (press `Ctrl-d` or type `exit` twice)
  * Optional: Check whether stack creation was successful, see **[here](#checkStackSetup)** 
  * Optional: Install additional software.
  
<a name="stackDeletion" />
### Stack Deletion
  * Option 1 (recommended):
    `${HOME}/shmack/shmack-repo/scripts/delete-stack.sh`
  * Option 2 (manual):
    * go to https://console.aws.amazon.com/cloudformation/ and delete the stack
  * Troubleshooting
    * Sometimes the deletion failes after approx. 20 minutes as a default VPC security group cannot be deleted. Reasons are likely race conditions. In this case the repetition of the stack deletion (either by Option 1 or Option 2) likely resolves the problem.
  * Verification (to avoid too high bills) make sure that...
	* ... the stack is deleted: https://console.aws.amazon.com/cloudformation/home?region=us-west-1#/stacks?filter=active
	* ... there are no autoscaling groups left: https://us-west-1.console.aws.amazon.com/ec2/autoscaling/home
	* ... there are no running EC2 instances or Volumes: https://us-west-1.console.aws.amazon.com/ec2/v2/home?region=us-west-1

# Affiliate
* Zühlke Focusgroup - Big Data / Cloud
* Team - TODO
* Initiator - wgi

# Links
* [Mesosphere Homepage](https://mesosphere.com/)
* [Documentation](http://docs.mesosphere.com/), in particular Architecture of [Components](https://docs.mesosphere.com/administration/dcosarchitecture/components/) and [Network Security](https://docs.mesosphere.com/administration/dcosarchitecture/security/)
* DC/OS for [Data Infrastructure](https://mesosphere.com/solutions/data/)
* [Tutorials](https://docs.mesosphere.com/tutorials/)
* Articles
  * [Data processing platforms architectures with SMACK (SMACK Overview Article)](http://datastrophic.io/data-processing-platforms-architectures-with-spark-mesos-akka-cassandra-and-kafka/)
  * [Introducing open source DC/OS](https://mesosphere.com/blog/2016/04/19/open-source-dcos/)
  * [The Mesosphere Datacenter Operating System is now generally available](https://mesosphere.com/blog/2015/06/09/the-mesosphere-datacenter-operating-system-is-now-generally-available/)
  * [Why DC/OS and Apache Spark are better together](https://mesosphere.com/blog/2016/05/10/why-dcos-and-apache-spark-are-better-together/) and [Spark and Mesos: shared history and future ](https://mesosphere.com/blog/2015/06/23/spark-mesos-shared-history-and-future-mesosphere-hackweek/)
  * [Making Apache Kafka Elastic With Apache Mesos](https://mesosphere.com/blog/2015/07/16/making-apache-kafka-elastic-with-apache-mesos/) and [Fast and flexible: Our new Kafka-DCOS service is in beta](https://mesosphere.com/blog/2016/02/04/kafka-dcos/)
  * [Mesosphere powers the Microsoft Azure Container Service](https://mesosphere.com/blog/2015/09/29/mesosphere-and-mesos-power-the-microsoft-azure-container-service/) and [Azure Container Service, powered by the DCOS, is available for Public Preview](https://mesosphere.com/blog/2016/02/17/azure-container-service-dcos-mesosphere/)
  * [Scaling ArangoDB to gigabytes per second on Mesosphere’s DCOS](https://mesosphere.com/blog/2015/11/30/arangodb-benchmark-dcos/)
  * [Samsung is powering the Internet of Things with Mesos and Marathon](https://mesosphere.com/blog/2015/12/21/samsung-is-powering-the-internet-of-things-with-mesos-and-marathon/)
* Demo use
  * Tweeter: https://github.com/mesosphere/tweeter - a Twitter-equivalent running on DC/OS using Cassandra for strorage, streeams to Kafka, and uses Zeppelin for analytics.
  * iot-demo: https://github.com/mesosphere/iot-demo - streams Twitter tweets to Kafka, processes with Spark, stores in Cassandra, and queries using Zeppelin.
  * cd-demo: https://github.com/mesosphere/cd-demo - implements a continuous delivery pipeline on DC/OS using Jenkins and Docker.
  * KillrWeather Time Series demo: https://github.com/mesosphere/killrweather - full SMACK stack demo including an Akka application. 
  * Crime Buster Time Series demo: https://github.com/mesosphere/time-series-demo - streams crime data of Chicago and adds InfluxDB and Grafana to the Kafka and Spark mix.
  * FluxCapcitor: https://github.com/fluxcapacitor - Reference Architecture for Netflix Style recommendation engines employing machine learning using a more complete, but also more complex [PANCAKE STACK](https://github.com/fluxcapacitor/pipeline/wiki)
* Other Ressources
  * AMP Lab - Reference Architecture: https://amplab.cs.berkeley.edu/software/
  * AMP Lap Camp with exercices: http://ampcamp.berkeley.edu/5/
  * Public Datasets (S3): https://aws.amazon.com/de/public-data-sets/
  * Apache Spark Example Use Cases (with Code): https://github.com/4Quant
  * Apache Spark Twitter Word Count: https://github.com/snowplow/spark-example-project

<a name="limitations" />
# Important Limitations / Things to consider before going productive
* As of 2015-10-28 the DC/OS stack does **NOT work in AWS Region `eu-central-1` (Frankfurt)**. Recommended region to try is `us-west-1`. Take care of **regulatory issues** (physical location of data) when thinking about a real productive System.
* What if the number of client request "explodes". Is there a way to do autoscaling with Mesosophere DC/OS WITHOUT human interaction?
* As of 2015-11-13 **all data in HDFS is lost** when scaling down, e.g. from 10 to 5 Slave nodes. This is a blocking issue. If unresolved productive use of the Stack is not possible. see **[here](https://github.com/Zuehlke/SHMACK/blob/master/03_analysis_design/Issues/Issue-10%20HDFS-Access/Scaling%20Test.docx)** According to the mesosphere development team (chat), this issue is addressed by **[maintenance primitives](https://mesosphere.com/blog/2015/10/07/mesos-inverse-offers/)**. But it is not clear when it will be finished.
* Make sure that admin access to the Mesos Master console is secure. As of 2016-06-03 the http access is only secured by asking for a cloud login (i.e. your GitHub account). 
  https needs to be implemented. Partially, this is only a problem of the DC/OS Community Edition; the Enterprise Edition learned many required features some time ago in version [1.3](https://mesosphere.com/blog/2015/11/17/new-security-features-and-more-in-mesosphere-dcos-1-3/) and [1.6](https://mesosphere.com/blog/2016/03/08/mesosphere-dcos-1-6/).
* Data Locality, e.g. How do we minimze latency between data storage and Spark workers?
* Not all DC/OS Packages are production ready. 
  * Those from [Mesosphere Universe](https://github.com/mesosphere/universe) should in a usable state, 
    but even for Spark the info states that it is still "in beta and there may be bugs, incomplete features, incorrect documentation or other discrepencies"
  * Those from [Mesosphere Multiverse](https://github.com/mesosphere/multiverse) are all experimental.

# FAQ
<a name="avoidBill" />
## How do I avoid to be surprised by a monthly bill of **1700 $** ?
Also check out [spot instances](#spotinstances) to reduce costs.
Check regularly the [Billing and Cost Dashboard](https://console.aws.amazon.com/billing/home), which Amazon will update daily. You also install the [AWS Console Mobile App](https://aws.amazon.com/console/mobile/) to even have an eye on the running instances and aggregated costs no matter where you are - and take actions if needed like deleting a running stack. 

To not constantly poll the costs, set up a [billig alert](https://console.aws.amazon.com/billing/home#/preferences).

And then: be careful when to start and stop the AWS instances.
As of 2015-10-23 there is **no** officially supported way to suspend AWS EC2 instances.
see [Stackoverflow](http://stackoverflow.com/questions/31848810/mesososphere-dcos-cluster-on-aws-ec2-instances-are-terminated-and-again-restart) and [Issue](https://github.com/Zuehlke/SHMACK/issues/2)

The only official supported way to stop AWS bills is to completely delete the stack.
**ATTENTION**: 
* To delete a stack it is not sufficient to just terminate the EC2 instances as they are contained in an autoscaling group.
* To delete a stack see **[here](#stackDeletion)**

And make sure, you keep your credentials for AWS safe!
* Never commit your AWS key ID + Secret Access Key to a repository, in particular not to a public one. There are [reports of people actively exploiting this](http://readwrite.com/2014/04/15/amazon-web-services-hack-bitcoin-miners-github/). Probably we could integegarte [Git Secrets](https://github.com/awslabs/git-secrets)...
* The same is probably true for your `shmack-key-pair-01.pem`, although this will usually only grant access to your running instances; not the option to run new instances and thus create additional costs.
* Whenever in doubt, better inactive and delete your AWS credentials and create new ones as described in [lost credentials](#forgotCred). 

<a name="shareCluster" />
## Can I share a running cluster with other people I work with to reduce costs?
In principle, you can. But be aware that you may block each other with running tasks.
* Each of you has to perform the complete [Development Environment Setup](#devEnvSetup), 
  except that only the one creating the stack needs to setup an AWS account.
* Ideally, create additional accounts for each additional user
	* Go to htps://console.aws.amazon.com/iam/home?region=us-west-1
	* Create a new user for each person to use your cluster
	* Let them get their credentials in a safe and secure way including their `shmack-key-pair-01.pem`, AWS Access Key ID, and AWS Secret Access Key
	* They will have to use them as described for [lost credentials](#forgotCred).
* [Create your stack](#stackCreation) and exchange the state
	* Use `capture-stack-state.sh` and distribute the generated file `stack-state.tgz`
	* They will have to use `populate-copied-stack-state.sh` to make use of the shared cluster
* Finally, when you are all done
	* One of you has to [Delete the stack](#stackDeletion) 
	* Delete/inactivate the additional accounts in htps://console.aws.amazon.com/iam/home?region=us-west-1

## What components are available?
That changes constantly as Mesosphere adds packages to DC/OS. And we provide our own.
* As of 2016-03-30, everything for SMACK/[Mesosphere Infinity](https://mesosphere.com/infinity/) [stack](https://mesosphere.com/blog/2015/08/20/mesosphere-infinity-youre-4-words-away-from-a-complete-big-data-system/) seems to be available, *except Akka*. 
* [Mesosphere Universe Packages for DCOS 1.7](https://github.com/mesosphere/universe-1.7/tree/version-2.x/repo/packages) 
* [Mesosphere Universe Packages](https://github.com/mesosphere/universe/tree/version-2.x/repo/packages) 
* Or type `dcos package search` to get the current list for your configured repositories or `open-shmack-marathon-ui.sh` and select Universe on the left.
* ... but keep in mind the [limitations](#limitations)

<a name="nonImplFiles" />
## Where do I put my notes / non-implementation files when working on an issue (including User-Stories) ?
Into the `03_analysis_design/Issues` folder, see https://github.com/Zuehlke/SHMACK/tree/master/03_analysis_design/Issues
````
<git-repo-root>
  |- 03_analysis_design
     |- Issues
        |- Issue-<ID> - <any short description you like>
           |- Any files you like to work on
````

## How do I scale up/down the number of slave nodes?
`${HOME}/shmack/shmack-repo/scripts/change-number-of-slaves.sh <new number of slaves>`
**Attention**: Data in HDFS is **destroyed** when scaling down!!

## Which Java/Scala/Python Version can be used?
As of 2016-08-26 Java 1.8.0_51, Spark 2.0 with Scala 2.11.8, and Python 3.4 are deployed on the created stack.

<a name="sparkShell" />
## Can I run an interactive Spark Shell?
Not really. [Officially](https://docs.mesosphere.com/1.7/usage/service-guides/spark/limitations/), you should use graphical webfrontends [Zeppelin](https://docs.mesosphere.com/1.7/usage/service-guides/zeppelin/) or [Spark Notebook](https://github.com/andypetrella/spark-notebook/#mesosphere-dcos) instead. 
An [older blog posting](https://support.mesosphere.com/hc/en-us/articles/206118703-Launching-spark-shell-on-a-DCOS-master-node) showed some steps, but that never really worked for anything with parallel execution / using the master.

<a name="checkStackSetup" />
## What should I do to check if the setup and stack creation was successful?

`open-shmack-master-console.sh` and see if all services are healthy.

Unfortunately, due to [issue #16](https://github.com/Zuehlke/SHMACK/issues/16) tests that require RSync no longer work,
and that includes most of the infrastructure tests.
Once this is fixed, you may execute the testcase `ShmackUtilsTest` in your IDE. 
This will run some basic tests to check that your local setup is fine and can properly make use of a running stack in the AWS cloud.
If this testcase fails: see **[here](#inTestcasesFailing)**

## How can I execute Unit-Tests on a local Spark instance?
Look at the examples:
* `JavaSparkPiLocalTest`
* `WordCountLocalTest`
These will not require any running stack and can therefore also be performed without any instance on AWS.

<a name="sparkTests" />
## How can I execute Unit-Tests on a remote Spark instance (i.e. in the Amazon EC2 cluster)?
Look at the examples:
* `JavaSparkPiRemoteTest`
* `WordCountRemoteTest`
These **will require** a running stack, they will fail if instance on AWS are not yet (or no longer) available or cannot be access through SSH.

**THIS CURRENTLY DOESN'T WORK BECAUSE UPLOAD VIA RSYNC IS BROKEN**

Make sure that every thecase hase it's own `testcaseId`. This id needs only to be distinct only within one Test-Class.
```
String testcaseId = "WordCount-" + nSlices;
RemoteSparkTestRunner runner = new RemoteSparkTestRunner(JavaSparkPiRemoteTest.class, testcaseId);
```

To execute the tests do the following:
* invoke `gradle fatJarWithTests` (available as eclipse launch configuration)
* invoke the JUnit test from within eclipse (or your favorite IDE)

## How can I use `src/test/resources` on a remote Spark instance?
* Synchronize the  `src/test/resources` to the HDFS filesystem
  * `RemoteSparkTestBase#syncTestRessourcesToHdfs()`
  * Example: `WordCountRemoteTest#testWordcountRemote()`
  * Note that thanks to `rsync` **only changes** will be transferred from your laptop to the EC2 instance. This saves huge amounts of time and bandwith ;-). Nevertheless there is no efficient way to sync between the EC2-Master node and the EC2-HDFS Filesystem. But this should be no problem as bandwidth within the EC2 cluster is very high.
  
* Use the required ressource from within the Spark-Job (executed remotely) addressedb by the HDFS-URL , e.g.
```
   see WordCountRemoteTest#main():
	// resolves to hdfs://hdfs/spark-tests/resources/tweets/tweets_big_data_2000.json
    String inputFile = getHdfsTestRessourcePath("tweets/tweets_big_data_2000.json");
```

## How can I retrieve the results from a Unit-Test executed  on a remote Spark instance?
Use the `RemoteSparkTestRunner#`**`getRemoteResult()`** as follows:
* `executeSparkRemote(String...)`
* `waitForSparkFinished()`
* `getRemoteResult()`

Examples: 
* `JavaSparkPiRemoteTest`
* `WordCountRemoteTest`

## How does the JUnit-Test know when a Spark-Job is finished?
The `RemoteSparkTestRunner#executeWithStatusTracking()` is to be invoked by the spark Job. It writes the state of the spark job to the HDFS filesystem
The JUnit test uses the `RemoteSparkTestRunner` to poll the state, see `RemoteSparkTestRunner#waitForSparkFinished()`.

Examples: 
* `JavaSparkPiRemoteTest`
* `WordCountRemoteTest`

Nevertheless it can happen that due to a severe error, that the status in HDFS is not written.
In this case see **[here](#sparkJobsFailing)** 

## How can I execute command in the EC2 cluster from a local JUnit Test?

Use methods provided by `ShmackUtils`:
* `runOnMaster(CommandLine, ExecExceptionHandling)`
* `runOnMaster(ExecExceptionHandling, String, String...)`
* `runOnMaster(String, String...)`

These methods will typically throw an exception if the return code is not 0 (can be controlled using ExecExceptionHandling).


## How do I read / write files from / to the HDFS file system in the EC2 cluster?
You can do this ...
* ... either **locally** from your laptop:
  * from JUnit Tests: use  method provided by `ShmackUtils`, e.g.
    * `copyFromHdfs(File, File)`
    * `copyToHdfs(File, File)`
    * `syncFolderToHdfs(File, File)`
    * `syncFolderFromHdfs(File, File)`
    * `deleteInHdfs(File)`
    * `getHdfsURL(File)`
    * `readByteArrayFromHdfs(File)`
    * `readStringFromHdfs(File)`
    * Note that you can simply use a java.io.File to address files in HDFS, e.g. `/foo/bar.txt` will be written to the HDFS URL `hdfs://hdfs/foo/bar.txt`
  
  * from a bash:
    * `copy-from-hdfs.sh`
    * `copy-to-hdfs.sh`
    * `sync-from-hdfs-to-local.sh`
    * `sync-to-hdfs.sh`
    
* ... or from a Spark-Job executed **remote** in the EC2 cluster:
  *  use `com.zuehlke.shmack.sparkjobs.base.HdfsUtils`



# Troubleshooting
## I get a `SignatureDoesNotMatch` error in aws-cli.
In detail, stack operation reports somethning like:
`A client error (SignatureDoesNotMatch) occurred when calling the CreateStack operation: Signature expired: 20160315T200648Z is now earlier than 20160316T091536Z (20160316T092036Z - 5 min.)`

Likely the clock of your virtual maching is wrong. 

To fix this:
* Shutdown VM completely (reboot is *not* enough in VirtualBox)
* Start VM
* Now the clock of the VM should be OK and aws-cli should work fine again.

Just to be on the safe side, you should probably also update the AWS Commandline Interface:
* `sudo -H pip install --upgrade awscli`

## `create-stack` fails with some message I should run `dcos auth login`
Don't worry. This happens sometimes when you have created a DC/OS stack before and the credentials no longer fit.
It is a pain, but very easy to fix.

To fix this:
* Do as told, execute `dcos auth login`, login with a valid account (GitHub works great), copy the activation code, and paste to the shell.
* Run `init-dcos-stack.sh` to complete the creation of the full stack.


<a name="forgotCred" />
## I forgot my AWS credentials / closed the browser too early.
You can always setup new credentials without needing to setup a new account, so this is no big deal:
* Go to https://console.aws.amazon.com/iam/home?region=us-west-1
* Select your user
* In the tab Security Credentials click the button Create Access Key
* Open a shell and run `aws configure` again using the new credentials
* Optional: Delete the old access key that is no longer in use 

<a name="setupFailing" />
## What should I do if the setup of the stack has failed?
* Try to understand the failure and fix it. Goal: As much as possible is automated and others do not fall into the same issue.
* Delete the stack to make sure there are no costs, see **[here](#stackDeletion)**
* If you still habe time: Try to create the stack again from scratch, but do not forget the **[running costs](#avoidBill)**...


## What should I do if ssh does not work?
In most cases the reason for this is that ssh is blocked by corporate networks.
Solution: Unplug network cable and use `zred` WiFi.

## What should I do to check if the setup was successful?
Execute the testcase `ShmackUtilsTest` in eclipse.
If this testcase fails: see **[here](#inTestcasesFailing)**

<a name="inTestcasesFailing" />
## What should I do if Integration testcases do not work?
Be sure to have a [stack created](#stackCreation) successfully and confirmed idendity of hosts, see **[here](#confirmSsh)**


<a name="sparkJobsFailing" />
## What should I do if Spark-Jobs are failing?
* Open the mesos Web-UI `${HOME}/shmack/repo/04_implementation/scripts/open-shmack-mesos-console.sh`
* Click on the Link to the `sandbox` of your spark-job
* Click on `stderr`  
* Example see: **[here](https://github.com/Zuehlke/SHMACK/blob/master/03_analysis_design/Issues/Issue-7%20Spark%20Word%20Count/Issue-7%20Spark%20Word%20Count.docx)**

## To start with a clean state, you may delete the whole HDFS Filesystem as follows
`ssh-into-dcos-master.sh`
`hadoop fs -rm -r -f 'hdfs://hdfs/*'`

<a name="eclipseUbuntu1604" />
## Eclipse freezes with no apparent reason?
Are you running Ubuntu 16.04? 
Because there is a known issue of SWT not working properly on GTK3: http://askubuntu.com/questions/761604/eclipse-not-working-in-16-04

<a name="stuckInDeployment" />
## Some of my dcos services are stuck in deployment
Follow the log of a service like this:
`dcos service log --follow hdfs`
You will see the same piece of log being logged over and over again. Analyze it (look for "failed" or similar).

<a name="sparkOOM" />
## Your spark driver or executors are being killed
* Figure out through mesos console on what host (slave) the driver/executor executed on
* ssh into the slave (`ssh-into-slave <num>`)
* Look for oom messages like: *Memory cgroup out of memory: Kill process 26801 (java) score 1018 or sacrifice child* in `dmesg`
* Start increasing the memory of the driver/executor with --driver-memory / --executor-memory

___
* [github] - See other project from Zühlke on github
* [bitbucket] - ee other project from Zühlke on bitbucket

[github]:https://github.com/zuehlke-ch
[bitbucket]:https://bitbucket.org/zuehlke/

<a name="license"></a>
# License Details

Copyright 2016

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
