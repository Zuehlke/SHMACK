# Setup Git for commandline usage: 
source of commands: https://help.github.com/articles/set-up-git/
* `git config --global user.name "YOUR NAME"`
* `git config --global user.email "your_GITHUB_email_address@example.com"`
* `git config --global push.default simple`
* Setup github Credentials
  * `git config --global credential.helper cache`
  * `git config --global credential.helper 'cache --timeout=43200'`  (cache 1 day)

# Recommended GUIs 
You may consider installing an additional git GUI - in particulat when you are neither happy with `git gui` or whatever is supported in your preferred IDE. 

Easy tools to setup on Ubuntu Linux available via `apt-get install`
* gitg
* giggle
* git-cola 

On Mac OS X or Windows, [Atlassian SourceTree](https://www.sourcetreeapp.com) works nice.

 
More clients listed on https://git-scm.com/downloads/guis.
