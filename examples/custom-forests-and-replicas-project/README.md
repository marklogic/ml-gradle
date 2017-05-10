This project shows an example of configuring any forests and replicas you'd like for a database. See
the forests/custom-forest-example-content directory.

 ml-gradle 2.7.0 provided a fix where prior to that version, default forests were still created for a database. In 2.7.0,
 ml-gradle is smart enough not to create default forests if custom forests have been specified. 
