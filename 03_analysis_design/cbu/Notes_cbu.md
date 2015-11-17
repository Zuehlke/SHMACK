# Connect to github using ssh:
      * **Option 2: SSH-Connection** (Pro: fine control, Con: does not work in Corporate network as SSH is required)
        * Create a SSH Key Pair (accept all defaults)
        * `ssh-keygen -t rsa -b 4096 -C "your_GITHUB_email_address@example.com"`
        * Associate Key Pair with github:
          * `ssh-agent -s`
          * `ssh-add ~/.ssh/id_rsa`
          * `xsel --clipboard < ~/.ssh/id_rsa.pub`  (copies public key into clipboard)
          * Open SSH Settings and Add Key from clipboard: https://github.com/settings/ssh
          * Test it: `ssh -T git@github.com` (Successful if your github is displayed as output)

# Install Java 8
(taken from https://wiki.ubuntuusers.de/Java/Installation/Oracle_Java/Java_8 )
    * Download the `*.-x64.tgz` from http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html, e.g. `jdk-8u65-linux-x64.tar.gz`
    * Extract and install the downloaded file to /usr/java
```
sudo apt-get -y purge openjdk*
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get -y install oracle-java8-installer
sudo apt-get -y install oracle-java8-set-default
```
    * Check installation: `java -version`
