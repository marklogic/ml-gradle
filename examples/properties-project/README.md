This project shows a basic setup of the [Gradle properties plugin](https://github.com/stevesaliman/gradle-properties-plugin).
The plugin allows you to define a properties file for each environment that you need to deploy to. These properties 
override whatever is in gradle.properties. 

The `environmentName` property is used to specify a properties file. If that property is not specified, then 
`gradle-local.properties` is used by default. That file is almost always ignored by version control, which is the case
in this sample project - the file is listed in .gitignore. 

The sample property files in this project only override `mlHost`, but of course you can get as creative as you want. A
more extreme example would be to override the `mlConfigPaths` property to specify a different configuration directory for
each environment.

