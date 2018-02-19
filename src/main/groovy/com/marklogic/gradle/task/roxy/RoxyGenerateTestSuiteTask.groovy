package com.marklogic.gradle.task.roxy

import org.gradle.api.tasks.TaskAction

// Task mlRoxyGenerateTestSuite : This task uses Groovy's SimpleTemplateEngine to customize the sample test suite
// Parameters:
//    suiteName       (Default: SampleTestSuite)
//        - Specify the name of the generated test suite
//    testName        (Default: SampleTest)
//        - Specify the name of the generated test
// Examples:
//    gradle mlRoxyGenerateTestSuite
//    gradle mlRoxyGenerateTestSuite -PsuiteName="foo" -PtestName="bar"
class RoxyGenerateTestSuiteTask extends RoxyTask {
    CommandLineArguments arguments

    @TaskAction
    void generateTestSuite() {
        this.arguments = new CommandLineArguments()
        
        scaffoldFromTemplates()
		println "Finished generating Roxy test suite."
    }

    def scaffoldFromTemplates() {	    
        def binding = ["testName":arguments.testName, "suiteName":arguments.suiteName]
        def engine = new groovy.text.SimpleTemplateEngine()

        project.file(arguments.targetSuiteDirName).mkdirs()
        def sampleTestsXqyString = SampleTestFiles.getSampleTestsXqy()
        processTemplateString(engine, binding, arguments.targetSuiteDirName, "/sample-tests.xqy", sampleTestsXqyString)
        def suiteSetupXqyString = SampleTestFiles.getSuiteSetupXqy()
        processTemplateString(engine, binding, arguments.targetSuiteDirName, "/suite-setup.xqy", suiteSetupXqyString)
        def suiteTeardownXqyString = SampleTestFiles.getSuiteTeardownXqy()
        processTemplateString(engine, binding, arguments.targetSuiteDirName, "/suite-teardown.xqy", suiteTeardownXqyString)
        def setupXqyString = SampleTestFiles.getSetupXqy()
        processTemplateString(engine, binding, arguments.targetSuiteDirName, "/setup.xqy", setupXqyString)
        def teardownXqyString = SampleTestFiles.getTeardownXqy()
        processTemplateString(engine, binding, arguments.targetSuiteDirName, "/teardown.xqy", teardownXqyString)
    }

    def processTemplateString(engine, binding, targetBaseDir, templateFilePath, templateString) {
        def template = engine.createTemplate(templateString).make(binding)
        def templateResult = template.toString()
        def targetFilepath = targetBaseDir + templateFilePath
        project.file(targetFilepath).write(templateResult)
		println "Generated test suite file: " + targetFilepath
    }

    class CommandLineArguments {
        String defaultSuitesDirName = "src/test/ml-modules"
        String suiteName = 'SampleTestSuite'
        String targetSuiteDirName = defaultSuitesDirName + '/' + suiteName
        String testName = 'SampleTest'

        CommandLineArguments() {
            if (project.hasProperty('suiteName')) {
                this.suiteName = project.property('suiteName')
            }
            this.targetSuiteDirName = this.defaultSuitesDirName + '/' + suiteName
            if (project.hasProperty('testName')) {
                this.testName = project.property('testName')
            }
        }
    }
}
