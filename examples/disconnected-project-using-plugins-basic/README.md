# Disconnected Gradle project that uses the Gradle Plugins Framework

This project will download all of the required dependencies (including plugins) into the 'project-maven-repo' directory in the project.

It does this by setting the 'gradle.user.home' to a local project directory and downloading all the project dependencies into this directory. The cache within this directory is then converted into a maven-repo format directory ('project-maven-repo') that will be used when running in a 'disconnected' mode 


## Usage 

### Download Dependencies 

The command below will download all the project dependencies (including plugins) into the  'project-maven-repo' directory

```
gradle -Dgradle.user.home=project-gradle-cache downloadToProjectMavenRepo 
```

### Run in 'disconnected' mode

You just need to set the disconnected project property when running your gradle tasks. E.g. - 

```
gradle tasks -Pdisconnected 
```

or

```
gradle mlDeploy -Pdisconnected 
```

This will use the jars that you have already downloaded to 'project-maven-repo'

## Customise

**IMPORTANT**: If you want to include dependencies for a configuration (e.g. compile, runtime, mlcp etc), then you need to modify the 'downloadToProjectMavenRepo' task to include the relevant configuration. E.g. by adding 'configurations.compile.files' to the beginning of the task, all of the dependencies for the 'compile' task will be downloaded.

E.g. (assuming you are using the java plugin), the configuration below will download all the compile and runtime dependencies that you have defined 

```
task downloadToProjectMavenRepo(type: Copy) {
    configurations.compile.files
    configurations.runtime.files
     ...
```