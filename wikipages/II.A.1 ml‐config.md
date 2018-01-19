This folder would contain various MarkLogic configuration details. These would control how resources like databases, roles, servers, etc. behave. Changes to any of the files under this folder would require the execution of the following command:

```
gradle mlDeploy
```

Smaller commands can be used to deploy specific sub-folders of this main folder. More info is available in the [Task Reference](TODO: task page to be built) page

## alert
```
ml-config
├───alert
│   └───configs
│       │   another-alert-config.xml
│       │   sample-alert-config.xml
│       │
│       ├───sample-alert-config-actions
│       │       log-alert.xml
│       │
│       └───sample-alert-config-rules
│               sample-rule.xml
│
└───databases
    └───content-database
        └───alert
            └───configs
                │   sample-alert-config.xml
                ├───sample-alert-config-actions
                │       log-alert.xml
                │
                └───sample-alert-config-rules
                       sample-rule.xml
```
This folder is used to configure [alerting applications in MarkLogic.](https://docs.marklogic.com/guide/search-dev/alerts). An [example project](https://github.com/marklogic-community/ml-gradle/tree/master/examples/alert-project), and an example of [database-specific alerting](https://github.com/marklogic-community/ml-app-deployer/tree/master/src/test/resources/sample-app/alert-config/databases) are available

Some naming conventions are expected. An alert's rules need to be added inside a folder of the same name with suffix of "-rules" while an alert's actions can be declared inside a folder of the same name with suffix of "-actions" as shown above.

## cpf
```
ml-config
└───cpf
    ├───cpf-configs
    │       sample-cpf-config.json
    │
    ├───domains
    │       sample-domain.json
    │
    └───pipelines
            sample-pipeline.json
            status-change-handling.xml
```
More information about CPF is available in the [MarkLogic docs](https://docs.marklogic.com/guide/cpf/overview). A working example is available in the [examples](https://github.com/marklogic-community/ml-gradle/tree/master/examples/cpf-project) of this project.

Although there are no file naming rules affecting the upload, it is recommended to maintain the same prefix for related configurations.

### cpf-configs
See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/cpf-configs) for what a 
CPF configuration JSON/XML file can contain.

### domains
See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/domains) for what a 
CPF domain JSON/XML file can contain.

### pipelines
See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/pipelines) for what a 
CPF pipeline JSON/XML file can contain.

Note that as of MarkLogic 8.0-3, the pipeline schema does not allow for a condition element to have a nested options
element. This will be fixed in 8.0-4. In the meantime, the best way to work around this is to create your own 
condition module that does not require an options element - in effect, move the logic expressed by the options element
into a custom condition. 

Additionally, files that does not end with '.json' and '.xml' are ignored.

## databases

This will configure databases to be created. A minimum requirement is to specify the `database-name`. More information about database properties that can be configured is available [in the online documentation](https://docs.marklogic.com/REST/POST/manage/v2/databases). 

If your application deploys a REST API server, ml-gradle will look for "./databases/content-database.json" as the content database to use for this REST API server. This allows for the database to be created with a certain number of forests before the call to /v1/rest-apis. 

Note that some resources can be defined in directories under "./databases/(name of database)" - see this [ml-app-deployer Wiki page](https://github.com/marklogic-community/ml-app-deployer/wiki/Deploying-resources-to-databases) for more information. 

## flexrep
```
ml-config
├───flexrep
│   └───configs
│       │   flexrep-example-domain.xml
│       └───flexrep-example-domain-targets
│               target-1.xml
└───databases
    └───content-database
        └───flexrep
            └───configs
                │   flexrep-example-domain.xml
                └───flexrep-example-domain-targets
                        target-1.xml
```
### configs

Will contain a number of flexrep configurations. Each `domain-name` within each xml configuration should have a folder counterpart that contains a suffix "-targets". That folder will contain the configuration of the target host.

It's recommended that each xml configuration's name would match the `domain-name` contained within.

## forests
```
ml-config
└───forests
    │   content-forest.json
    │
    └───custom-forest-example-content
            custom-forests.json
            single-custom-forest.json
```
Unless completely necessary, we recommend creating forests using the property approach, i.e. to make use of [gradle.properties](https://github.com/kghmanuel/ml-gradle/wiki/II.B-gradle.properties-reference#database-and-forest-properties). If high level of control over forest creation is needed, the following section will help.

See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/forests) for what a forest JSON/XML file can
contain.

Defining a content-forest.json file is optional - without it, ml-gradle will still create "plain vanilla" forests
for the main content database and the optional test content database. The DeployContentDatabasesCommand is configured 
to look for content-forest.json by default, and if it exists, that command will use that file for creating content
forests.

There currently isn't a command that will just iterate over every forest file in the forests directory and process it.
Instead, similar to databases, you would write a new command that reads a specific file in the forests directory.

If you do make such a command, consider extending DeployForestsCommand, which supports the tokens %%FOREST_NAME%%, 
%%FOREST_HOST%%, and %%FOREST_DATABASE%%.

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

## groups

Group configurations are used to control cache sizes of hosts that belong to a particular group. Please refer to [knowledge base](https://help.marklogic.com/Knowledgebase/Article/View/420/18/group-level-cache-settings-based-on-ram) for recommended values relative to available memory.

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/groups) for what a group JSON/XML file can
contain.

## mimetypes

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/mimetypes) for what a mimetype JSON/XML file can
contain.

## security
```
ml-config
└───security
    ├───amps
    │       amp-1.json
    │
    ├───certificate-authorities
    │       template-1.xml
    │
    ├───certificate-templates
    │       template-1.xml
    │
    ├───external-security
    │       external-security-1.xml.old
    │
    ├───privileges
    │       my-privilege.json
    │
    ├───protected-collections
    │       my-collection.json
    │
    ├───protected-paths
    │       my-path.json
    │
    ├───query-rolesets
    │       my-path.json
    │
    ├───roles
    │       my-app-role.json
    │
    └───users
            my-user.json
```
### amps

Amps are used to elevate execution privileges for a particular function. See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/amps) for what an amp JSON/XML file can contain.

