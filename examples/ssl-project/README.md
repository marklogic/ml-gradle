This project demonstrates how to use a certificate template to enable SSL connections on an app server. 

The project currently uses the `mlSimpleSsl` property to tell ml-gradle to use a "trust everything" approach for 
communicating with the project's REST API server. This is only used for demonstration purposes and is not recommended
for a production environment. See 
[this guide on SSL with ml-gradle](https://github.com/marklogic/ml-gradle/wiki/Loading-modules-via-SSL) for 
information on properly configuring SSL usage. 

Note the inclusion of an `ext` block in this project's `build.gradle` file. This automates the generation of a temp 
certificate for the certificate template. This can also be accomplished by updating the app server via the MarkLogic
Admin UI; the Admin UI will check for a temp for the app server's certificate template and will create one if it does
not yet exist. 
