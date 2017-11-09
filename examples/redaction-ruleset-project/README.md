This project shows an example of how MarkLogic 9 Redaction Rulesets can be loaded into a schemas
database from src/main/ml-schemas (the default path - this can be overridden via 
mlSchemasPath). 

Note that in order for this to work, the content-database.json file must specify the schema
database that it's associated with. And in most cases, you'll want your own schemas database - not the default Schemas one - so schemas-database.json can be used to create own with a name based on mlAppName.

Within each folder containing one or more Redaction Rulesets, you must provide a **collections.properties** and (optionally) a **permissions.properties** file.
These files contain the definitions for the applicable collections to be applied to the rulesets as well as the document permissions (if included).

***Note***: Rulesets must have a .json or .xml file extension.

See [Specifying collections and permissions](https://github.com/marklogic-community/ml-javaclient-util/wiki/Loading-files#specifying-collections-and-permissions) for information on how to apply the collections and permission when the rulesets are loaded

See [Redacting Document Content](http://docs.marklogic.com/guide/app-dev/redaction) for more information on redacting content
