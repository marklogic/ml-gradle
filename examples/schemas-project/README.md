This project shows an example of how MarkLogic 9 schemas can be loaded into a schemas
database from src/main/ml-schemas (the default path - this can be overridden via 
mlSchemasPath). 

Note that in order for this to work, the content-database.json file must specify the schema
database that it's associated with. And in most cases, you'll want your own schemas database - not the default Schemas one - so schemas-database.json can be used to create own with a name based on mlAppName.