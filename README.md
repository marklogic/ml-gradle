# What is ml-app-deployer?

ml-app-deployer is a Java library that provides two capabilities:

# A client library for the new [Management REST API](http://docs.marklogic.com/REST/management) in MarkLogic 8. 
# A command-driven approach for deploying and undeploying an application to MarkLogic that depends on the management client library.

If you're just looking for a Java library for interacting with the Management REST API, you can certainly use ml-app-deployer. The deployer/command library is mostly a thin layer around the management client library and can be safely ignored if you don't need it. 

# What does it depend on? 

ml-app-deployer depends on Spring's [RestTemplate](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html) for interacting with the Management REST API. It also depends on [ml-javaclient-util](https://github.com/rjrudin/ml-javaclient-util) for loading modules, which is done via the MarkLogic Client REST API. 

# How do I start using it?

