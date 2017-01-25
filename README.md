[![Build Status](https://travis-ci.org/rjrudin/ml-javaclient-util.svg?branch=master)](https://travis-ci.org/rjrudin/ml-javaclient-util)

ml-javaclient-util is a library of Java classes that provide some useful functionality on top of 
the [MarkLogic Java Client API](http://docs.marklogic.com/guide/java). Those features include:

- Support for [loading modules via the REST API](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/modulesloader)
- Basic integration with [Spring via a Spring FactoryBean](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/spring)
- Library for [parallelizing batched writes](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/batch)
- Spring-style [template/callback library for XCC](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/xcc/template)
- Support for generating MarkLogic 9 [Entity Services modules](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/es)
- Support for [importing/exporting qconsole workspaces] (https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/qconsole)
 
This is a lower-level library that is primarily used via [ml-app-deployer](https://github.com/rjrudin/ml-app-deployer) 
and [ml-gradle](https://github.com/rjrudin/ml-gradle) and [ml-junit](https://github.com/rjrudin/ml-junit). But you can use it by itself too.

### Loading Modules

Here's a sample of loading modules - though it's best to look at the aforementioned projects to see all the ways this can be done:

    DatabaseClient client = DatabaseClientFactory.newClient(...); // Use the ML Java Client API
    XccAssetLoader assetLoader = new XccAssetLoader(client); // Can use XCC or the REST API to load asset modules
    DefaultModulesLoader modulesLoader = new DefaultModulesLoader(assetLoader);
    File modulesDir = new File("src/main/ml-modules");
    ModulesFinder modulesFinder = new DefaultModulesFinder(); // Allows for adjusting where modules are stored on a filesystem
    modulesLoader.loadModules(modulesDir, modulesFinder, client);

### Parallelized batch writes

The [BatchWriter](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/batch) library
was created primarily for applications using [marklogic-spring-batch](https://github.com/sastafford/marklogic-spring-batch). But 
it can be used in any environment. It provides the following features:

1. Uses Spring's [TaskExecutor library](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/scheduling.html) for parallelizing writes
1. Supports writes via the REST API or XCC

Once MarkLogic 9 is available, an implementation will be used that depends on the new Data Movement SDK, which is being
added to the MarkLogic Java Client API. 