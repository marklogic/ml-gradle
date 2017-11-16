Starting in version 2.3.0 of ml-gradle, both mlDeploy and mlDeployCustomForests will process
each directory under ./ml-config/forests. Each directory is assumed to be a database name, and thus the forest files
within each directory are created for the database with the same name as the directory.

Currently, only JSON files in the directories are supported. A JSON file may contain a single JSON object,
defining a single forest, or it can contain an array of many JSON objects, each defining a single forest. This
is a little different from how ml-gradle normally works, where the contents of a file match exactly what
the Management REST API expects. But in this scenario, where it's common to have many forests that are all
very similar in their configuration, it seemed worth supporting an array of objects to avoid creating a lot
of different files. 

Also, note that the custom forests all use "localhost". It's more common, in a large cluster, to specify
the exact host so you have precise control over where each forest is created.

