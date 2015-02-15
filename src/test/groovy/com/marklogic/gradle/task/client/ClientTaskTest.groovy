package com.marklogic.gradle.task.client;

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

import com.marklogic.gradle.AppConfig

class ClientTaskTest {

    @Test
    public void test() {
        Project project = ProjectBuilder.builder().build()
        project.apply plugin: 'ml-gradle'

        //project.setProperty("mlUsername", "test-user")
        //project.setProperty("mlPassword", "test-password")
        AppConfig appConfig = project.property("mlAppConfig")
        appConfig.setRestPort(8123)

        // TODO How do we write a test for a custom task??
        //        ClientTask task = new ClientTask()
        //        task.setProject(project)
        //
        //        assertEquals(8123, task.getClientPort())
    }
}
