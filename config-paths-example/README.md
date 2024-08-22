Running the following only deploys the database under src/main/ml-config:

    ../gradlew -i mldeploydatabases

Running this only deploys the database under src/main/uat-config:

    ../gradlew -i mldeploydatabases -PenvironmentName=uat
