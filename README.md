What is Gradle?
===============

Gradle is a standard build tool that is used for building and deploying primarily Java applications, but it can be used for any codebase. The full user guide for Gradle is available at http://www.gradle.org/docs/current/userguide/userguide.html. 

This Wiki assumes a general understanding of the following topics.

- [General Understanding of Gradle](http://www.gradle.org/docs/current/userguide/tutorial_using_tasks.html "")
- Understands the MarkLogic Package REST API
- Understands the MarkLogic Client REST API

What is ml-gradle?
=========
ml-gradle is a [Gradle plugin](http://www.gradle.org/plugins "") that supports a number of tasks pertaining to deploying an application to MarkLogic and interacting with other features of MarkLogic via a Gradle build file. In general, ml-gradle tries to get as many things done as possible by making HTTP calls to the MarkLogic REST API services, particularly the [packaging API](http://docs.marklogic.com/REST/packaging ""). When a REST API endpoint doesn't exist, ml-gradle will use XCC. ml-gradle also provides support for using tools such as MarkLogic Content Pump from a Gradle build file. 

Why use ml-gradle?
=========
ml-gradle is a good fit for you and your team if:

1. You're using MarkLogic
1. You're already using Gradle, or you're using Ant or Maven and are interested in shifting to Gradle (there are many comparisons of these tools on the Internet; I'll just say I recommend making this shift)
1. You're a Java development team that can utilize a lot of the Java-focused features of Gradle

Essentially, if you're using MarkLogic and Java, then ml-gradle is probably a good fit for you. And if you're not using Java, then ml-gradle is most likely not a good fit for you - I recommend instead using the [Roxy deployer](https://github.com/marklogic/roxy) for managing how you configure MarkLogic and deploy applications to it. You can still use ml-gradle, but odds are introducing Gradle for the sole purpose of using ml-gradle is going to add too much complexity to your project to be worth it. You should have already decided that you want to use Gradle as your project's build tool (or as Gradle calls it, your "enterprise automation tool"), and then ml-gradle will be a good fit for you. 

Here are some of the main features of ml-gradle:

1. Can install database/server packages and load modules via the MarkLogic REST API
1. Can watch for new/modified modules and automatically load them for you, thus simplifying the code/build/test cycle
1. Can treat packages of XQuery code as true third-party dependencies, resolving them just like you would a dependency on a jar, as well as automatically loading such code into your modules database
1. Can easily run MarkLogic Content Pump and Corb without having to copy jars around and worry about a classpath
1. Can perform most tasks related to CPF
1. Can manage security resources such as users, roles, and amps

ml-gradle quick start
=========
To use ml-gradle right away, you'll need Gradle installed first. And of course you'll need Marklogic installed somewhere - it doesn't have to be the same computer as the one you're running Gradle on. Then create a directory for your project and add a build.gradle file and a gradle.properties file. Here's the simplest build.gradle file possible:

    buildscript {
      repositories {
        mavenCentral()
        maven {url "http://developer.marklogic.com/maven2/"}
        maven {url "http://rjrudin.github.io/marklogic-java/releases"}
      }
      dependencies {
        classpath "com.marklogic:ml-gradle:0.9.9"
      }
    }
    
    apply plugin: 'ml-gradle'

And here's the simplest gradle.properties file possible (you can of course customize these properties as needed, particularly the ports - make sure that they're open on the host you're deploying to):

    mlHost=localhost
    mlUsername=admin
    mlPassword=admin
    mlRestPort=8200
    mlXdbcPort=8201
    mlAppName=quick-start

Then just run "gradle mlDeploy" in the directory containing these two files. You'll end up with a new REST API server on port 8200, an XDBC server on 8201, and two databases - a content database and a modules database, with one forest for each. 


Digging deeper into ml-gradle
=========
The best way to dig deeper into what ml-gradle provies is to clone [the marklogic-java repository]((https://github.com/rjrudin/marklogic-java) and 
[examine the build.gradle file](https://github.com/rjrudin/marklogic-java/blob/master/sample-project/build.gradle) in the 
sample-project directory. This is intended to show all the different features of ml-gradle, with comments explaining 
each one. Most tasks have Wiki pages as well to provide further information, and of course there's always 
the source code, with [the MarkLogicPlugin](https://github.com/rjrudin/marklogic-java/blob/master/ml-gradle/src/main/groovy/com/marklogic/gradle/MarkLogicPlugin.groovy) being a 
good place to start, as it lists out all the registered tasks. 

If you have a project already, then a good way to start is by copying the aforementioned build.gradle file into your project. You can remove all the optional stuff to start with a bare minimum Gradle file, and then start adding things back in as you realize a need for them.

Once you have a Gradle file with ml-gradle applied, you can see a list of all tasks added by ml-gradle by running:

    gradle tasks

You can also see a list of the tasks with all their dependencies - this is helpful for knowing how tasks relate to one another:

    gradle tasks --all 

To see ml-gradle in action, you can deploy the sample-project application by doing the following (assuming you've cloned the repository already):

1. cd sample-project
1. gradle mlDeploy

Then watch the logging scroll by as a number of ml-gradle tasks are executed, resulting in new databases and app servers prefixed with "sample-project" as the name, all of which you can of course inspect via the MarkLogic Admin app. 
