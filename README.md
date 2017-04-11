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
2. You're using Gradle, or you're considering moving away from Ant/Maven/etc to Gradle to manage all of your build processes
3. You'd like to use a build tool that can manage the dependencies and execution of MarkLogic tools such as the Java Client API, mlcp, corb, recordloader, xqsync
4. You'd like to have a single all-purpose tool that allows you to define tasks to do anything you need to on your MarkLogic project, including tasks that have nothing to do with MarkLogic. 

If you're currently using Ant or Maven and are wondering about Gradle, there are many comparisons of these tools on the Internet; I recommend making this shift. If you are using Ant, it's easy to invoke Ant tasks from Gradle. And if you can't move away from Maven, you can try [this Maven plugin for invoking Gradle](https://github.com/if6was9/gradle-maven-plugin).

What are the main features of ml-gradle?
=========
1. Utilizes the new [Management REST API](http://docs.marklogic.com/REST/management) in MarkLogic 8 to configure and deploy all aspects of an application.
1. Can [watch for new/modified modules](https://github.com/rjrudin/ml-gradle/wiki/Watching-for-module-changes) and automatically load them for you, thus speeding up the code/build/test cycle
1. Can run Content Pump, Corb, and other Java-based MarkLogic tools without having to copy jars around and worry about a classpath
1. Can treat packages of MarkLogic code as [true third-party dependencies](https://github.com/rjrudin/ml-gradle/wiki/Preparing-REST-API-dependencies), resolving them just like you would a dependency on a jar, as well as automatically loading such code into your modules database
1. Can take advantage of [all the features of Gradle](https://docs.gradle.org/current/userguide/overview.html)

Can I use ml-gradle with Roxy?
=========
You bet! Starting with version 2.2.0, you can use "gradle mlWatch" to automatically load new/modified modules into the modules database of your Roxy application, and ml-gradle will substitute tokens based on Roxy properties as well. See [Loading modules in a Roxy project](https://github.com/rjrudin/ml-gradle/wiki/Loading-modules-on-a-Roxy-project) for more information.

How can I start using ml-gradle?
=========
First, please note the [Wiki and FAQ](https://github.com/rjrudin/ml-gradle/wiki) which have answers to many of the questions you'll have or run into soon.

Then, check out the [new Getting Started Wiki page](https://github.com/rjrudin/ml-gradle/wiki/Getting-started). 

Exploring the sample projects
-----

To start customizing your application, your best bet is to examine the [sample-project application](https://github.com/rjrudin/ml-gradle/blob/master/examples/sample-project) in this repository (along with all the other sample projects in that directory). There are three primary things to examine:

1. The [build.gradle file](https://github.com/rjrudin/ml-gradle/blob/master/examples/sample-project/build.gradle) provides examples of configuring and extending ml-gradle. 
1. The [ml-config directory](https://github.com/rjrudin/ml-gradle/tree/master/examples/sample-project/src/main/ml-config) provides examples of many of the MarkLogic management resources currently supported by ml-gradle.
1. The [ml-modules directory](https://github.com/rjrudin/ml-gradle/tree/master/examples/sample-project/src/main/ml-modules) provides examples of the different kinds of modules that can be loaded (application modules are loaded via the MarkLogic Client REST API, not the Management REST API). 

To try out the sample project, just do the following:

1. Clone this repository
1. cd examples/sample-project
1. gradle mlDeploy

For a new project - to quickly generate a useful set of configuration files, just run:

    gradle mlScaffold

This will generate a directory structure containing several configuration files - one for a content database, a REST API server, an application role, an application user, and more. You can change these and add more configuration files based on the examples in the sample project mentioned above.
