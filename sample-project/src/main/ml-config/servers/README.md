See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/servers) for what a server JSON/XML file can contain.

ml-gradle will always look for a file named "rest-api-server.json" in this directory. If found, this file is used to
update the REST API application whose name equals that of the "mlAppName" property. ml-gradle is really designed for
applications based on the REST API, so it's likely that you'll want to use this file to configure the REST API server. 
Note that the endpoint for creating a REST API application - http://docs.marklogic.com/REST/POST/v1/rest-apis - is
part of the Client REST API and only provides a few options for configuring the REST API server. You can override 
those options via the ml-config/rest-properties.json file. 

ml-gradle will process any other .json/.xml file found in this directory, creating/updating a server as necessary.

Note that while this example shows an XDBC server being created, you won't often need one with MarkLogic, as you can
either use port 8000 for XCC calls, or you can use the new [/v1/eval](http://docs.marklogic.com/REST/POST/v1/eval) 
endpoint on a REST API server.
 