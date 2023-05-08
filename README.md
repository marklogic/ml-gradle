![GitHub release](https://img.shields.io/github/release/marklogic/ml-javaclient-util.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/marklogic/ml-javaclient-util.svg)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Known Vulnerabilities](https://snyk.io/test/github/marklogic/ml-javaclient-util/badge.svg)](https://snyk.io/test/github/marklogic/ml-javaclient-util)

ml-javaclient-util is a library of Java classes that provide some useful functionality on top of 
the [MarkLogic Java Client API](http://docs.marklogic.com/guide/java). Those features include:

- Support for [loading modules via the REST API](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/modulesloader)
- Basic integration with [Spring via a Spring FactoryBean](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/spring)
- Library for [parallelizing batched writes](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/batch)
- Spring-style [template/callback library for XCC](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/xcc/template)
- Support for generating MarkLogic 9 [Entity Services modules](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/es)
- Support for [importing/exporting qconsole workspaces](https://github.com/rjrudin/ml-javaclient-util/tree/master/src/main/java/com/marklogic/client/ext/qconsole)
 
This is a lower-level library that is primarily used via [ml-app-deployer](https://github.com/rjrudin/ml-app-deployer) 
and [ml-gradle](https://github.com/rjrudin/ml-gradle) and [ml-junit](https://github.com/rjrudin/ml-junit). But you can use it by itself too.

See the following Wiki pages for more information on some of the main features in this library:

1. [Loading files](https://github.com/marklogic/ml-javaclient-util/wiki/Loading-files), including modules
1. [DMSDK Support](https://github.com/marklogic/ml-javaclient-util/wiki/DMSDK-Support)
1. [Writing documents in batches](https://github.com/marklogic/ml-javaclient-util/wiki/Writing-documents-in-batches)