### certificate-authorities

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/certificate-authorities) for details.

### certificate-templates

See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/certificate-templates) for what a certificate 
template JSON/XML file can contain.

### privileges

See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/privileges) for what a privilege JSON/XML file can contain.

### protected-collections

See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/protected-collections) for what a protected 
collection JSON/XML file can contain.

Note that these protected collections apply to the entire cluster and not just a single application. Make sure that these collections do not overlap with other applications hosted or that the application is the only one hosted in the target cluster.

### protected-paths

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/protected-paths) for what a protected 
path JSON/XML file can contain.

Note that these protected paths apply to the entire cluster and not just a single application. Make sure that these paths do not overlap with other applications hosted or that the application is the only one hosted in the target cluster.

### query-rolesets

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/query-rolesets) for what a query roleset JSON/XML file can contain.

### roles
This will configure the different roles that the application is expected to use. More information about database properties that can be configured is available [in the online documentation](https://docs.marklogic.com/REST/POST/manage/v2/roles). 

It is recommended to use placeholders in defining roles to avoid overwrite from other projects.

### users

This will configure the different users. More information about database properties that can be configured is available [in the online documentation](https://docs.marklogic.com/REST/POST/manage/v2/users). 

Care should be taken when specifying usernames as these may conflict with pre-existing users.

## servers

The rest-api-server.json file uses a special token that references the ID of a certificate template. This token is set during mlDeploy - specifically, after the certificate template has been created or updated. This solves a problem where the config file must reference an ID, but there's no way to know what that ID is until the certificate template has been created. 

The downside to this approach is that you cannot run a task that updates the server if that task does not also run a command to create/update the certificate template; otherwise, ml-gradle will not have a value with which to replace the token. 

If you want your REST API server to use basic authentication, as of ml-gradle 2.6.0, you'll need to do 2 things.

First, because the [/v1/rest-apis](http://docs.marklogic.com/REST/POST/v1/rest-apis) endpoint doesn't allow for an authentication strategy to be set, you'll have to modify the REST API server via ml-config/servers/rest-api-server.json, e.g.:
```
{
  "server-name": "%%NAME%%",
  "authentication": "basic"
}
```
Then, you'll need to modify the ml-gradle configuration so that the DatabaseClient that's constructed uses basic auth instead of digest:
```
ext {
  mlAppConfig {
    restAuthentication = com.marklogic.client.DatabaseClientFactory.Authentication.BASIC
  }
}
```
Or if you're using version 2.7.0 or higher, just set a property:
```
mlRestAuthentication=BASIC
```

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/servers) for what a server JSON/XML file can contain.

## tasks

See [the MarkLogic docs](http://docs.marklogic.com/REST/POST/manage/v2/tasks) for what a scheduled task JSON/XML file can contain.

## temporal
```
ml-config
├───temporal
│   ├───axes
│   │       temporal-system-axis.json
│   │       temporal-valid-axis.json
│   │
│   └───collections
│       │   temporal-collection.json
│       │
│       └───lsqt
│               temporal-collection.json
└───databases
    └───content-database
        └───temporal
            ├───axes
            │       temporal-system-axis.json
            │       temporal-valid-axis.json
            │
            └───collections
                │   temporal-collection.json
                │
                └───lsqt
                        temporal-collection.json
```
See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/temporal/axes) for what a temporal axis JSON/XML file can contain.
See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/temporal/collections) for what a temporal collection JSON/XML file can contain.
See [the MarkLogic docs](https://docs.marklogic.com/REST/PUT/manage/v2/databases/[id-or-name]/temporal/collections/lsqt/properties@collection=[name]) for what a temporal lsqt JSON/XML file can contain.

## triggers

See [the MarkLogic docs](https://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/triggers) for what a triggers JSON/XML file can contain.

## view-schemas
```
ml-config
├───view-schemas
│   │       my-schema.json
│   │
│   └───my-schema
│       my-view.json
│
└───databases
    └───content-database
        └───view-schemas
            │       my-schema.json
            │
            └───my-schema
                    my-view.json
```
These refer to [SQL Schemas and Views](http://docs.marklogic.com/REST/management/sql-schemas-and-views). This is different from Template Driven Extraction (TDE) which is handled in ml-schemas.

For each view schema that ml-gradle processes in this directory, it will look for a directory with a name of "(view schema name)-views". If it finds such a directory, it will process each file within the directory as a SQL view. This `view schema name` refers to the `view-schema-name` property within the JSON/XML file and not the file name.

In [this example](https://github.com/marklogic-community/ml-gradle/tree/master/examples/sample-project/src/main/ml-config/view-schemas), the name of the view schema is "main", and so the "main-views" directory contains the views to be created in association with this schema.