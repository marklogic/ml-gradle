# ml-app-deployer

AppDeployer is a library for automating typical tasks involved in installing and configuring a MarkLogic application.
It was extracted from the https://github.com/rjrudin/ml-gradle Gradle plugin for the primary purpose of making it easier
to create an equivalent plugin for Maven. Having it as a separate, plain Java library also makes it easier to test, extend, and
override how the library works.

AppDeployer is currently only taking advantage of REST API endpoints in MarkLogic 7; it will soon be enhanced to leverage all
the new management endpoints in MarkLogic 8. 

Using AppDeployer is simple - just instantiate AppConfig, set whatever properties you need to, and then pass it into an implementation of AppDeployer.java (currently, Ml7AppDeployer.java). [DeployAppTest](https://github.com/rjrudin/ml-app-deployer/blob/master/src/test/java/com/marklogic/appdeployer/ml7/DeployAppTest.java) shows an example of this, which is shown below as well:

    AppConfig appConfig = new AppConfig();
    appConfig.setName("myAppName");
    appConfig.setRestPort(8123);
    appConfig.setXdbcPort(8124);
  
    AppDeployer deployer = new Ml7AppDeployer(new Ml7ManageClient("localhost", 8002, "admin", "admin"));
    deployer.installPackages(appConfig);
    deployer.loadModules(appConfig, null);

The idea then is you define all your application configuration in AppConfig and then pass it into each high-level method in AppDeployer, along with any other parameters specific to that particular operation. You can then run this in a simple Java program, or more likely, bake it into a plugin for a tool like Ant, Gradle, or Maven. 
