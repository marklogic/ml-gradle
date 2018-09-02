This project shows an example of how to depend on an instance of ml-gradle
that you've published to your local Maven repository.

To publish an instance of ml-gradle locally, just do the following:

1. From the ml-gradle root directory, run "publish -Pversion=DEV publishToMavenLocal" (you can pick any version name that you want)
1. Verify manually that the library was published to ~/.m2/repository/com/marklogic/ml-gradle

You should then be able to run "gradle tasks" on this project, and that will use your
locally published copy of ml-gradle. Again, feel free to change the version name to whatever
you want, there's no requirement to use "DEV". 

Note that the "src" directory is gitignore'd in this project so that you can add any artifacts you want.
