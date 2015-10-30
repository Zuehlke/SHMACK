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
