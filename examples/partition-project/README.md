This project shows a simple example of deploying partitions. As noted in the docs for 
[configuring query partitions](https://docs.marklogic.com/guide/admin/tiered-storage#id_31779), this example has the 
following configured on the content database:

- The assignment policy is set to "range" with a partition key of "myDate"
- A range index on "myDate"
- Locking is set to "strict"
- And a schemas database is associated with the content database as well (GET calls to the Manage API for partitions fail otherwise)

Alternatively, partition queries can be deployed by using an assignment policy of "query" and adding files to 
./src/main/ml-config/databases/partition-example-content/partition-queries based on the 
[partition query docs](http://docs.marklogic.com/REST/POST/manage/v2/databases/[id-or-name]/partition-queries).

To try this example out, first deploy the application:

    gradle mlDeploy

This will result in 3 forests - one "default" forest with no partition assigned, and then two forests based on 
partitions - "myDate-2011" and "myDate-2012". Note also that the "mlAddHostNameTokens" property is set so that the 
partition files can refer to a host name dynamically via the "mlHostName1" token as opposed to being hardcoded to a 
specific host name. See these [ml-app-deployer docs](https://github.com/marklogic/ml-app-deployer/wiki/Scheduled-Tasks#referring-to-host-names-in-scheduled-task-files) for more information. 

You can then insert documents via qconsole (or any other ML interface) to test out the range assignments - e.g.

```
declareUpdate();
xdmp.documentInsert("test1.json", {"myDate":"2011-01-01"});
xdmp.documentInsert("test2.json", {"myDate":"2012-01-01"})
```

You can then use the ML Admin GUI to verify that both myDate-* forests have 1 document each.

Additionally, ml-gradle has tasks for taking partitions online and offline - e.g.

```
gradle mlTakePartitionOffline -Pdatabase=partition-example-content -Ppartition=myDate-2011
gradle mlTakePartitionOnline -Pdatabase=partition-example-content -Ppartition=myDate-2011
```
