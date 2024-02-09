This project contains a trigger that listens for documents being written to the `corticon` collection. 
The trigger executes the `src/main/ml-modules/root/trigger.sjs` module. This will soon be modified to do 
something far more interesting than logging the URI that it receives. 

To test this project out, follow these steps:

First, create `gradle-local.properties` and add the value to it:

```
mlHost=host name of the MarkLogic cluster you wish to connect to
mlUsername=your MarkLogic admin user username
mlPassword=password for your admin user
```

If you are using the EC2 instance associated with this project, you do not need to deploy this project's
application as it has already been deployed to that instance. Otherwise, just run `../gradlew -i mlDeploy` to 
deploy the application.

To write a document, run `../gradlew writeDocument`. Then go to the MarkLogic Admin UI (on port 8001) and do the
following:

1. Click on "Logs".
2. Click on the "8008_ErrorLog.txt" file (if that file doesn't exist, the trigger did not execute).
3. You should see a line like this: "2024-02-09 19:36:27.013 Info: Received URI: /data/test1.json"

You can delete that document by running `../gradlew deleteCollection`, which allows you to re-test repeatedly. 
The trigger is configured to only execute when a document is created, so overwriting it will not cause the trigger to 
execute.

