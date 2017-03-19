This project shows how to deploy a sample temporal config - including lsqt config

Note that (as of this version), the deployed temporal config cannot be changed except for 

1. The temporal collection option properties (refer to https://docs.marklogic.com/temporal:collection-set-options for possible values)
1. The lsqt temporal collection option properties (refer to https://docs.marklogic.com/REST/PUT/manage/v2/databases/[id-or-name]/temporal/collections/lsqt/properties@collection=[name]) for possible values)
	1. Note that the lsqt config filename must correspond to the name of the temporal collection it is modifying
	1. E.g. ml-config/temporal/collections/lsqt/my-temporal-collection.json will modify the lsqt properties for the 'my-temporal-collection' temporal collection 
