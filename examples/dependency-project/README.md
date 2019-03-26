This set of projects demonstrates:

- How a "provider" project can publish a zip of MarkLogic modules and data
- How an ml-gradle or DHF project can depend on this zip so that the modules and data are automatically included 
in an application

## Publishing the provider 

To try this out, you'll first need Gradle installed locally. 

Then, begin by publishing the zip from the provider project:

    cd provider-project
    gradle publishToMavenLocal

If you'd like to inspect the zip, you'll find it at ~/.m2/repository/com/marklogic/example-dependency.

## Deploying and verifying the ml-gradle project

Next, deploy the app in the ml-gradle-client-project:

    cd ../ml-gradle-client-project
    gradle -i mlDeploy

You'll see logging like this that lets you know that the modules and data from the example-dependency zip 
will be included when the application is deployed:

```
Found mlBundle configuration, will extract all of its dependencies to build/mlBundle
Extracting file: /Users/rrudin/.m2/repository/com/marklogic/example-dependency/1.0.0/example-dependency-1.0.0.jar
    [unzip] Expanding: /Users/rrudin/.m2/repository/com/marklogic/example-dependency/1.0.0/example-dependency-1.0.0.jar into /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle
Finished extracting mlBundle dependencies
Module paths: [/Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-modules, /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/src/main/ml-modules]
Data paths: [/Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-data, /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/src/main/ml-data]
:mlPrepareBundles (Thread[Task worker for ':' Thread 4,5,main]) completed. Took 0.165 secs.
```

And you'll see logging like this that indicates that the example-dependency modules were loaded:

```
Executing command [com.marklogic.appdeployer.command.modules.LoadModulesCommand] with sort order [400]
Initializing new instance of ModulesLoader
Loading asset modules from dir: /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-modules
Writing 1 files
Writing: /example.sjs
Writing 1 documents to MarkLogic; port: 8000; database: ml-gradle-client-modules
```

And you'll see logging like this that indicates that the example-dependency data was loaded:

```
Executing command [com.marklogic.appdeployer.command.data.LoadDataCommand] with sort order [1300]
Initializing ExecutorService 
Writing 2 files
Writing: /example/data1.json
Writing: /example/data2.json
Shutting down ExecutorService
Writing 2 documents to MarkLogic; port: 8030
```

You can then use qconsole to verify that the following documents were inserted:

- In ml-gradle-client-modules: /example.sjs
- In ml-gradle-client-content: /example/data1.json and /example/data2.json

See [Loading data](https://github.com/marklogic-community/ml-app-deployer/wiki/Loading-data) for more
information on configuring how data is loaded during a deployment.


## Deploying and verifying the DHF project

The DHF project is deployed the same way as the ml-gradle project, though you'll first need to run hubInit to 
initialize the project (this is to avoid adding a bunch of DHF files to version control that aren't needed for
the purposes of this example):

    cd ../dhf-client-project
    gradle -i mlDeploy
    
You'll see the same logging as shown above for the ml-gradle project. And likewise, you can use qconsole
to verify that the following documents were inserted:

- In dhf-client-MODULES: /example.sjs
- In dhf-client-FINAL: /example/data1.json, /example/data2.json

Note that in a DHF project, the final database is equivalent to the default content database in an ml-gradle
project. Thus, data is loaded by default to the final database. This can be overridden via the 
mlDataDatabaseName property. 

