A typical project will have at least the following resources:
```
project_root
│       build.gradle
│       gradle.properties
│       gradle-local.properties
└───src
    └───main
        ├───ml-config
        │   ├───databases
        │   │       content-database.json
        │   │
        │   └───security
        │       ├───roles
        │       │       my-app-role.json
        │       │
        │       └───users
        │               my-app-user.json
        │    
        ├───ml-modules
        │   ├───ext
        │   │       my-custom-code.xqy
        │   │
        │   └───options
        │           my-rest-api-search-options.xml
        │    
        └───ml-schemas
                    my-template.json
```

Other directories and files can exist within the project but the following list are treated in a special way by this gradle plugin. 

The following list provides a quick reference of the tasks to easily reflect each folder into MarkLogic:

|Folder | Gradle Task|
|---|---|
|ml-config|mlDeploy|
|ml-modules|mlLoadModules|
|ml-schemas|mlLoadSchemas|

A project that makes use of all the MarkLogic resources would have a structure similar to the following:
```
src
└───main
    ├───ml-config
    │   ├───alert
    │   │   └───configs
    │   │       ├───sample-alert-config-actions
    │   │       └───sample-alert-config-rules
    │   ├───cpf
    │   │   ├───cpf-configs
    │   │   ├───domains
    │   │   └───pipelines
    │   ├───databases
    │   ├───flexrep
    │   │   ├───configs
    │   │   │   └───flexrep-example-domain-targets
    │   │   ├───master
    │   │   │   ├───cpf
    │   │   │   │   ├───cpf-configs
    │   │   │   │   ├───domains
    │   │   │   │   └───pipelines
    │   │   │   ├───flexrep
    │   │   │   │   └───configs
    │   │   │   │       └───master-domain-targets
    │   │   │   └───servers
    │   │   └───replica
    │   │       ├───cpf
    │   │       │   ├───cpf-configs
    │   │       │   ├───domains
    │   │       │   └───pipelines
    │   │       ├───flexrep
    │   │       │   └───configs
    │   │       │       └───replica-domain-targets
    │   │       └───servers
    │   ├───forests
    │   │   └───custom-forest-example-content
    │   ├───groups
    │   ├───mimetypes
    │   ├───security
    │   │   ├───amps
    │   │   ├───certificate-templates
    │   │   ├───external-security
    │   │   ├───privileges
    │   │   ├───protected-collections
    │   │   ├───roles
    │   │   └───users
    │   ├───servers
    │   ├───tasks
    │   ├───temp
    │   ├───temporal
    │   │   ├───axes
    │   │   └───collections
    │   │       └───lsqt
    │   ├───triggers
    │   └───view-schemas
    │       └───main-views
    ├───ml-modules
    │   ├───ext
    │   ├───options
    │   ├───root
    │   ├───services
    │   ├───transforms
    │   └───ui
    └───ml-schemas
        ├───redactionRules
        └───tde
```

More information about each of the folders listed above are available at [[II.A.1 ml‐config]], [[II.A.2 ml‐modules]], and [[II.A.3 ml‐schemas]]