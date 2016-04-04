This project shows an example of managing both the master and replica config in the
flexrep/master and flexrep/replica directories. 

The use case for this is when you have an application that should be deployed on both the
master and replica clusters in the exact way, with one exception - how flexrep is configured.

The property "mlFlexrepPath" can be set to specify the path name under 
src/main/ml-config/flexrep that contains the configuration to load, based on whether the app
is being deployed to the master cluster or the replica cluster.
