# ml-app-deployer

AppDeployer is a library for automating typical tasks involved in installing and configuring a MarkLogic application.
It was extracted from the https://github.com/rjrudin/ml-gradle Gradle plugin for the primary purpose of making it easier
to create an equivalent plugin for Maven. Having it as a separate, plain Java library also makes it easier to test, extend, and
override how the library works.

AppDeployer is currently only taking advantage of REST API endpoints in MarkLogic 7; it will soon be enhanced to leverage all
the new management endpoints in MarkLogic 8. 
