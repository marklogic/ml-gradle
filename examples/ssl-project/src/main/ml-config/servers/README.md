The rest-api-server.json file uses a special token that references the ID of a certificate template. This token is
set during mlDeploy - specifically, after the certificate template has been created or updated. This solves a problem
where the config file must reference an ID, but there's no way to know what that ID is until the certificate template
has been created. 

The downside to this approach is that you cannot run a task that updates the server if that task does not also run
a command to create/update the certificate template; otherwise, ml-gradle will not have a value with which to replace
the token. 
