# Getting Started

Here's how to get started with making contributions to this project:

1. Install a JDK (Java Development Kit) version 9 or higher. (If you're working with ml-gradle 2.x or 3.x, you only need Java 8.)
1. Clone this repository.
1. Assuming you want to use Eclipse for development, run "gradlew eclipse" (or "gradle eclipse" if you have Gradle installed locally). This will generate Eclipse project files.
1. Import your repository folder as a new project in Eclipse. Everything should compile successfully and you should be good to go.

To make contributions, check out the issues in the project and follow the below, subject-to-change process:

1. For a given issue, create a local branch with the name of the issue (I'll use issue #9 as an example): git checkout issue-9
1. Track the local branch to a remote one: git push -u origin issue-9
1. Do your work on that local feature branch, ideally making small commits. Push to remote as often as you'd like. 
1. When you're ready, submit a pull request from your feature branch for review. 

Currently, I don't have any tests for ml-gradle, as most of the interesting functionality is in ml-app-deployer, where
there are many automated tests. I also haven't found an easy way to test tasks or the MarkLogicPlugin class. So for the
time being, don't worry about automated tests. 

## Formatting

See the [.editorconfig](.editorconfig) file. If you don't have an [editorconfig plugin](https://editorconfig.org/#download), 
either install that or follow the guidelines set in the file. 

## How to test ml-gradle changes

This part is a little tricky - in order to really test ml-gradle, you need to build it and publish it to your local 
Maven repository (~/.m2) and then reference it in a separate Gradle file. Fortunately, the ./examples directory in this
project has many projects that you can use for testing out your change to ml-gradle. Here's how to go about doing so:

1. Make your changes to the ml-gradle source code
1. Run "gradle -Pversion=issue-number publishToMavenLocal" (the version can be anything you want)

Then pick a project under ./examples, and replace the "plugins" block with the following code:

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

The above unfortunately has to be done because while the "plugins" DSL in Gradle is very concise, 
it can't be used for finding plugins in your local Maven repository. So we have to
replace it with the much more verbose syntax. Of course, after you're done testing with this, change the Gradle file
back to just use the plugins DSL.

After making the above changes, when you run any Gradle task, Gradle will use the ml-gradle plugin that you published
to your local Maven repository. 

You can then repeat this process as often as you want - i.e. make more changes in the ml-gradle source, publish a new
copy to your local Maven repo, and test it in the project you chose. 

For a real example, see the "local-testing-project" project in the examples directory.
