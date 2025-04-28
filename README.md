![GitHub release](https://img.shields.io/github/release/marklogic/ml-gradle.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/marklogic/ml-gradle.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Known Vulnerabilities](https://snyk.io/test/github/marklogic/ml-gradle/badge.svg)](https://snyk.io/test/github/marklogic/ml-gradle)

Automate everything involving MarkLogic with Gradle
=========

ml-gradle is a [Gradle](https://gradle.org/) [plugin](https://docs.gradle.org/current/userguide/plugins.html) that can automate everything
you do with [MarkLogic](https://www.marklogic.com/). Deploy an application, add a host, backup a database, stub out a new project, load modules as you modify them, 
run an [MLCP](https://developer.marklogic.com/products/mlcp) or [CoRB](https://developer.marklogic.com/code/corb) 
or [Data Movement](http://docs.marklogic.com/guide/java/data-movement) job - if it involves MarkLogic, 
ml-gradle either automates it already or can be extended to do so. 

You can use ml-gradle right away with the simple tutorial below, or learn more:

- The [ml-gradle Wiki](https://github.com/marklogic/ml-gradle/wiki) guides you through all of the ml-gradle documentation
- Read the [Getting Started guide](https://github.com/marklogic/ml-gradle/wiki/Getting-started) for more details on setting up a new project
- Browse the [example projects](https://github.com/marklogic/ml-gradle/tree/master/examples) for working examples of different ml-gradle features

**Don't want to use Gradle?** Then consider using [the ml-app-deployer Java library](https://github.com/marklogic/ml-app-deployer) 
on which ml-gradle depends. ml-app-deployer provides most of the functionality within ml-gradle without having any dependency on Gradle or 
Groovy - ml-gradle is then a fairly thin wrapper around ml-app-deployer to expose its functionality within a Gradle environment.

Compatibility guidelines
=========

The minimum required version of Gradle for use with ml-gradle is largely based on fixes to Gradle itself that are
related to transitive dependencies in ml-gradle. Please adhere to the guideline below for determining the minimum
version of Gradle to use based on the version of ml-gradle you are using.

| ml-gradle version | Minimum Gradle version | 
|-------------------|------------------------|
| 5.0.0 or higher | Gradle 7.6.4 or higher, with Gradle 8.0 or higher recommended. |
| 4.6.0 to 4.8.0 | Gradle 7.6.0 or higher. |
| Before 4.6.0 | Gradle 7.0 or higher is recommended, though earlier versions of Gradle 6.x may work as well. |

If you wish to use Gradle 7 or higher, you must use ml-gradle 4.3.0 or higher. 

You will need Java 8 or higher to run Gradle and ml-gradle. Both ml-gradle and the underlying 
[MarkLogic Java Client](https://github.com/marklogic/java-client-api) dependency are tested on Java 8, 11, 17, and 21.

Start using ml-gradle
=========

ml-gradle depends on at least Java 8 or higher and [MarkLogic 9 or higher](https://developer.marklogic.com/products), 
so if you have those installed, you're just a few minutes away from using ml-gradle to start a new project and deploy an 
application from it.

First, [install Gradle](https://gradle.org/install/). 

Then, in an empty directory, create a file named "build.gradle" with your favorite text editor and enter the following:

    plugins { id "com.marklogic.ml-gradle" version "5.0.0" }
    
Then run:

    gradle mlNewProject

(If you are trying ml-gradle 4.5.0, you will need to do `gradle -PmlUsername= -PmlPassword= mlNewProject` 
due to a bug that has been fixed in ml-gradle 4.5.1).

This starts a project wizard to stub out files for your new application. You can accept all the defaults, but be sure to
enter a valid port number for the "REST API port" question. ml-gradle will then print the following logging:

    Updating build.gradle so that the Gradle properties plugin can be applied
    Writing: build.gradle
    Writing: gradle.properties
    Writing: gradle-dev.properties
    Writing: gradle-local.properties
    Writing: gradle-qa.properties
    Writing: gradle-prod.properties
    Making directory: src/main/ml-config
    Making directory: src/main/ml-modules
    Writing project scaffolding files

You now have an ml-gradle project stubbed out with support for deploying to multiple environments via the 
[Gradle properties plugin](https://github.com/stevesaliman/gradle-properties-plugin). 

Now deploy it!

    gradle mlDeploy
    
And you should see more ml-gradle logging like this:

    :mlDeleteModuleTimestampsFile
    :mlPrepareBundles
    :mlDeployApp
    :mlPostDeploy UP-TO-DATE
    :mlDeploy
    BUILD SUCCESSFUL

And once that's complete, you can go to the MarkLogic Admin UI on port 8001 to see the resources that have been created 
(the names of these resources start with the application name you selected in the project wizard, which defaults to myApp):

- Under App Servers, a new REST server named myApp on the port you chose
- Under Databases, a new content datase named myApp-content and a new modules database named myApp-modules
- Under Forests, 3 new forests for myApp-content and 1 new forest for myApp-modules
- Under Security/Users, 3 new users, each prefixed with myApp
- Under Security/Roles, 5 new roles, each prefixed with myApp

Congratulations! You've used ml-gradle to stub out a new project and deploy its application to MarkLogic. You're now 
ready to start adding more resources and modules to your project. See the links above this tutorial to learn
more about using ml-gradle. 
