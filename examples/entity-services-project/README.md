This project shows where Entity Services (part of ML9) model definitions can be stored so that ml-gradle can be used
to generate artifacts for the models. Generating artifacts depends on the application having been deployed
already as a REST API server is required. Thus, a typical workflow is:

1. Stub out a project with a build.gradle and gradle.properties file
2. Run "gradle mlDeploy" to deploy the application
3. Create one or more model definitions in ./data/entity-services
4. Run "gradle mlGenerateModelArtifacts" (should be able to abbreviate it as "gradle mlgen") to use Entity Services
to generate model artifacts for each of the model definitions
5. Run "gradle mlDeploy" to deploy all of the model artifacts, which will include creating a schemas database if one
doesn't exist already

You can of course shorten the last 2 steps to "gradle mlgen mldeploy".
