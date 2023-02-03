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
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.command.alert.DeployAlertActionsCommand;
import com.marklogic.appdeployer.command.alert.DeployAlertConfigsCommand;
import com.marklogic.appdeployer.command.alert.DeployAlertRulesCommand;
import com.marklogic.appdeployer.command.appservers.DeployOtherServersCommand;
import com.marklogic.appdeployer.command.appservers.UpdateRestApiServersCommand;
import com.marklogic.appdeployer.command.clusters.ModifyLocalClusterCommand;
import com.marklogic.appdeployer.command.cma.DeployConfigurationsCommand;
import com.marklogic.appdeployer.command.cpf.DeployCpfConfigsCommand;
import com.marklogic.appdeployer.command.cpf.DeployDomainsCommand;
import com.marklogic.appdeployer.command.cpf.DeployPipelinesCommand;
import com.marklogic.appdeployer.command.data.LoadDataCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.appdeployer.command.flexrep.DeployConfigsCommand;
import com.marklogic.appdeployer.command.flexrep.DeployFlexrepCommand;
import com.marklogic.appdeployer.command.flexrep.DeployTargetsCommand;
import com.marklogic.appdeployer.command.forests.ConfigureForestReplicasCommand;
import com.marklogic.appdeployer.command.forests.DeployCustomForestsCommand;
import com.marklogic.appdeployer.command.groups.DeployGroupsCommand;
import com.marklogic.appdeployer.command.hosts.AssignHostsToGroupsCommand;
import com.marklogic.appdeployer.command.mimetypes.DeployMimetypesCommand;
import com.marklogic.appdeployer.command.modules.DeleteTestModulesCommand;
import com.marklogic.appdeployer.command.modules.LoadModulesCommand;
import com.marklogic.appdeployer.command.plugins.InstallPluginsCommand;
import com.marklogic.appdeployer.command.rebalancer.DeployPartitionQueriesCommand;
import com.marklogic.appdeployer.command.rebalancer.DeployPartitionsCommand;
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.schemas.LoadSchemasCommand;
import com.marklogic.appdeployer.command.security.*;
import com.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand;
import com.marklogic.appdeployer.command.taskservers.UpdateTaskServerCommand;
import com.marklogic.appdeployer.command.temporal.DeployTemporalAxesCommand;
import com.marklogic.appdeployer.command.temporal.DeployTemporalCollectionsCommand;
import com.marklogic.appdeployer.command.temporal.DeployTemporalCollectionsLSQTCommand;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.appdeployer.command.viewschemas.DeployViewSchemasCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The intent of this class is to construct a map of commonly used commands that can used in a variety of contexts - i.e.
 * ml-gradle or the Data Hub Framework - thus preventing those clients from having to duplicate this code.
 * <p>
 * A map is returned so that the commands can be grouped into lists, which is convenient for e.g. ml-gradle tasks that
 * want to execute all of the commands for a particular resource or set of resources - e.g. mlSecurityCommands for
 * invoking all commands pertaining to security resources.
 */
public class CommandMapBuilder {

	/**
	 * @return a map of all commands relevant to deploying an application, including those that can write data to a
	 * database and thus should only be run against a cluster that is not a replica cluster
	 */
	public Map<String, List<Command>> buildCommandMap() {
		Map<String, List<Command>> map = new HashMap<>();
		addCommandsThatDoNotWriteToDatabases(map);
		addCommandsThatWriteToDatabases(map);
		return map;
	}

	/**
	 * For deploying to a replica cluster, care must be taken not to run any command that will write to a database that
	 * is likely to have database replication configured for it. This then returns a map of commands that are known not
	 * to write any data to databases. The security commands are an exception though, as it's typical for the Security
	 * database to not have database replication configured for it. If this is not the case for a user, the user can
	 * still use this method and simply remove the commands that write to the Security database.
	 *
	 * @return
	 */
	public Map<String, List<Command>> buildCommandMapForReplicaCluster() {
		Map<String, List<Command>> map = new HashMap<>();
		addCommandsThatDoNotWriteToDatabases(map);
		return map;
	}

	/**
	 * Same as buildCommandMapForReplicaCluster, but returns a list of all the commands.
	 *
	 * @return
	 */
	public List<Command> getCommandsForReplicaCluster() {
		return buildCommandMapForReplicaCluster()
			.values()
			.stream().reduce(new ArrayList<>(), (a, b) -> {
				a.addAll(b);
				return a;
			});
	}

