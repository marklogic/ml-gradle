package com.marklogic.gradle.task

import com.marklogic.xcc.ContentSource
import com.marklogic.xcc.SecurityOptions
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.xcc.XccHelper

/**
 * Simple task for easing the execution of ad hoc XQuery.
 */
class XccTask extends MarkLogicTask {

	@Input
	String xquery

	@Input
	@Optional
	String xccUrl

	@Input
	@Optional
	SecurityOptions securityOptions

	@Input
	@Optional
	ContentSource contentSource

    @TaskAction
    void executeXcc() {
	    XccHelper helper = null
	    if (contentSource != null) {
		    helper = new XccHelper(contentSource)
	    } else if (securityOptions != null) {
		    helper = new XccHelper(xccUrl, securityOptions)
	    } else {
		    helper = new XccHelper(xccUrl)
	    }
        helper.executeXquery(xquery)
    }
}
