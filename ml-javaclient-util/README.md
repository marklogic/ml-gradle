ml-javaclient-util is a library of Java classes that provide common functionality on top of
the [MarkLogic Java Client API](http://docs.marklogic.com/guide/java). Features include:

- Support for [loading modules via the REST API](https://github.
	com/marklogic/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/modulesloader)
- Basic integration with [Spring via a Spring FactoryBean](https://github.com/marklogic/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/spring)
- Spring-style [template/callback library for XCC](https://github.com/marklogic/ml-javaclient-util/tree/master/src/main/java/com/marklogic/xcc/template)
- Support for [importing/exporting qconsole workspaces](https://github.com/marklogic/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/qconsole)

This is a lower-level library that is primarily used via [ml-app-deployer](https://github.com/marklogic/ml-app-deployer)
and [ml-gradle](https://github.com/marklogic/ml-gradle).

See the following Wiki pages for more information on some of the main features in this library:

1. [Loading files](https://github.com/marklogic/ml-javaclient-util/wiki/Loading-files), including modules
1. [DMSDK Support](https://github.com/marklogic/ml-javaclient-util/wiki/DMSDK-Support)
1. [Writing documents in batches](https://github.com/marklogic/ml-javaclient-util/wiki/Writing-documents-in-batches)
