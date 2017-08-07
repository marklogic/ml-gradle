This project shows an example of configuring replica forests for the out-of-the-box databases that are typically worth 
enabling failover. See https://github.com/marklogic-community/ml-gradle/wiki/Property-reference for more properties
for configuring the replica forests that are created.

Note that you'll need to run this against a cluster with at least two nodes (failover requires three nodes, but you
only need two nodes to see how ml-gradle will create replica forests).

For complete control of primary and replica forests, check out 
the [custom forests and replicas](../custom-forests-and-replicas-project) project.
