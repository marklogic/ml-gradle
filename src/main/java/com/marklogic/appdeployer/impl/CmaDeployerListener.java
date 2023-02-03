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
package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.api.configuration.Configurations;

import java.util.List;

/**
 * Has knowledge of the list of commands and when a combined CMA request should be submitted. As of 3.15.0, a request
 * should be submitted when any of the following are true:
 *
 * <ol>
 * <li>Users were just deployed, meaning that a combined request of privileges, roles, protected paths,
 * query rolesets, and users should be submitted.</li>
 * <li>No commands remain to be executed. In this case, need to check for a pending combined request that should be submitted. This can easily
 * happen in e.g. an ml-gradle context when running a task like mlDeployPrivileges.
 * </li>
 * </ol>
 * <p>
 * Unfortunately, "other" servers can't yet be included in the combined request with databases and forests. That's
 * because REST API servers are created before "other" servers. If REST API servers are created before databases are
 * created, then content and modules databases will be created with forests that don't match what a user intends.
 */
public class CmaDeployerListener extends DeployerListenerSupport {

	@Override
	public void afterCommandExecuted(Command command, DeploymentContext context, List<Command> remainingCommands) {
		if (combinedRequestShouldBeSubmitted(command, remainingCommands)) {
			CommandContext commandContext = context.getCommandContext();

			Configurations configs = commandContext.getCombinedCmaRequest();
			if (configs != null) {
				commandContext.removeCombinedCmaRequest();
				if (configs.hasResources()) {
					logger.info("Submitting combined CMA request");
					configs.submit(commandContext.getManageClient());
				}
			}
		}
	}

	protected boolean combinedRequestShouldBeSubmitted(Command command, List<Command> remainingCommands) {
		if (command instanceof DeployUsersCommand) {
			return true;
		}

		if (remainingCommands.isEmpty()) {
			return true;
		}

		/**
		 * At least for many ml-app-deployer tests, a small subset of commands are used. To ensure a combined request
		 * is submitted, we look at the next command to see if it executes after users are deployed, and if so, then
		 * the request is submitted.
		 */
		Command nextCommand = remainingCommands.get(0);
		return nextCommand.getExecuteSortOrder() >= SortOrderConstants.DEPLOY_USERS;
	}
}
