## What is ml-app-deployer?

ml-app-deployer is a Java library that provides two capabilities:

1. A client library for the new [Management REST API](http://docs.marklogic.com/REST/management) in MarkLogic 8. 
1. A command-driven approach for deploying and undeploying an application to MarkLogic that depends on the management client library.

If you're just looking for a Java library for interacting with the Management REST API, you can certainly use ml-app-deployer. The deployer/command library is mostly a thin layer around the management client library and can be safely ignored if you don't need it. 

Javadocs for ml-app-deployer are [here](http://rjrudin.github.io/marklogic-java/javadocs/ml-app-deployer/index.html).

## What does it depend on? 

ml-app-deployer depends on MarkLogic 8 and Java 1.7+.

Under the hood, it depends on Spring's [RestTemplate](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) for interacting with the Management REST API. It also depends on [ml-javaclient-util](https://github.com/rjrudin/ml-javaclient-util) for loading modules, which is done via the MarkLogic Client REST API. 

## How do I start using the client library?

The general pattern for using the management client library is:

1. Create an instance of [ManageConfig](https://github.com/rjrudin/ml-app-deployer/blob/master/src/main/java/com/marklogic/mgmt/ManageConfig.java), which specifies connection information for the management REST API instance.
2. Create an instance of [ManageClient](https://github.com/rjrudin/ml-app-deployer/blob/master/src/main/java/com/marklogic/mgmt/ManageClient.java) using ManageConfig. ManageClient simply wraps a RestTemplate with some convenience methods.
3. Using ManageClient, create a Manager class based on the management resource you want to configure. For example, to create or modify or delete a database, create a [DatabaseManager](https://github.com/rjrudin/ml-app-deployer/blob/master/src/main/java/com/marklogic/mgmt/databases/DatabaseManager.java) to talk to the [database endpoints](http://docs.marklogic.com/REST/management/databases). 

Here's a brief example of what that looks like:

    ManageConfig config = new ManageConfig(); // defaults to localhost/8002/admin/admin
    ManageClient client = new ManageClient(config);
    DatabaseManager dbMgr = new DatabaseManager(client);
    dbMgr.save("{\"database-name\":\"my-database\"}");

## How do I start using the deployer library?

The main concept behind the deployer library is invoke a series of commands, where each command looks for one or more configuration files in a specific directory structure and then uses a Manager class in the client library to apply those configuration files as part of deploying an application. 

The best way to understand that directory is to look at the [sample-app application](https://github.com/rjrudin/ml-app-deployer/tree/master/src/test/resources/sample-app/src/main/ml-config) that's used by the JUnit tests. The concept is fairly simple - within the ml-config directory, there's a directory for each of the top-level resources defined by the [Management API docs](http://docs.marklogic.com/REST/management). Thus, database config files are found under "databases", while scheduled task config files are found under "scheduled-tasks". Some directories have subdirectories based on how the Management API endpoints are defined - for example, the "security" directory has child directories of "amps", "roles", "users", and others based on the resources that comprise the "security" set of endpoints. 

The logic for when to look for files is encapsulated in Command objects. A deployment is performed by one or more Command objects. Thus, the general pattern for using the deployer library is:

1. Create an instance of SimpleAppDeployer, which implements the AppDeployer interface
2. Set a list of commands on the SimpleAppDeployer instance
3. Call the "deploy" method to invoke each of the commands in a specific order

Here's a brief example of what that looks like - note that we'll reuse our ManageClient from above, and we'll deploy an 
application that needs to create a REST API server named "my-app" on port 8123 and create some users too - the config for both of those will be read from
files in the ml-config directory structure:

    ManageClient client = new ManageClient(); // defaults to localhost/8002/admin/admin
    AdminManager manager = new AdminManager(); // used for restarting ML; defaults to localhost/8001/admin/admin
    AppDeployer deployer = new SimpleAppDeployer(client, manager, 
        new CreateRestApiServersCommand(), new CreateUsersCommand());
    
    // AppConfig contains all configuration about the application being deployed
    AppConfig config = new AppConfig(); 
    config.setName("my-app");
    config.setRestPort(8123);
    
    // Calls each command, passing the AppConfig and ManageClient to each one
    deployer.deploy(config); 
    
    // do some other stuff...
    
    // Calls each command, giving each a chance to undo what it did before
    deployer.undeploy(config); 

## When will there be a 2.0 final of ml-app-deployer?

This will be created in conjunction with the release of MarkLogic 8.0-4, which includes support for triggers and alerts, along with a number of fixes. 
