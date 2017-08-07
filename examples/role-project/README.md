This project shows how to create a role that refers to itself. See the
src/main/ml-config/security/roles directory for more information. 

As of version 2.9.0, you don't need to do anything special now. ml-gradle will detect that a role refers to itself, 
and it will deploy the role twice - once without its role dependencies, and again with its role dependencies. 

In version 2.9.0, ml-gradle will also figure out what order to deploy roles in - no need to order them yourself via
their filenames. 
