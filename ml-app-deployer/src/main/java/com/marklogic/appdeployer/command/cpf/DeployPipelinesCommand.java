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
package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.PipelineManager;

import java.io.File;

public class DeployPipelinesCommand extends AbstractCpfResourceCommand {

	public DeployPipelinesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PIPELINES);
	}

	@Override
	protected File getCpfResourceDir(ConfigDir configDir) {
		return configDir.getPipelinesDir();
	}

	@Override
	protected AbstractCpfResourceManager getResourceManager(CommandContext context, String databaseIdOrName) {
		return new PipelineManager(context.getManageClient(), databaseIdOrName);
	}
}
