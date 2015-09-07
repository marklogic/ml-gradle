The ml-modules directory contains all of the modules for an application. Its structure is based on that of the
[MarkLogic samplestack reference application](https://github.com/marklogic/marklogic-samplestack/tree/master/database):

- /ext contains libraries and other application assets; these map to the /v1/ext endpoint in the Client API, though
they are still loaded via XCC, which is much more efficient than using that endpoint. It is considered good practice
to store all application libraries and assets in this directory to avoid any chance of naming collisions with REST API-managed
modules.
- /options contains search options, to be used with /v1/search in the Client API
- /root contains libraries that for some reason cannot be stored under /ext; the best example of this is when you want
to override modules in the Marklogic/Modules directory, such as overriding a REST API module. In that case, you cannot
put them under /ext because the path won't match that of the MarkLogic module.  
- /services contains [custom services](http://docs.marklogic.com/REST/GET/v1/config/resources), exposed via /v1/resources/(name of service)
- /transforms contains [custom transforms](http://docs.marklogic.com/REST/GET/v1/config/transforms), to be used with /v1/documents and /v1/search in the Client API

In addition, the rest-properties.json file in the root of this directory is used to configure the 
[properties of the REST API server](http://docs.marklogic.com/REST/GET/v1/config/properties). Note that these properties
are different from the configuration of the server itself - the properties are specific to the REST API out-of-the-box
endpoints.  