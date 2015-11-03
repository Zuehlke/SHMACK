# S.H.M.A.C.K

S. = Spark
H. = Hatch
M. = Mesos
A. = Akka
C. = Cassandra
K. = Kafka

## Trying some default stack for Big Data prototypes (May come out different from the above).

#<font color="red">WARNING: things can get expensive $$$$$ !</font>
When setting up the tutorial servers on Amazon AWS and letting them running, there will be monthly costs of approx **1700 $** !
Please make sure that servers are only used as required. See [FAQ](#avoidBill) section in this document.

# What do I need to read before working on this project? --> Exactly THIS! #
* Backlog is an Excel-File in order to prioritize and filter issues: **[here](#backlog)**
* Details for Backlog-Items are github issues with a link from the Backlog
* Any files used for work on specific issues can be found **[here](https://github.com/Zuehlke/SHMACK/tree/master/03_analysis_design/Issues)**, see also **[FAQ](#nonImplFiles)**
* Whatever can be automated shall be automated and checked in the `04_implementation` folder **[here](https://github.com/Zuehlke/SHMACK/tree/master/04_implementation)** (common sense may be applied ;-)
* The [Vision](#vision)
* [Installation instructions](#installation)

# Vision #
* We want like to be fast when ramping up cloud infrastructure.
* We do not want to answer "we never did this" to customers when asked.
* We want to know where the issues and traps are when setting up cloud infrastructure with the SHMACK stack.
* We want to create a RUA for Machine Learning to acquire customers and show competence.

* **@wgi: TODO Please correct / append this vision.**

### Installation

#### To be done once:
* Create AWS account **[here](https://aws.amazon.com/de/)**
* Create a Virtual Machine 
  * Recommended: **[Ubuntu >= 14.04.3 LTS](http://www.ubuntu.com/download/desktop)** with VMWare-Player
  * Alternative: **[LinuxMint >= 17.02](http://www.linuxmint.com/download.php)** with VirtualBox 
  * **ATTENTION**: Do NOT only start the OS from the downloaded ISO image. INSTALL the OS to the virtual machine on the virtual machine's harddisk.
  * Hint: If Copy/Paste does not work, check whether VM-tools are installed.
* In the Virtual machine
  * `sudo apt-get install xsel`
  * setup GIT (source of commands: https://help.github.com/articles/set-up-git/ )
    * `git config --global user.name "YOUR NAME"`
    * `git config --global user.email "your_GITHUB_email_address@example.com"`
    * `git config --global push.default simple`
    * Setup github Credentials
      * `git config --global credential.helper cache`
      * `git config --global credential.helper 'cache --timeout=43200'`  (cache 1 day)
  * `mkdir ${HOME}/shmack`
  * `cd ${HOME}/shmack && git clone https://github.com/Zuehlke/SHMACK.git repo`
  * `cd ${HOME}/shmack/repo/04_implementation/scripts && sudo -H bash ./setup-ubuntu.sh`
  * Setup AWS console (Source: http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html )
    * Create AWS user including AWS Access Key (can be deleted to revoce access from VM)
      * https://console.aws.amazon.com/iam/home?#users
      * Username: `shmack`
      * **DON'T TOUCH mouse or keyboard - LEAVE THE BROWSER OPEN** (credentials are shown only here, optionally download credentials and store them in a safe place only for you)
    * `aws configure`
      * `AWS Access Key ID [None]: [from browser page]`
      * `AWS Secret Access Key [None]: [from browser page]`
      * `Default region name [None]: **us-west-1**`  (VERY important, DO NOT change this!)
      * `Default output format [None]: json`
    * Assign Admin-Permissions to user `smack`: 
https://console.aws.amazon.com/iam/home?#users/shmack 
    * Create a AWS Key-Pair in region **us-west-1**: 
https://us-west-1.console.aws.amazon.com/ec2/v2/home?region=us-west-1#KeyPairs:sort=keyName
      * Name: `shmack-key-pair-01`

    
#### Stack Creation and Deletion 
##### Stack Creation
  * `${HOME}/shmack/repo/04_implementation/scripts/create-stack.sh`
  * Open URL as instructed in `Go to the following link in your browser:` and enter verification code.
  * `Modify your bash profile to add DCOS to your PATH? [yes/no]` --> yes
  * Confirm cassandra installation: `Continue installing? [yes/no]` --> yes
  
##### Stack Deletion
  * `${HOME}/shmack/repo/04_implementation/scripts/delete-stack.sh`


#### Affiliate
* Focusgroup - Big Data / Cloud
* Team - TODO
* Initiator - wgi

# Links #
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


# Glossary
| Term | Definition |
|--------|--------|
| Issue  | = Can be a **"User Story"** (to be in sync with scrum and github terminology) or a **Bug**|


# Important Limitations / Things to consider before going productive
* As of 2015-10-28 the DCOS stack does **NOT work in AWS Region `eu-central-1` (Frankfurt)**. Recommended region to try is `us-west-1`. Take care of **regulatory issues** (physical location of data) when thinking about a real productive System.
* What if the number of client request "explodes". Is there a way to do autoscaling with DCOS / Mesophere WITHOUT human interaction?


# FAQ
## How do I avoid to be surprised by a monthly bill of **1700 $** ?<a name="avoidBill"></a>
As of 2015-10-23 there is **no** officially supported way to suspend AWS EC2 instances.
see [Stackoverflow](http://stackoverflow.com/questions/31848810/mesososphere-dcos-cluster-on-aws-ec2-instances-are-terminated-and-again-restart) and [Issue](https://github.com/Zuehlke/SHMACK/issues/2)

The only official supported way to stop AWS bills is to completely delete the stack.
**ATTENTION**: 
* To delete a stack it is not sufficient to just terminate the EC2 instances as they are contained in an autoscaling group.
* To delete a stack 
  * go to https://console.aws.amazon.com/cloudformation/ and delete the stack
  * make sure that there are no autoscaling groups left: https://us-west-1.console.aws.amazon.com/ec2/autoscaling/home

## Where is the Backlog?<a name="backlog"></a>
The Backlog is an Excel-File which contains for each story
- the short name 
- the Category 
- the link to the Issue in github (which contains the details description of the story)

We use the Excel-Format due to the following reasons: 
1. we want to prioritize the issues
2. we want to filter for open issues only (otherwise the backlog would become too long)
3. we do not (yet) want to introduce another tool like trello to keep thing simple and together.

## Where do I put my notes / non-implementation files when working on an issue (including User-Stories) ?<a name="nonImplFiles"></a>
Into the `03_analysis_design/Issues` folder, see https://github.com/Zuehlke/SHMACK/tree/master/03_analysis_design/Issues
````
<git-repo-root>
  |- 03_analysis_design
     |- Issues
        |- Issue-<ID> - <any short description you like>
           |- Any files you like to work on
````

# Troubleshooting
## I get a `SignatureDoesNotMatch` error in aws-cli.
Likely the clock of your virtual maching is wrong. 
To fix is:
* Shutdown VM completely (reboot is *not* enough in VirtualBox)
* Start VM
* Now the clock of the VM should be OK and aws-cli should work fine again.
___
* [github] - See other project from Zühlke on github
* [bitbucket] - ee other project from Zühlke on bitbucket

[github]:https://github.com/zuehlke-ch
[bitbucket]:https://bitbucket.org/zuehlke/
