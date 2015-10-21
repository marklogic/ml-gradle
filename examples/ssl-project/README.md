This project shows how to use a certificate template to enable SSL connections on an app server (in this case, the
REST API server). Two things to note in this project:

1. The src/main/ml-config/servers/rest-api-server.json config file shows an example of referring to the ID of a 
certificate template.
1. The gradle.properties file shows how the mlSimpleSsl property is used to tell ml-gradle to use an SSL connection 
when loading modules. 