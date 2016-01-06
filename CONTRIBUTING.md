Here's how to get started with making contributions to this project:

1. Install a JDK (Java Development Kit) version 7 or higher.
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

I also don't have an .editorconfig file yet. In the absence of one, just follow these simple guidelines:

1. Use spaces for tabs
1. Use a tab size of 4 spaces

## How to test ml-gradle changes

This part is a little tricky - in order to really test ml-gradle, you need to build it and publish it to your local 
Maven repository (~/.m2) and then reference it in a separate Gradle file. Fortunately, the ./examples directory in this
project has many projects that you can use for testing out your change to ml-gradle. Here's how to go about doing so:

1. Make your changes to the ml-gradle source code
1. Run "gradle -Pversion=issue-number publishToMavenLocal" (the version can be anything you want)
1. Pick a project under ./examples, and add "buildscript {repositories { mavenLocal() } }" to the build.gradle file
1. Then change the version of ml-gradle in that build.gradle file be whatever version you selected when publishing
1. When running Gradle on the project, you should now get the version you published to your local Maven repo
 