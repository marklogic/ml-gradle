This folder will contain application code ranging from library modules, REST API extensions, document transforms, etc. To allow the plugin to properly identify how each code is to be handled, certain naming conventions are used. Changes and additions to these files can be deployed using the following gradle task:

```
gradle mlLoadModules
```

Smaller commands can be used to deploy specific sub-folders of this main folder. More info is available in the [Task Reference](TODO: task page to be built) page

## ext

This folder would contain your application logic. It is considered good practice to store all application libraries and assets in this directory to avoid any chance of naming collisions with REST API-managed modules. Content under this folder will retain the "/ext" prefix for all files. In the example above, `my-custom-code.xqy` will get uploaded as `/ext/my-custom-code.xqy` 

## options

This folder contains search options that can be used as 'options' parameter to '/v1/search' REST API. In the example above, `my-rest-api-search-options.xqy` can be used as `/v1/search?options=my-rest-api-search-options`.

More information about search options is available [here](https://docs.marklogic.com/guide/search-dev/appendixa)

## services

This folder will contain REST API extensions and can be accessed using `/v1/ext/file-name`. More information about REST API extensions is available [here](https://docs.marklogic.com/guide/rest-dev/extensions)

## transforms

This folder will contain document transforms. These are used in conjunction with the REST API. More information about content transforms are available [here](https://docs.marklogic.com/guide/rest-dev/transforms).

## namespaces 

This folder will contain namespace declarations. More information about content transforms are available [here](https://docs.marklogic.com/guide/admin/namespaces).

## "unrecognized" directories

Prior to ml-gradle 3.0.0, the URI of each such document would include the name of the unrecognized directory, i.e.:
```
ml-modules
├───my-module
│       my-file.xqy
│
└───my-web
        my-page.html
```
will get loaded as `/my-module/my-file.xqy` and `/my-web/my-page.html` respectively.

But with version 3.0.0 and greater, the URI does not have the unrecognized directory name which will result in the files getting uploaded as `/my-file.xqy` and `/my-page.html`, respectively.

It is recommended to use "ext" and "root" for your application code.