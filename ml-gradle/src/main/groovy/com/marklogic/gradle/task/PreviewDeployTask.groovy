/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.modules.LoadModulesCommand
import com.marklogic.appdeployer.command.schemas.LoadSchemasCommand
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
		modifyAppConfigBeforePreview()
		modifyAppDeployerBeforePreview()

		PreviewInterceptor interceptor = configurePreviewInterceptor()

		super.deployApp()

		println "\nPREVIEW OF DEPLOYMENT:\n"
		println ObjectMapperFactory.getObjectMapper().writeValueAsString(interceptor.getResults())
	}

	void modifyAppConfigBeforePreview() {
		// Disable loading of any modules
		getAppConfig().setModulePaths(new ArrayList<String>())

		// Disable loading of schemas
		// Database-specific schema paths are handled by removing instances of LoadSchemasCommand
		getAppConfig().setSchemaPaths(null)

		// Disable loading of data
		getAppConfig().getDataConfig().setDataPaths(null)

		// Disable installing of plugins
		getAppConfig().getPluginConfig().setPluginPaths(null)
	}

	void modifyAppDeployerBeforePreview() {
		AppDeployer deployer = getAppDeployer()

		if (deployer instanceof SimpleAppDeployer) {
			SimpleAppDeployer simpleAppDeployer = (SimpleAppDeployer) deployer

			List<Command> newCommands = new ArrayList<>()
			for (Command c : simpleAppDeployer.getCommands()) {
				if (c instanceof LoadSchemasCommand || c instanceof LoadModulesCommand) {
					// Don't include these; no need to load schemas or modules during a preview
				} else {
					newCommands.add(c)
				}
			}
			simpleAppDeployer.setCommands(newCommands)
		}
	}

	PreviewInterceptor configurePreviewInterceptor() {
		PreviewInterceptor interceptor = new PreviewInterceptor(getManageClient())
		getManageClient().getRestTemplate().getInterceptors().add(interceptor)
		getManageClient().getRestTemplate().setErrorHandler(interceptor)
		if (getManageClient().getRestTemplate() != getManageClient().getSecurityUserRestTemplate()) {
			getManageClient().getSecurityUserRestTemplate().getInterceptors().add(interceptor)
			getManageClient().getSecurityUserRestTemplate().setErrorHandler(interceptor)
		}
		return interceptor
	}
}
