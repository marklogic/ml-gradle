[![Build Status](https://travis-ci.org/rjrudin/ml-javaclient-util.svg?branch=master)](https://travis-ci.org/rjrudin/ml-javaclient-util)

ml-javaclient-util is a library of Java classes that provide some useful functionality on top of 
the [MarkLogic Java Client API](http://docs.marklogic.com/7.0/guide/java). Those features include:

- Support for loading any kind of module using the REST API
- Support for automatically loading a new/modified module using the REST API
- Basic integration with Spring via a Spring FactoryBean

These features have been written for MarkLogic 7 and do not yet take advantage of all of the new REST API endpoints in 
MarkLogic 8. Version 2 of this library will provide that kind of support. 
