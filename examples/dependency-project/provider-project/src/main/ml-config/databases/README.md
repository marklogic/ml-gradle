This example shows how a bundle provider can contribute a range index to an application-specific database. The token 
"%%DATABASE%%" is used, which will be populated by ml-gradle by the main content database name (on a Data Hub project, 
this will be the name of the final database). The file could reference any property though - e.g. a provider may wish 
to require a property such as "acmeDatabaseName", and the client of the provider would need to set that property e.g. 
in gradle.properties so that it's correctly replaced at deployment time. 
