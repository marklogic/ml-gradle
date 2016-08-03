See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/view-schemas) for what a 
SQL view schema and view JSON/XML file can contain.

For each view schema that ml-gradle processes in this directory, it will look for a directory with a name
of "(view schema name)-views". If it finds such a directory, it will process each file within the directory
as a SQL view. In this example, the name of the view schema is "main", and so the "main-views" directory
contains the views to be created in association with this schema. 
