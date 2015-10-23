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
* To create a setup according to the tutorial follow the steps [here](https://mesosphere.com/amazon/setup/)
* TODO automated setup from scratch, see https://github.com/Zuehlke/SHMACK/issues/1


##### Affiliate
* Focusgroup - Big Data / Cloud
* Team - TODO
* Initiator - wgi

#<font color="red">WARNING: things can get expensive $$$$$ !</font>
When setting up the tutorial servers on Amazon AWS and letting them running, there will be monthly costs of approx **1700 $** !
Please make sure that servers are only used as required. See [FAQ](#avoidBill) section in this document.

# Links #
* [Mesosphere Homepage](https://mesosphere.com/)
* [Documentation](http://docs.mesosphere.com/)
* [Tutorials](https://docs.mesosphere.com/tutorials/)
* Articles
  * [MESOSPHERE DATACENTER OPERATING SYSTEM IS NOW GENERALLY AVAILABLE](https://mesosphere.com/blog/2015/06/09/the-mesosphere-datacenter-operating-system-is-now-generally-available/)
  * [MEET A NEW VERSION OF SPARK, BUILT JUST FOR THE DCOS](https://mesosphere.com/blog/2015/06/15/meet-a-new-version-of-spark-built-just-for-the-dcos/)
  * [EVERYTHING YOU NEED TO KNOW ABOUT SCALA AND BIG DATA](https://mesosphere.com/blog/2015/07/24/learn-everything-you-need-to-know-about-scala-and-big-data-in-oakland/)
  * [APPLE DETAILS HOW IT REBUILT SIRI ON MESOS](https://mesosphere.com/blog/2015/04/23/apple-details-j-a-r-v-i-s-the-mesos-framework-that-runs-siri/)



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


___
* [github] - See other project from Zühlke on github
* [bitbucket] - ee other project from Zühlke on bitbucket

[github]:https://github.com/zuehlke-ch
[bitbucket]:https://bitbucket.org/zuehlke/
