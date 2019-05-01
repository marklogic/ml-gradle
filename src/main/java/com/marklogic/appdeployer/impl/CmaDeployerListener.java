package com.marklogic.appdeployer.impl;

import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.databases.DatabasePlan;
import com.marklogic.appdeployer.command.security.DeployAmpsCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.mgmt.api.configuration.Configurations;

import java.util.List;

/**
 * Has knowledge of the list of commands and when a combined CMA request should be submitted. As of 3.15.0, a request
 * should be submitted when any of the following are true:
 *
 * <ol>
 * <li>DeployUsersCommand was just executed, meaning that a combined request of privileges, roles, and users should be submitted.</li>
 * <li>DeployOtherDatabaseCommand was just executed, meaning that a combined request of databases and forests should be submitted.</li>
 * <li>No commands remain to be executed. In this case, need to check for a pending combined request that should be submitted. This can easily
 * happen in e.g. an ml-gradle context when running a task like mlDeployPrivileges or mlDeployDatabases.
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
		if (
			command instanceof DeployUsersCommand ||
				command instanceof DeployAmpsCommand ||
				remainingCommands.isEmpty()
		) {
			CommandContext commandContext = context.getCommandContext();

			Configurations configs = commandContext.getCombinedCmaRequest();
			if (configs != null) {
				commandContext.removeCombinedCmaRequest();

				if (configs.hasResources()) {
					logger.info("Submitting combined CMA request");
					configs.submit(commandContext.getManageClient());

					if (command instanceof DeployAmpsCommand || remainingCommands.isEmpty()) {
						List<DatabasePlan> databasePlans = (List<DatabasePlan>) commandContext.getContextMap().get("database-plans");
						if (databasePlans != null) {
							commandContext.getContextMap().remove("database-plans");
							databasePlans.forEach(plan -> {
								plan.getDeployDatabaseCommand().deploySubDatabases(plan.getDatabaseName(), commandContext);
							});
						}
					}
				}
			}
		}
	}
}
