## Overview

Example of how to use [jsdoc](http://usejsdoc.org/) in MarkLogic project

This project uses the [JSDoc Gradle Plugin](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins-jsdoc) to run the jsdoc task


## Usage

Configuration is captured in __build.gradle__, __gradle.properties__, and __jsdoc.json__.

To test:

1. Clone this repoitory.
1. Review the default property values provided in __gradle.properties__.  If any do not match your environment, override in __gradle-local.properties__ (not provided).
1. Run `gradle generateJsDoc`

This will generate jsdoc for the code in __src/main/ml-modules/lib__

The html documentation will be generated in __build/docs/jsdoc__

Tested with Java 11, Gradle 5.6.4, ml-gradle 4.0.4, Node.js 12.18.3, Liferay node 7.2.0, and Liferay app jsdoc 2.0.54 on Windows 10 Pro 64-bit.

## Customize

To customise the jsdoc output, you can modify the __jsdoc.json__ file. Refer to the http://usejsdoc.org/about-configuring-jsdoc.html site for details on how to configure this.

To customise the jsdoc task generator, please customize the __generateJsDoc__ task in the __build.gradle__ file. Refer to the [JSDoc Gradle Plugin](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins-jsdoc) site for details on how to configure this.


