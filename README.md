What is Gradle?
===============

Gradle is a standard build tool that is used for building and deploying primarily Java applications, but it can be used for any codebase. The full user guide for Gradle is available at http://www.gradle.org/docs/current/userguide/userguide.html. 

What is ml-gradle?
=========
ml-gradle is a [Gradle plugin](http://www.gradle.org/plugins "") that supports a number of tasks pertaining to deploying an application to MarkLogic and interacting with other features of MarkLogic via a Gradle build file. The bulk of the functionality provided by ml-gradle is actually in [ml-app-deployer](https://github.com/rjrudin/ml-app-deployer) - ml-gradle is just intended to be a thin wrapper around this library, exposing its functionality via Gradle tasks and properties.

Why use ml-gradle?
=========
ml-gradle is a good fit for you and your team if:

1. You're using MarkLogic 8
2. You're using Gradle, or you think Gradle would be a useful build tool to add to your development/deployment process. If you're currently using Ant or Maven and are wondering about Gradle, there are many comparisons of these tools on the Internet; I'll just say I recommend making this shift. If you are using Ant, it's easy to invoke Ant tasks from Gradle. And if you can't move away from Maven, you can try [this Maven plugin for invoking Gradle](https://github.com/if6was9/gradle-maven-plugin).
3. (Optional) You're interested in managing other people's MarkLogic modules as [true third-party dependencies](https://github.com/rjrudin/ml-gradle/wiki/Common-tasks#preparing-rest-api-dependencies) instead of having to clone their code into your own project. This leverages Gradle's normal dependency management. 

If you don't want to use Gradle, then ml-gradle may not be a good fit for you - consider using the [Roxy deployer](https://github.com/marklogic/roxy) instead. One thing to remember though is that many of MarkLogic's libraries are in Java - mlcp, corb, recordloader, xqsync, the Java Client API - and thus if you're not using a Java-friendly build tool, you'll have to manage all these dependencies yourself as well as cook up ways to invoke each of them with the correct classpath. 

Here are some of the main features of ml-gradle:

1. Utilizes the new [Management REST API](http://docs.marklogic.com/REST/management) in MarkLogic 8 to configure all aspects of an application.
1. Can watch for new/modified modules and automatically load them for you, thus simplifying the code/build/test cycle
1. Can treat packages of MarkLogic code as [true third-party dependencies](https://github.com/rjrudin/ml-gradle/wiki/Common-tasks#preparing-rest-api-dependencies), resolving them just like you would a dependency on a jar, as well as automatically loading such code into your modules database
1. Can easily run MarkLogic Content Pump and Corb without having to copy jars around and worry about a classpath

What are all of the tasks I can perform with ml-gradle?
=========
See [the Wiki page on all tasks](https://github.com/rjrudin/ml-gradle/wiki/All-tasks).


How can I start using ml-gradle?
=========
To use ml-gradle right away, you'll need Gradle installed first. And of course you'll need Marklogic installed somewhere - it doesn't have to be the same computer as the one you're running Gradle on. Then create a directory for your project and add a build.gradle file and a gradle.properties file. Here's the simplest build.gradle file possible:

    buildscript {
      repositories {
        mavenCentral()
        maven {url "http://developer.marklogic.com/maven2/"}
        maven {url "http://rjrudin.github.io/marklogic-java/releases"}
      }
      dependencies {
        classpath "com.marklogic:ml-gradle:2.0rc1"
      }
    }
    
    apply plugin: 'ml-gradle'

And here's the simplest gradle.properties file possible (you can of course customize these properties as needed, particularly the port - make sure that it's open on the host you're deploying to):

    mlHost=localhost
    mlUsername=admin
    mlPassword=admin
    mlAppName=quick-start
    mlRestPort=8200

Then just run "gradle mlDeploy" in the directory containing these two files:

    gradle mlDeploy
    
You'll end up with a new REST API server on port 8200 with a modules database and a content database with 3 forests by default. 

To see exactly what mlDeploy is doing, just run Gradle with the "-i" or "--info" option (it's normally useful to do this in any case with Gradle):

    gradle -i mlDeploy


Exploring the sample project
-----

To start customizing your application, your best bet is to examine the [sample-project application](https://github.com/rjrudin/ml-gradle/blob/master/examples/sample-project) in this repository. There are three primary things to examine:

1. The [build.gradle file](https://github.com/rjrudin/ml-gradle/blob/master/examples/sample-project/build.gradle) provides examples of configuring and extending ml-gradle. 
1. The [ml-config directory](https://github.com/rjrudin/ml-gradle/tree/master/examples/sample-project/src/main/ml-config) provides examples of all of the MarkLogic management resources currently supported by ml-gradle.
1. The [ml-modules directory](https://github.com/rjrudin/ml-gradle/tree/master/examples/sample-project/src/main/ml-modules) provides examples of the different kinds of modules that can be loaded (application modules are loaded via the MarkLogic Client REST API, not the Management REST API). 

To quickly generate a useful set of configuration files, just run:

    gradle mlScaffold

This will generate a directory structure containing several configuration files - one for a content database, a REST API server, an application role, an application user, and more. You can change these and add more configuration files based on the examples in the sample project mentioned above.


Digging deeper into ml-gradle
=========
There are two things to learn with ml-gradle - what the ml-app-deployer library lets you do, and what ml-gradle adds on top of it. The main thing to know about ml-app-deployer is where it expects Management API configuration files to be placed so that they're automatically processed by ml-app-deployer and thus by ml-gradle. 

To learn more about what ml-gradle provides on top of ml-app-deployer, you should start by 
[examining the build.gradle file](https://github.com/rjrudin/ml-gradle/blob/master/examples/sample-project/build.gradle) in the 
sample-project directory of this repository. This is intended to show all the different features of ml-gradle. To understand the ml-gradle code itself, you should start with [the MarkLogicPlugin](https://github.com/rjrudin/ml-gradle/blob/master/src/main/groovy/com/marklogic/gradle/MarkLogicPlugin.groovy), as it lists out all the registered tasks. 

If you have a project already, then a good way to start is by copying the aforementioned build.gradle file into your project. You can remove all the optional stuff to start with a bare minimum Gradle file, and then start adding things back in as you realize a need for them.

Once you have a Gradle file with ml-gradle applied, you can see a list of all tasks added by ml-gradle by running:

    gradle tasks

You can also see a list of the tasks with all their dependencies - this is helpful for knowing how tasks relate to one another:

    gradle tasks --all 

To see ml-gradle in action, you can deploy the sample-project application by doing the following (assuming you've cloned this repository already):

1. cd sample-project
1. gradle mlDeploy

Finally, check out all the Wiki pages on the right for more information about ml-gradle. 

## When will there be a 2.0 final of ml-gradle?

This will be created in conjunction with the release of MarkLogic 8.0-4, which includes support for triggers and alerts, along with a number of fixes. 
