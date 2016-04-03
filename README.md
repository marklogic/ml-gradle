[![Build Status](https://travis-ci.org/rjrudin/ml-javaclient-util.svg?branch=master)](https://travis-ci.org/rjrudin/ml-javaclient-util)

ml-javaclient-util is a library of Java classes that provide some useful functionality on top of 
the [MarkLogic Java Client API](http://docs.marklogic.com/guide/java). Those features include:

- Support for loading any kind of module using the REST API
- Support for automatically loading a new/modified module using the REST API
- Basic integration with Spring via a Spring FactoryBean

This is a lower-level library that is primarily used via [ml-app-deployer](https://github.com/rjrudin/ml-app-deployer) and [ml-gradle](https://github.com/rjrudin/ml-gradle) and [ml-junit](https://github.com/rjrudin/ml-junit). But you can use it by itself too.

Here's a sample of loading modules - though it's best to look at the aforementioned projects to see all the ways this can be done:

    DatabaseClient client = DatabaseClientFactory.newClient(...); // Use the ML Java Client API
    RestApiAssetLoader assetLoader = new RestApiAssetLoader(client); // Can use XCC or the REST API to load asset modules
    DefaultModulesLoader modulesLoader = new DefaultModulesLoader(assetLoader);
    File modulesDir = new File("src/main/ml-modules");
    ModulesFinder modulesFinder = new DefaultModulesFinder(); // Allows for adjusting where modules are stored on a filesystem
    modulesLoader.loadModules(modulesDir, modulesFinder, client);
    