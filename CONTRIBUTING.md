# Getting Started

Here's how to get started with making contributions to this project:

1. Install a JDK (Java Development Kit) version 8 or higher.
1. Clone this repository.
1. Import your repository folder as a new project into your IDE. Everything should compile successfully and you should be good to go.

## Formatting

See the [.editorconfig](.editorconfig) file. If you don't have an [editorconfig plugin](https://editorconfig.org/#download), 
either install that or follow the guidelines set in the file. 

## How to test ml-gradle changes

In order to test ml-gradle as a Gradle plugin, you need to build it and publish it to your local 
Maven repository (~/.m2) and then reference it in a separate Gradle file. The `./examples` directory in this
project has many projects that you can use for testing out your change to ml-gradle, with the 
`./examples/local-testing-project` being intended for testing local changes to ml-gradle. 

To build and publish ml-gradle locally, perform the following steps:

1. Make your changes to the ml-gradle source code.
1. Run `gradle publishToMavenLocal`.

Then pick a project under `./examples`, and replace the `plugins` block with the following code:

    buildscript {
      repositories {
        mavenLocal() 
        mavenCentral()
      } 
      dependencies {
        classpath "com.marklogic:ml-gradle:(the version number you chose)"
      }
    }
    apply plugin: "com.marklogic.ml-gradle"

The above has to be done because while the "plugins" DSL in Gradle is very concise, it can't be used for finding 
plugins in your local Maven repository.

After making the above changes, when you run any Gradle task, Gradle will use the ml-gradle plugin that you published
to your local Maven repository. 

You can then repeat this process as often as you want - i.e. make more changes in the ml-gradle source, publish a new
copy to your local Maven repo, and test it in the project you chose. 

## Automated Testing

The automated tests for each of the three subprojects require access to a MarkLogic server, and the ml-javaclient-util &
ml-gradle subprojects also require a test application deployed to it. To deploy that
application in a new Docker container, run the following commands:

```
docker compose up -d --build
cd test-app
../gradlew -i mlDeploy
cd ..
```

Once the test server and application are set up, you are ready to run the tests for each subproject. Those tests can be
run in multiple ways, but the easiest is to simply use Gradle on the command-line as shown below.

```
./gradlew ml-javaclient-util:test
./gradlew ml-app-deployer:test
./gradlew ml-gradle:test
```

You can also run all tests via:

    ./gradlew test

