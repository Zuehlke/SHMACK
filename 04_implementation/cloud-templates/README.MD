This folder contains patched files of the official 
<https://s3.amazonaws.com/downloads.mesosphere.io/dcos/stable/cloudformation/single-master.cloudformation.json>
... may need a refresh every now and then to align with new versions of DC/OS.

Main purpose is to expose more parameters, so it is easy to create a new stack with different instance types and  (optionally) using spot instances.

In order to use and adjust this, you need to create an S3 bucket to upload the CloudFormation template.
open S3 console <https://console.aws.amazon.com/s3/home?region=us-west-1>, click 'Create Bucket' and give it a (globally!) unique name. Then open the properties and add permission for grantee 'Everyone' to list this bucket.

Finally upload/synchronize the content of this folder:
> aws s3 sync cloud-templates s3://bucketname