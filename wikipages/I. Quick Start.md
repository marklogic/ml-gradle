## What you need

1. [Install Gradle](https://gradle.org/install)
    - consider at least skimming through some of the following Gradle docs to get a little familiar with it:
        - [Gradle overview](https://docs.gradle.org/3.4.1/userguide/overview.html)
        - [Using Gradle from the command line](https://docs.gradle.org/3.4.1/userguide/tutorial_gradle_command_line.html)
        - [Gradle build script basics](https://docs.gradle.org/3.4.1/userguide/tutorial_using_tasks.html)
        - [The Gradle build environment and how properties are used](https://docs.gradle.org/3.4.1/userguide/build_environment.html)
        - [How to write custom Gradle tasks](https://guides.gradle.org/writing-gradle-tasks/)

2. [Install MarkLogic](https://developer.marklogic.com/products)
    - At least MarkLogic 8.0-4 
    - This tutorial assumes you have installed MarkLogic on localhost.

## Starting a new Gradle project with ml-gradle

1. Create a new folder to contain your project
    - for this tutorial, let's use 'petstore'.
2. Move into the project
3. Initiate the build.gradle file.
    - You can use [gradle init](https://docs.gradle.org/3.4.1/userguide/build_init_plugin.html) and add the lines below accordingly;
    - Or, create the file manually and add the lines below.

**build.gradle**
```
plugins {
  id "net.saliman.properties" version "1.4.6"
  id "com.marklogic.ml-gradle" version "3.3.0" 
}
```
Above is a Groovy snippet to declare we are using the ml-gradle plugin. ([why Groovy?](https://docs.gradle.org/3.4.1/userguide/overview.html#sec:why_groovy))

4. Creating our initial project configuration file.

```
gradle mlNew
```

This process will ask you several questions to help stub out a new project. 

```
[ant:input] Application name: [myApp]
[ant:input] Host to deploy to: [localhost]
[ant:input] MarkLogic admin username: [admin]
[ant:input] MarkLogic admin password: [admin]
[ant:input] REST API port (leave blank for no REST API server):
[ant:input] Test REST API port (intended for running automated tests; leave blank for no server):
[ant:input] Do you want support for multiple environments?  ([y], n)
[ant:input] Do you want resource files for a content database and set of users/roles created? ([y], n)
```
  - Supply 'petstore' as "Application name"
  - Selecting 'y' to "multiple environments" will pre-create properties files that could be modified accordingly. Refer to the [plugin documentation](https://github.com/stevesaliman/gradle-properties-plugin) for more info about this plugin.
  - Selecting 'y' to "resource files" will pre-create various resources under the src folder (also created if not available). We'll discuss these generated files more later.

5. Deploy
```
gradle mlDeploy
```
You would see some logging like this at the end:
```
:mlDeleteModuleTimestampsFile
:mlPrepareRestApiDependencies
:mlDeployApp
:mlPostDeploy UP-TO-DATE
:mlDeploy
BUILD SUCCESSFUL
Total time: 22.518 secs
```

6. Verify
You should now have the following items as you reload your admin UI (i.e. <host>:8001)
    - Under Databases:
        - petstore-content
        - petstore-modules
        - petstore-test-content
    - Under App Servers:
        - petstore (8003) -- or whatever port was specified above
        - petstore-test (8004) -- or whatever port was specified above
    - Under Forests:
        - petstore-content-1 to 3
        - petstore-modules-1
        - petstore-content-test-1 to 3
    - Under Security:
        - roles: petstore-proj-admin, petstore-proj-internal, petstore-proj-nobody, petstore-proj-reader, petstore-proj-writer
        - users: petstore-admin, petstore-reader, petstore-writer

Congratulations! You now have your project wired and ready to go!