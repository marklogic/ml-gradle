Example of configuring a project to deploy an app to MarkLogic Cloud.

The app servers are modified to use digestbasic authentication solely to facilitate testing 
with the `ReverseProxyServer` test program in the Java Client repository. That program does not 
support digest authentication, so basic must be supported by the ML app servers.

Note that if the above is true, you need to manually set the Manage app server to use digestbasic.
