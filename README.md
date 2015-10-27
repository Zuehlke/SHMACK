# S.H.M.A.C.K

S. = Spark
H. = Hatch
M. = Mesos
A. = Akka
C. = Cassandra
K. = Kafka

## Trying some default stack for Big Data prototypes (May come out different from the above).

### Installation
* **Always** create the AWS clusters in Region **"us-west-1"** as "eu-central-1" (Frankfurt) does not work (instances are created and rolled back without any obvious reason)
* To create a setup according to the tutorial follow the steps [here](https://mesosphere.com/amazon/setup/) TODO: Change this to use `initial_setup.sh`
* TODO automated setup from scratch, see https://github.com/Zuehlke/SHMACK/issues/1


##### Affiliate
* Focusgroup - Big Data / Cloud
* Team - TODO
* Initiator - wgi

#<font color="red">WARNING: things can get expensive $$$$$ !</font>
When setting up the tutorial servers on Amazon AWS and letting them running, there will be monthly costs of approx **1700 $** !
Please make sure that servers are only used as required. See [FAQ](#avoidBill) section in this document.

# What do I need to read before working on this project? --> Exactly THIS! #
* Backlog is an Excel-File in order to prioritize and filter issues: **[here](#backlog)**
* Details for Backlog-Items are github issues with a link from the Backlog
* Any files used for work on specific issues can be found **[here](https://github.com/Zuehlke/SHMACK/tree/master/03_analysis_design/Issues)**, see also **[FAQ](#nonImplFiles)**
* Whatever can be automated shall be automated and checked in the `04_implementation` folder **[here](https://github.com/Zuehlke/SHMACK/tree/master/04_implementation)** (common sense may be applied ;-)
* The [Vision][#vision]

# Vision #
We want like to be fast when ramping up cloud infrastructure.
We do not want to answer "we never did this" to customers when asked.
We want to know where the issues and traps are when setting up cloud infrastructure with the SHMACK stack.
**@wgi: TODO Please correct / append this vision.**

# Links #
* [Mesosphere Homepage](https://mesosphere.com/)
* [Documentation](http://docs.mesosphere.com/)
* [Tutorials](https://docs.mesosphere.com/tutorials/)
* Articles
  * [MESOSPHERE DATACENTER OPERATING SYSTEM IS NOW GENERALLY AVAILABLE](https://mesosphere.com/blog/2015/06/09/the-mesosphere-datacenter-operating-system-is-now-generally-available/)
  * [MEET A NEW VERSION OF SPARK, BUILT JUST FOR THE DCOS](https://mesosphere.com/blog/2015/06/15/meet-a-new-version-of-spark-built-just-for-the-dcos/)
  * [EVERYTHING YOU NEED TO KNOW ABOUT SCALA AND BIG DATA](https://mesosphere.com/blog/2015/07/24/learn-everything-you-need-to-know-about-scala-and-big-data-in-oakland/)
  * [APPLE DETAILS HOW IT REBUILT SIRI ON MESOS](https://mesosphere.com/blog/2015/04/23/apple-details-j-a-r-v-i-s-the-mesos-framework-that-runs-siri/)


# Glossary
| Term | Definition |
|--------|--------|
| Issue  | = Can be a **"User Story"** (to be in sync with scrum and github terminology) or a **Bug**|



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

___
* [github] - See other project from Zühlke on github
* [bitbucket] - ee other project from Zühlke on bitbucket

[github]:https://github.com/zuehlke-ch
[bitbucket]:https://bitbucket.org/zuehlke/
