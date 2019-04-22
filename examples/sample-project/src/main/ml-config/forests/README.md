See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/forests) for what a forest JSON/XML file can
contain.

Defining a content-forest.json file is optional - without it, ml-gradle will still create "plain vanilla" forests
for the main content database and the optional test content database.

There currently isn't a command that will just iterate over every forest file in the forests directory and process it.
Instead, similar to databases, you would write a new command that reads a specific file in the forests directory.

If you do make such a command, consider extending DeployForestsCommand, which supports the tokens %%FOREST_NAME%%, 
%%FOREST_HOST%%, and %%FOREST_DATABASE%%.
