# S.H.M.A.C.K

S. = Spark

H. = Hatch

M. = Mesos

A. = Akka

C. = Cassandra

K. = Kafka

## A modern stack for Big Data applications

SHMACK is open source under terms of Apache License 2.0 (see **[License Details](#license)**).
For now, it provides a quick start to set up a Mesos cluster with Spark and Cassandra on Amazon Web Services (AWS), 
with the intention to cover the full SMACK stack (Spark, Mesos, Akka, Cassandra, Kafka - also known as [Mesosphere Infinity](https://mesosphere.com/infinity/) [stack](https://mesosphere.com/blog/2015/08/20/mesosphere-infinity-youre-4-words-away-from-a-complete-big-data-system/))
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
* We want to create a reusable asset for Big Data combined with cloud-scale Data Analytics and Machine Learning to acquire customers and be able to show competence not only on paper, but running in the cloud.

# Installation
Everything can be performed free of charge until you start up nodes in the cloud (called [Stack creation](stackCreation)). 


## Register accounts
* Create GitHub account (if you don't have one yet): https://github.com/join
* Create AWS account **[here](https://aws.amazon.com/de/)**


<a name="devEnvSetup" />
## Development Environment setup
You will need a (for now) a Linux machine to control and configure the running SHMACK stack. 
You will also need that in order to develop and contribute. 

### Create a Virtual Machine
* assign at least 4 GB RAM and 30 GB HDD! 
* Recommended: **[Ubuntu >= 15.10 LTS](http://www.ubuntu.com/download/desktop)** with VMWare-Player
  * Would prefer an LTS version, but **14.04 is too outdated and some of the installed certificates are no longer accepted**.
  * Maybe 16.04 LTS will work fine again. But until it comes out, better stick to 15.10 which is known to cause no major headaches. 
* Alternative: any other recent Linux (native, or virtualized - VirtualBox is also fine) 
  * **ATTENTION**: Do NOT only start the OS from the downloaded ISO image. INSTALL the OS to the virtual machine on the virtual machine's harddisk.
  * **ATTENTION**: The AWS and DCOS Commandline Tools (CLI) use Python with many dependencies installed and maintained through pip. 
    This may cause problems when the OS provides already some of the used libraries in older version - why it is not always possible to mix those. For instance, CoreOS and OS X unfortunately don't get along right now.
### In the Virtual machine
* `sudo apt-get install git`
* `mkdir ${HOME}/shmack && cd ${HOME}/shmack && git clone https://github.com/Zuehlke/SHMACK.git repo`
* `cd ${HOME}/shmack/repo/04_implementation/scripts && sudo -H bash ./setup-ubuntu.sh`
  * This will install among others the AWS Commandline Tools, OpenJDK 8, and Scala
  * If you don't start with a fresh image, it's probably better to have a look in `setup-ubuntu.sh` and see yourself what is missing - and install only missing bits.
* Optional: DCOS provides the cluster on AWS currently with Oracle java version "1.8.0_51", so better use same or newer; 
  for installing the same version, follow http://askubuntu.com/questions/56104/how-can-i-install-sun-oracles-proprietary-java-jdk-6-7-8-or-jre
* Optional: DCOS provides the cluster on AWS currently with Scala version "2.10.5", so better use same or newer
* Optional: When you like working on shell, append the following lines at the **end** of your `${HOME}/.bashrc`
```
alias cds='cd ${HOME}/shmack/repo/'
alias eclipse='nohup ${HOME}/eclipse/eclipse > /dev/null 2>&1 &'
PATH=${PATH}:${HOME}/shmack/repo/04_implementation/scripts:${HOME}/shmack/repo/04_implementation/scripts/target/dcos/bin
export PATH
```
* Optional: setup git for commandline usage (source of commands: https://help.github.com/articles/set-up-git/ )
  * `git config --global user.name "YOUR NAME"`
  * `git config --global user.email "your_GITHUB_email_address@example.com"`
  * `git config --global push.default simple`
  * Setup github Credentials
    * `git config --global credential.helper cache`
    * `git config --global credential.helper 'cache --timeout=43200'`  (cache 1 day)
  * You may consider installing an additional git GUI. Easy tools to setup on Linux are gitg, giggle, and git-cola (just apt-get them); on Mac OS X or Windows, [Atlassian SourceTree](https://www.sourcetreeapp.com) works nice. 
    More clients listed on https://git-scm.com/downloads/guis.
* Optional: When intending to create new packages for the stack on DCOS / deploy applications, 
    install Docker: https://docs.docker.com/engine/installation/linux/ubuntulinux/

### Setup AWS console (Source: http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html )
* Create AWS user including AWS Access Key (can be deleted to revoce access from VM)
  * https://console.aws.amazon.com/iam/home?#users
  * Username: `shmack`
  * **DON'T TOUCH mouse or keyboard - LEAVE THE BROWSER OPEN** (credentials are shown only here, optionally download credentials and store them in a safe place only for you)
* `aws configure`
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

### Download, install, and configure Eclipse for the use in SHMACK
* Download `Eclipse IDE for Java EE Developers` as 64 bit for Linux from https://www.eclipse.org/downloads/ 
* Extract eclipse: `cd ${HOME}; tar xvfz Downloads/eclipse-jee-mars-2-linux-gtk-x86_64.tar.gz` 
* Add gradle support to eclipse
  * open `eclipse/eclipse`
  * Open `Help --> Eclipse Marketplace`
  * Install `Gradle (STS) IDE Pack`
* Import Gradle projects from `${HOME}/shmack/repo/04_implementation` into eclipse
  * "Import" --> "Gradle (STS) Project"
  * Click "Build model"
  * Select all projects
* Optional: Install [DLTK ShellEd](#http://www.eclipse.org/dltk/install.php) for Eclipse
  * Provides a nice support for editing shell scripts in Eclipse
  * Install new software... Add `http://download.eclipse.org/technology/dltk/updates-dev/latest/`
  * Select "ShellEd IDE" and "Python IDE" and "next" to install
* Optional: Install Eclipse support for Scala from http://scala-ide.org/download/current.html
  * Install new software... Add `http://download.scala-ide.org/sdk/lithium/e44/scala211/stable/site`
  * Select "Scala IDE for Eclipse" which will install sbt, Scala 2.11, and the IDE plugins
    
## Stack Creation and Deletion 
Mesosphere provides AWS CloudFormation templates to create a stack with several EC2 instances in autoscaling groups, 
some of directly accessible (acting as gateways), others only accessible through the gateway nodes.¨
The scripts for SHMACK will not only create/delete such a stack, but also maintain the necessary IDs to communicate and 
setup dcos packeges to form SHMACK.

<a name="stackCreation" />
### Stack Creation (from now on, you pay for usage)
  * Execute `${HOME}/shmack/repo/04_implementation/scripts/create-stack.sh`
    * Wait approx. 10 Minutes
    * **Do NOT interrupt the script!** (especially do **NOT** press Ctrl-C to copy the instructed URL!)
    * In case of failures see [Troubleshoting Section](#setupFailing)
  * In order to automatically install, update, and configure the [DCOS Commandline Interface](https://docs.mesosphere.com/administration/cli/install-cli/), 
    you will be prompted to enter your passwword as part of the installation process needs to run via sudo.  
  * Open URL as instructed in `Go to the following link in your browser:` and enter verification code.
  * DCOS will now install the packages defined in `create-stack.sh`
  	* Confirm optional installations (if desired): `Continue installing? [yes/no]` --> yes
    * Even after the command returns, it will still take some time until every package is fully operational
    * In the Mesos Master UI you will see them initially in status "Idle" or "Unhealthy" until they converge to "Healthy",
      in particular Spark, HDFS, and Cassandra will need time until replications is properly initialized 
  * <a name="confirmSsh"></a>Login once using ssh (in order to add mesos master to known hosts, needed among others for unit tests to run without manual interaction)
    * `${HOME}/shmack/repo/04_implementation/scripts/ssh-into-dcos-slave.sh 0`
    * Confirm SSH security prompts
    * Logout from the cluser (press `Ctrl-d` or type `exit` twice)
  * Optional: Check whether stack creation was successful, see **[here](#checkStackSetup)** 
  
<a name="stackDeletion" />
### Stack Deletion
  * Option 1 (recommended):
    `${HOME}/shmack/repo/04_implementation/scripts/delete-stack.sh`
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
* [Documentation](http://docs.mesosphere.com/)
* [Tutorials](https://docs.mesosphere.com/tutorials/)
* [DCOS Service Availability](https://docs.mesosphere.com/reference/servicestatus/)
* Articles
  * [MESOSPHERE DATACENTER OPERATING SYSTEM IS NOW GENERALLY AVAILABLE](https://mesosphere.com/blog/2015/06/09/the-mesosphere-datacenter-operating-system-is-now-generally-available/)
  * [MEET A NEW VERSION OF SPARK, BUILT JUST FOR THE DCOS](https://mesosphere.com/blog/2015/06/15/meet-a-new-version-of-spark-built-just-for-the-dcos/)
  * [EVERYTHING YOU NEED TO KNOW ABOUT SCALA AND BIG DATA](https://mesosphere.com/blog/2015/07/24/learn-everything-you-need-to-know-about-scala-and-big-data-in-oakland/)
  * [APPLE DETAILS HOW IT REBUILT SIRI ON MESOS](https://mesosphere.com/blog/2015/04/23/apple-details-j-a-r-v-i-s-the-mesos-framework-that-runs-siri/)
* Other Ressources
  * AMP Lab - Reference Architecture: https://amplab.cs.berkeley.edu/software/
  * AMP Lap Camp with exercices: http://ampcamp.berkeley.edu/5/
  * Public Datasets (S3): https://aws.amazon.com/de/public-data-sets/
  * Reference Architecture for Netflix Style recommendation engines: https://github.com/fluxcapacitor
  * Apache Spark Example Use Cases (with Code): https://github.com/4Quant
  * Apache Spark Twitter Word Count: https://github.com/snowplow/spark-example-project

<a name="limitations" />
# Important Limitations / Things to consider before going productive
* As of 2015-10-28 the DCOS stack does **NOT work in AWS Region `eu-central-1` (Frankfurt)**. Recommended region to try is `us-west-1`. Take care of **regulatory issues** (physical location of data) when thinking about a real productive System.
* What if the number of client request "explodes". Is there a way to do autoscaling with DCOS / Mesophere WITHOUT human interaction?
* As of 2015-11-13 **all data in HDFS is lost** when scaling down, e.g. from 10 to 5 Slave nodes. This is a blocking issue. If unresolved productive use of the Stack is not possible. see **[here](https://github.com/Zuehlke/SHMACK/blob/master/03_analysis_design/Issues/Issue-10%20HDFS-Access/Scaling%20Test.docx)** According to the mesosphere development team (chat), this issue is addressed by **[maintenance primitives](https://mesosphere.com/blog/2015/10/07/mesos-inverse-offers/)**. But it is not clear when it will be finished.
* Make sure that admin access to the Mesos Master console is secure. As of 2015-11-27 only **passwordless** http access is possible. https needs to be implemented.
* Data Locality, e.g. How do we minimze latency between data storage and Spark workers?


# FAQ
<a name="avoidBill" />
## How do I avoid to be surprised by a monthly bill of **1700 $** ?
Check regularly the [Billing and Cost Dashboard](https://console.aws.amazon.com/billing/home), which Amazon will update daily. You also install the [AWS Console Mobile App](https://aws.amazon.com/console/mobile/) to even have an eye on the running instances and aggregated costs when you are sitting at your desk - and take actions if needed. 

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
	* Mail them the credentials including their `shmack-key-pair-01.pem`, AWS Access Key ID, and AWS Secret Access Key
	* They will have to use them as described for [lost credentials](#forgotCred).
* [Create your stack](#stackCreation) and exchange the state
	* Use `capture-stack-state.sh` and distribute the generated file `stack-state.tgz`
	* They will have to use `populate-copied-stack-state.sh` to make use of the shared cluster
* Finally, when you are all done
	* One of you has to [Delete the stack](#stackDeletion) 
	* Delete/inactivate the additional accounts in htps://console.aws.amazon.com/iam/home?region=us-west-1

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
`${HOME}/shmack/repo/04_implementation/scripts/change-number-of-slaves.sh <new number of slaves>`
**Attention**: Data in HDFS is **destroyed** when scaling down!!

## Which Java/Scala/Python Version can be used?
As of 2016-03-08 Java 1.8.0_51, Spark 1.6 with Scala 2.10.5, and Python 3.4 are deployed on the created stack.

<a name="sparkShell" />
## Can I run an interactive Spark Shell?
Yes, you can - but unfortunately this is rather a smoke test than a usable environment so far.
* See https://support.mesosphere.com/hc/en-us/articles/206118703-Launching-spark-shell-on-a-DCOS-master-node
```
ssh-into-dcos-master.sh
curl -O http://d3kbcqa49mib13.cloudfront.net/spark-1.6.0-bin-hadoop2.6.tgz
tar xzf spark-1.6.0-bin-hadoop2.6.tgz
cd spark-1.6.0-bin-hadoop2.6/bin/
./spark-shell --master mesos://<local-ip>:5050
```
* You can run simple commands with that
```
scala> scala.util.Properties.versionString
res0: String = version 2.10.5
```
* But executing distributed tasks will fail
```
scala> sc.parallelize(0 to 10, 8).count
```
will complain with
```WARN TaskSchedulerImpl: Initial job has not accepted any resources; check your cluster ui
to ensure that workers are registered and have sufficient memory```

**TODO** Figure out, what's wrong. Tried to set [coarse grained mode and docker image](http://spark.apache.org/docs/latest/running-on-mesos.html) as `--conf` options, but didn't help.  

* [Apache Zeppelin](http://zeppelin.incubator.apache.org/) can replace the need for interactive data analysis and provide even nice visualizations. You access Zeppelin on your running stack simply with: `open-shmack-zeppelin.sh`
The URL of Zeppelin will be available also outside the VM.


<a name="checkStackSetup" />
## What should I do to check if the setup was successful?
Execute the testcase `ShmackUtilsTest` in eclipse.
If this testcase fails: see **[here](#inTestcasesFailing)**

## How can I execute Unit-Tests on a local Spark instance?
Look at the examples:
* `JavaSparkPiLocalTest`
* `WordCountLocalTest`

## How can I execute Unit-Tests on a remote Spark instance (i.e. in the Amazon EC2 cluster)?
Look at the examples:
* `JavaSparkPiRemoteTest`
* `WordCountRemoteTest`

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


<a name="forgotCred" />
## I forgot my AWS credentials / closed the browser too early.
You can always setup new credentials without needing to setup a new account, so this is no big deal:
* Go to https://console.aws.amazon.com/iam/home?region=us-west-1
* Select your user
* In the tab Security Credentials click the button Create Access Key
* Open a shell and run `aws configure` again using the new credentials
* Optional: Delete the old access key that is no longer in use 

<a name="setupFailing"></a>
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

<a name="inTestcasesFailing"></a>
## What should I do if Integration testcases do not work?
Be sure to have confirmed idendity of hosts, see **[here](#confirmSsh)**


<a name="sparkJobsFailing"></a>
## What should I do if Spark-Jobs are failing?
* Open the mesos Web-UI `${HOME}/shmack/repo/04_implementation/scripts/open-shmack-mesos-console.sh`
* Click on the Link to the `sandbox` of your spark-job
* Click on `stderr`  
* Example see: **[here](https://github.com/Zuehlke/SHMACK/blob/master/03_analysis_design/Issues/Issue-7%20Spark%20Word%20Count/Issue-7%20Spark%20Word%20Count.docx)**

## To start with a clean state, you may delete the whole HDFS Filesystem as follows
`ssh-into-dcos-master.sh`
`hadoop fs -rm -r -f 'hdfs://hdfs/*'`
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
