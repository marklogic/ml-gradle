The rest-api.json file in the root of the ml-config directory is used for the initial call to 
[the Client REST API](http://docs.marklogic.com/REST/POST/v1/rest-apis) to create a REST API server. This API is 
separate from the Management REST API, and it only allows for a handful of configuration properties. But it's a very
handy API to use, as it automatically creates content and modules databases to with the REST API server. 

You don't need to include a rest-api.json file in this directory; if you don't, some sensible defaults will be used
by ml-gradle.

Note that in order to "fully" configure the REST API server - e.g. modifying its authentication strategy or its 
rewriter - you will instead use ml-config/servers/rest-api-server.json, which is instead processed via the 
[endpoint for creating and updating servers](http://docs.marklogic.com/REST/PUT/manage/v2/servers/[id-or-name]/properties).
