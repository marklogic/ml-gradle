## Overview

Example of how to use [jsdoc](http://usejsdoc.org/) in MarkLogic project

This project uses the [JSDoc Gradle Plugin](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins-jsdoc) to run the jsdoc task


## Usage

All of the configuration is captured in the __build.gradle__ file and the __jsdoc.json__ file. To test this, clone this repoitory and execute the command below in this directory 

```
gradle mlJsdoc
```

This will generate jsdoc for the code in __src/main/ml-modules/lib__

The html documentation will be generated in __build/docs/jsdoc__

## Customize

To customise the jsdoc output, you can modify the __jsdoc.json__ file. Refer to the http://usejsdoc.org/about-configuring-jsdoc.html site for details on how to configure this.

To customise the jsdoc task generator, please customize the __mlJsDoc__ task in the __build.gradle__ file. Refer to the [JSDoc Gradle Plugin](https://github.com/liferay/liferay-portal/tree/master/modules/sdk/gradle-plugins-jsdoc) site for details on how to configure this.


