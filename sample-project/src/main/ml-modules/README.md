The ml-modules directory contains all of the modules for an application. Its structure is based on that of the
[MarkLogic samplestack reference application](https://github.com/marklogic/marklogic-samplestack/tree/master/database):

- /ext contains libraries and other application assets
- /options contains search options, to be used with /v1/search in the Client API
- /services contains [custom services](http://docs.marklogic.com/REST/GET/v1/config/resources), exposed via /v1/resources/(name of service)
- /transforms contains [custom transforms](http://docs.marklogic.com/REST/GET/v1/config/transforms), to be used with /v1/documents and /v1/search in the Client API

In addition, the rest-properties.json file in the root of this directory is used to configure the 
[properties of the REST API server](http://docs.marklogic.com/REST/GET/v1/config/properties). Note that these properties
are different from the configuration of the server itself - the properties are specific to the REST API out-of-the-box
endpoints.  