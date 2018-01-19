This projects shows a simple example of using the PostgreSQL JDBC driver to connect to 
a MarkLogic ODBC server. 

Just deploy the app (verify the connection properties in gradle.properties):

    gradle mlDeploy

And then run this task to run a simple query - SELECT SCHEMA, NAME FROM SYS_TABLES - against the MarkLogic ODBC server:

    gradle selectFromSysTables

And you should see logging like this:

    SCHEMA: sys, NAME: sys_columns
    SCHEMA: sys, NAME: sys_collations
    SCHEMA: sys, NAME: sys_functions
    SCHEMA: sys, NAME: sys_schemas
    SCHEMA: sys, NAME: sys_tables

The Main program uses Spring's JDBC API to simplify using JDBC. You can use this example to roll
your own JDBC program using the PostgreSQL driver to connect to MarkLogic.
