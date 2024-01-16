This set of projects demonstrates:

- How a "provider" project can publish a zip of MarkLogic files (modules, data, schemas, or system plugins).
- How an ml-gradle project can depend on this zip so that the files are automatically included in an application.

## Publishing the provider 

To try this out, begin by publishing the zip from the provider project:

    cd provider-project
    ../../gradlew publishToMavenLocal

If you'd like to inspect the zip, you'll find it at `~/.m2/repository/com/marklogic/example-dependency`.

## Deploying and verifying the ml-gradle project

Next, deploy the app in the ml-gradle-client-project, replacing "changeme" below with the password for your admin user 
(or using a different admin-like user):

    cd ../ml-gradle-client-project
    ../../gradlew -i mlDeploy -PmlUsername=admin -PmlPassword=change

You'll see logging like this that lets you know that the modules and data from the example-dependency zip 
will be included when the application is deployed:

```
Found mlBundle configuration, will extract all of its dependencies to build/mlBundle
    [unzip] Expanding: /Users/rrudin/.m2/repository/com/marklogic/example-dependency/1.0.0/example-dependency-1.0.0.jar into /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle
Finished extracting mlBundle dependencies
Module paths including mlBundle paths: [/Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-modules, /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/src/main/ml-modules]
Data paths including mlBundle paths: [/Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-data, /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/src/main/ml-data]
Schema paths including mlBundle paths: [/Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-schemas, /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/src/main/ml-schemas]
:mlPrepareBundles (Thread[Task worker for ':',5,main]) completed. Took 0.059 secs.
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

And finally, some logging like this that indicates that schemas were loaded:

```
Executing command [com.marklogic.appdeployer.command.schemas.LoadSchemasCommand] with sort order [350]
Loading schemas into database ml-gradle-client-schemas from: /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-schemas
TDE templates loaded into ml-gradle-client-schemas will be validated against content database ml-gradle-client-content
Initializing ExecutorService 
TDE template passed validation: /Users/rrudin/dev/workspace/ml-gradle/examples/dependency-project/ml-gradle-client-project/build/mlBundle/example-dependency/ml-schemas/tde/template1.json
Writing 1 files
Writing: /tde/template1.json
Shutting down ExecutorService
Writing 1 documents to MarkLogic; port: 8000; database: ml-gradle-client-schemas
```

You can then use qconsole to verify that the following documents were inserted:

- In ml-gradle-client-modules: `/example.sjs` (in addition to the modules included by this project: `/my-lib.xqy` and `/Default/ml-gradle-client/rest-api/properties.xml`).
- In ml-gradle-client-schemas: `/tde/template1.json` (in addition to the schema file included in this project: `/tde/my-template.json`).
- In ml-gradle-client-content: `/example/data1.json` and `/example/data2.json` (in addition to the data files including 
in this project: `/testdata/test1.json`, `/testdata/test2.json`, and `/testdata/test3.json`).

See [Loading data](https://github.com/marklogic/ml-app-deployer/wiki/Loading-data) for more
information on configuring how data is loaded during a deployment.

## Provider project with system plugin

The `./provider-with-plugin-project` directory is included as a reference for including a MarkLogic system plugin 
in a bundle. Building this plugin requires a C++ compiler, so it is not included in `./provider-project`. 