	private void addCommandsThatDoNotWriteToDatabases(Map<String, List<Command>> map) {
		List<Command> clusterCommands = new ArrayList<>();
		clusterCommands.add(new ModifyLocalClusterCommand());
		map.put("mlClusterCommands", clusterCommands);

		List<Command> configurationCommands = new ArrayList<>();
		configurationCommands.add(new DeployConfigurationsCommand());
		map.put("mlConfigurationCommands", configurationCommands);

		List<Command> dbCommands = new ArrayList<>();
		dbCommands.add(new DeployOtherDatabasesCommand());
		map.put("mlDatabaseCommands", dbCommands);

		List<Command> forestCommands = new ArrayList<>();
		forestCommands.add(new DeployCustomForestsCommand());
		map.put("mlForestCommands", forestCommands);

		List<Command> replicaCommands = new ArrayList<>();
		replicaCommands.add(new ConfigureForestReplicasCommand());
		map.put("mlForestReplicaCommands", replicaCommands);

		List<Command> groupCommands = new ArrayList<>();
		groupCommands.add(new DeployGroupsCommand());
		map.put("mlGroupCommands", groupCommands);

		List<Command> hostCommands = new ArrayList<>();
		hostCommands.add(new AssignHostsToGroupsCommand());
		map.put("mlHostCommands", hostCommands);

		List<Command> mimetypeCommands = new ArrayList<>();
		mimetypeCommands.add(new DeployMimetypesCommand());
		map.put("mlMimetypeCommands", mimetypeCommands);

		List<Command> pluginCommands = new ArrayList<>();
		pluginCommands.add(new InstallPluginsCommand());
		map.put("mlPluginCommands", pluginCommands);

		List<Command> rebalancerCommands = new ArrayList<>();
		rebalancerCommands.add(new DeployPartitionsCommand());
		rebalancerCommands.add(new DeployPartitionQueriesCommand());
		map.put("mlRebalancerCommands", rebalancerCommands);

		List<Command> restApiCommands = new ArrayList<>();
		restApiCommands.add(new DeployRestApiServersCommand());
		map.put("mlRestApiCommands", restApiCommands);

		List<Command> securityCommands = new ArrayList<>();
		securityCommands.add(new DeployRolesCommand());
		securityCommands.add(new DeployUsersCommand());
		securityCommands.add(new DeployAmpsCommand());
		securityCommands.add(new DeployCertificateTemplatesCommand());
		securityCommands.add(new DeployCertificateAuthoritiesCommand());
		securityCommands.add(new InsertCertificateHostsTemplateCommand());
		securityCommands.add(new DeployExternalSecurityCommand());
		securityCommands.add(new DeploySecureCredentialsCommand());
		securityCommands.add(new DeployPrivilegesCommand());
		securityCommands.add(new DeployPrivilegeRolesCommand());
		securityCommands.add(new DeployProtectedCollectionsCommand());
		securityCommands.add(new DeployProtectedPathsCommand());
		securityCommands.add(new DeployQueryRolesetsCommand());
		map.put("mlSecurityCommands", securityCommands);

		List<Command> serverCommands = new ArrayList<>();
		serverCommands.add(new DeployOtherServersCommand());
		serverCommands.add(new UpdateRestApiServersCommand());
		map.put("mlServerCommands", serverCommands);

		List<Command> taskCommands = new ArrayList<>();
		taskCommands.add(new DeployScheduledTasksCommand());
		taskCommands.add(new UpdateTaskServerCommand());
		map.put("mlTaskCommands", taskCommands);
	}

	private void addCommandsThatWriteToDatabases(Map<String, List<Command>> map) {
		List<Command> alertCommands = new ArrayList<>();
		alertCommands.add(new DeployAlertConfigsCommand());
		alertCommands.add(new DeployAlertActionsCommand());
		alertCommands.add(new DeployAlertRulesCommand());
		map.put("mlAlertCommands", alertCommands);

		List<Command> cpfCommands = new ArrayList<>();
		cpfCommands.add(new DeployCpfConfigsCommand());
		cpfCommands.add(new DeployDomainsCommand());
		cpfCommands.add(new DeployPipelinesCommand());
		map.put("mlCpfCommands", cpfCommands);

		List<Command> dataCommands = new ArrayList<>();
		dataCommands.add(new LoadDataCommand());
		map.put("mlDataCommands", dataCommands);

		List<Command> flexrepCommands = new ArrayList<>();
		flexrepCommands.add(new DeployConfigsCommand());
		flexrepCommands.add(new DeployTargetsCommand());
		flexrepCommands.add(new DeployFlexrepCommand());
		map.put("mlFlexrepCommands", flexrepCommands);

		List<Command> moduleCommands = new ArrayList<>();
		moduleCommands.add(new LoadModulesCommand());
		moduleCommands.add(new DeleteTestModulesCommand());
		map.put("mlModuleCommands", moduleCommands);

		List<Command> schemaCommands = new ArrayList<>();
		schemaCommands.add(new LoadSchemasCommand());
		map.put("mlSchemaCommands", schemaCommands);

		List<Command> temporalCommands = new ArrayList<>();
		temporalCommands.add(new DeployTemporalAxesCommand());
		temporalCommands.add(new DeployTemporalCollectionsCommand());
		temporalCommands.add(new DeployTemporalCollectionsLSQTCommand());
		map.put("mlTemporalCommands", temporalCommands);

		List<Command> triggerCommands = new ArrayList<>();
		triggerCommands.add(new DeployTriggersCommand());
		map.put("mlTriggerCommands", triggerCommands);

		List<Command> viewCommands = new ArrayList<>();
		viewCommands.add(new DeployViewSchemasCommand());
		map.put("mlViewCommands", viewCommands);
	}
}
