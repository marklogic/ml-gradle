package com.marklogic.gradle.task.client.document

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.client.ClientTask;

class PutDocumentTask extends ClientTask {

    String uri
    String category = "content"
    String format = "xml"
    String data
    String filePath
    String requestContentType = "application/xml"
     
    @TaskAction
    void putDocument() {
        if (filePath) {
            data = new File(filePath).text
        }
        newRestHelper().invoke("PUT", "/v1/documents?uri=" + uri + "&category=" + category + "&format=" + format, data, requestContentType)
    }
}
