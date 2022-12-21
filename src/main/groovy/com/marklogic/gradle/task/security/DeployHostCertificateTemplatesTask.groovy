package com.marklogic.gradle.task.security

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class DeployHostCertificateTemplatesTask extends MarkLogicTask {

	@TaskAction
	void deployHostCertificateTemplates() {
		invokeDeployerCommandWithClassName("InsertCertificateHostsTemplateCommand")
	}

}
