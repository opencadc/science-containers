# Direct URL Upload/Download 

A direct url is available for files on the /arc storage system, i.e.,
those in the home or project directories of the Science Portal. A file
in your home directory would be located at
`https://ws-uv.canfar.net/arc/files/home/[username]/[myfile]` and
similarly a file in your project directory would be located at
`https://ws-uv.canfar.net/arc/files/projects/[projectname]/[myfile]`

You can upload or download files directly using this URL, but note that
all uploads will require authentication and write permission at the
target directory, while downloads may require authentication, depending
on the read permissions of the file.

Here is an example of how to use the curl command to upload a file
called `myfile.txt` from your local computer to your home directory on
the Science Portal. First, make sure your permissions are set up,
supplying your CADC password at the prompt:

    cadc-get-cert -u [username] 

Then upload the file:

    curl -E ~/.ssl/cadcproxy.pem https://ws-uv.canfar.net/arc/files/home/[username]/myfile.txt -T myfile.txt
