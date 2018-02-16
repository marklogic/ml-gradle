package com.marklogic.gradle.task.roxy

import org.gradle.api.tasks.TaskAction

//import com.marklogic.gradle.task.roxy.scaffoldTemplates.SampleTestFiles

// Task mlGenerateRoxyTest : This task uses Groovy's SimpleTemplateEngine to customize the sample test suite
// Parameters:
//    suiteName       (Default: SampleTestSuite)
//        - Specify the name of the generated test suite
//    testName        (Default: SampleTest)
//        - Specify the name of the generated test
// Examples:
//    gradle mlGenerateRoxyTest
//    gradle mlGenerateRoxyTest -PsuiteName="foo" -PtestName="bar"
class RoxyGenerateTestScaffoldTask extends RoxyTask {
    CommandLineArguments arguments

    @TaskAction
    void generateScaffoldTask() {
        println "Generate Roxy Test Scaffold"
        
        this.arguments = new CommandLineArguments()
        
        scaffoldFromTemplates()
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

        def sourceTestDataDirectory = project.file(DefaultValues.resourcesDirName + "/" + DefaultValues.sampleDirName + "/test-data")
        def targetTestDataDirectoryName = arguments.targetSuiteDirName + "/test-data"
        def targetTestDataDirectory = project.file(targetTestDataDirectoryName)
        project.file(targetTestDataDirectoryName).mkdirs()
        project.copy {
            from sourceTestDataDirectory
            into targetTestDataDirectory
            include '**'
        }
    }

    def processTemplateFile(engine, binding, targetBaseDir, templateFilePath) {
        def templateFile = project.file(DefaultValues.resourcesDirName + "/" + DefaultValues.templateDirName + templateFilePath)
        def template = engine.createTemplate(templateFile).make(binding)
        def templateResult = template.toString()
        project.file(targetBaseDir + templateFilePath).write(templateResult)
    }

    def processTemplateString(engine, binding, targetBaseDir, templateFilePath, templateString) {
        def template = engine.createTemplate(templateString).make(binding)
        def templateResult = template.toString()
        project.file(targetBaseDir + templateFilePath).write(templateResult)
    }

    class DefaultValues {
        static String defaultSuiteName = 'SampleTestSuite'
        static String templateDirName = 'test-templates'
        static String defaultTestName = 'SampleTest'
        static String resourcesDirName = 'src/main/resources'
        static String sampleDirName = 'sample-tests'
    }

    class CommandLineArguments {
        String roxySuitesDirName = getAppConfig().getModulePaths().last()
        String suiteName = DefaultValues.defaultSuiteName
        String targetSuiteDirName = roxySuitesDirName + '/' + suiteName
        String testName = DefaultValues.defaultTestName

        CommandLineArguments() {
            if (project.hasProperty('suiteName')) {
                this.suiteName = project.property('suiteName')
            }
            this.targetSuiteDirName = this.roxySuitesDirName + '/' + suiteName
            if (project.hasProperty('testName')) {
                this.testName = project.property('testName')
            }
        }
    }
}
