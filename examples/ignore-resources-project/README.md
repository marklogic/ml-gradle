As of ml-gradle 2.6.0, you can set the mlResourceFilenamesToIgnore property to specify the names of all resource
files to ignore. The build.gradle file shows an alternative to using this property, where a command is individually
configured. 

This feature is useful for when you need to ignore some resources in different environments - for example, a dev 
environment may require an additional database that's not needed in a production environment.
