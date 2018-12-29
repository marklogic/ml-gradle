package com.marklogic.gradle.task


import com.marklogic.appdeployer.command.security.DeployRolesCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer
import com.marklogic.mgmt.util.ObjectMapperFactory
import com.marklogic.rest.util.PreviewInterceptor
import org.gradle.api.tasks.TaskAction

/**
 * Extends DeployAppTask and applies an instance of PreviewInterceptor to the RestTemplate objects associated with
 * the ManageClient created by MarkLogicPlugin. Because of this extension, a user can use the "ignore" property
 * supported by the parent class to ignore any commands that cause issues with doing a preview.
 */
class PreviewDeployTask extends DeployAppTask {

	@TaskAction
	void deployApp() {
		// Disable loading of any modules
		getAppConfig().setModulePaths(new ArrayList<String>())

		// Disable loading of any schemas
		getAppConfig().setSchemasPath(null)

		SimpleAppDeployer deployer = getAppDeployer()

		// Loading roles in two phases breaks the preview feature, so it's disabled
		DeployRolesCommand deployRolesCommand = deployer.getCommandOfType(DeployRolesCommand.class)
		if (deployRolesCommand != null) {
			deployRolesCommand.setDeployRolesInTwoPhases(false)
		}

		PreviewInterceptor interceptor = new PreviewInterceptor(getManageClient())
		getManageClient().getRestTemplate().getInterceptors().add(interceptor)
		getManageClient().getRestTemplate().setErrorHandler(interceptor)
		if (getManageClient().getRestTemplate() != getManageClient().getSecurityUserRestTemplate()) {
			getManageClient().getSecurityUserRestTemplate().getInterceptors().add(interceptor)
			getManageClient().getSecurityUserRestTemplate().setErrorHandler(interceptor)
		}

		super.deployApp()

		println "\nPREVIEW OF DEPLOYMENT:\n"
		println ObjectMapperFactory.getObjectMapper().writeValueAsString(interceptor.getResults())
	}

}
