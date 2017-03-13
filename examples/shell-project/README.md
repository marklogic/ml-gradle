The Gradle build file in this project shows the things you need in order to run groovysh with 
some MarkLogic support built in:

1. The latest ml-gradle plugin (at the time of this writing, most likely 2.6.0+)
1. The java plugin, so that mlShell can have a classpath set on it
1. The jcenter repository, which hosts ml-groovysh
1. The latest ml-groovysh dependency
1. And then mlShell's classpath should be set to the Java runtime classpath

You can accomplish some of the above configuration via a custom configuration in Gradle, but the 
above approach should be the simplest one.

Once the above is setup, you can run the shell (after running mlDeploy to deploy the test application) 
with the following command:

    gradle --no-daemon mlShell
    
To run the shell and watch for new/modified modules:

    gradle --no-daemon mlShell -PmlShellWatchModules=true
    

