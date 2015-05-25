Here's how to get started with making contributions to this project:

1. Install Java 8.
1. Clone this repository.
1. Assuming you want to use Eclipse for development, run "gradlew eclipse". This will generate Eclipse project files.
1. Import your repository folder as a new project in Eclipse. Everything should compile successfully and you should be good to go.

Note that in the instructions above, "gradlew" - the Gradle wrapper - is used to process the project's build.gradle file. If
you already have Gradle installed in your environment, you can just run "gradle". I believe any version of Gradle 2.x should
work, but I always recommend the latest one.

To kick the tires on this library, try running any of the tests in src/test/java. 

To make contributions, check out the issues in the project and follow the below, subject-to-change process:

1. For a given issue, create a local branch with the name of the issue (I'll use issue #9 as an example): git checkout issue-9
1. Track the local branch to a remote one: git push -u origin issue-9
1. Do your work on that local feature branch, ideally making small commits. Push to remote as often as you'd like. 
1. When you're ready, submit a pull request from your feature branch for review. 

More guidelines to come soon:

1. Eclipse formatting preferences for Java
1. General development guidelines