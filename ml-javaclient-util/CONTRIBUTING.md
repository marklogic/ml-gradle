To run the tests for ml-javaclient-util, clone this repository and deploy this project's test application first via 
the following steps:

1. Create a file named `gradle-local.properties`.
2. Add `mlPassword=` followed by the password for your MarkLogic admin user.
3. Verify that port 8006 is available on your computer - i.e. no other process is listening on it.
4. Run `./gradlew -i mlDeploy`.

You can then run `./gradlew -i test` to run all of the tests.
