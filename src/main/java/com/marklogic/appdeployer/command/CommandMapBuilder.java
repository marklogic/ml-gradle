package com.marklogic.appdeployer.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.marklogic.appdeployer.command.databases.DeployContentDatabasesCommand;
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
import com.marklogic.appdeployer.command.restapis.DeployRestApiServersCommand;
import com.marklogic.appdeployer.command.schemas.LoadSchemasCommand;
import com.marklogic.appdeployer.command.security.DeployAmpsCommand;
import com.marklogic.appdeployer.command.security.DeployCertificateAuthoritiesCommand;
import com.marklogic.appdeployer.command.security.DeployCertificateTemplatesCommand;
import com.marklogic.appdeployer.command.security.InsertCertificateHostsTemplateCommand;
import com.marklogic.appdeployer.command.security.DeployExternalSecurityCommand;
import com.marklogic.appdeployer.command.security.DeployPrivilegesCommand;
import com.marklogic.appdeployer.command.security.DeployProtectedCollectionsCommand;
import com.marklogic.appdeployer.command.security.DeployProtectedPathsCommand;
import com.marklogic.appdeployer.command.security.DeployQueryRolesetsCommand;
import com.marklogic.appdeployer.command.security.DeployRolesCommand;
import com.marklogic.appdeployer.command.security.DeployUsersCommand;
import com.marklogic.appdeployer.command.tasks.DeployScheduledTasksCommand;
import com.marklogic.appdeployer.command.taskservers.UpdateTaskServerCommand;
import com.marklogic.appdeployer.command.temporal.DeployTemporalAxesCommand;
import com.marklogic.appdeployer.command.temporal.DeployTemporalCollectionsCommand;
import com.marklogic.appdeployer.command.temporal.DeployTemporalCollectionsLSQTCommand;
import com.marklogic.appdeployer.command.triggers.DeployTriggersCommand;
import com.marklogic.appdeployer.command.viewschemas.DeployViewSchemasCommand;

/**
 * The intent of this class is to construct a map of commonly used commands that can used in a variety of contexts - i.e.
 * ml-gradle or the Data Hub Framework - thus preventing those clients from having to duplicate this code.
 *
 * A map is returned so that the commands can be grouped into lists, which is convenient for e.g. ml-gradle tasks that
 * want to execute all of the commands for a particular resource or set of resources - e.g. mlSecurityCommands for
 * invoking all commands pertaining to security resources.
 */
public class CommandMapBuilder {

	public Map<String, List<Command>> buildCommandMap() {
		Map<String, List<Command>> map = new HashMap<>();

		// Security
		List<Command> securityCommands = new ArrayList<Command>();
		securityCommands.add(new DeployRolesCommand());
		securityCommands.add(new DeployUsersCommand());
		securityCommands.add(new DeployAmpsCommand());
		securityCommands.add(new DeployCertificateTemplatesCommand());
		securityCommands.add(new DeployCertificateAuthoritiesCommand());
		securityCommands.add(new InsertCertificateHostsTemplateCommand());
		securityCommands.add(new DeployExternalSecurityCommand());
		securityCommands.add(new DeployPrivilegesCommand());
		securityCommands.add(new DeployProtectedCollectionsCommand());
		securityCommands.add(new DeployProtectedPathsCommand());
		securityCommands.add(new DeployQueryRolesetsCommand());
		map.put("mlSecurityCommands", securityCommands);

		// Cluster
		List<Command> clusterCommands = new ArrayList<Command>();
		clusterCommands.add(new ModifyLocalClusterCommand());
		map.put("mlClusterCommands", clusterCommands);

		// Configurations
		List<Command> configurationCommands = new ArrayList<>();
		configurationCommands.add(new DeployConfigurationsCommand());
		map.put("mlConfigurationCommands", configurationCommands);

		// Databases
		List<Command> dbCommands = new ArrayList<Command>();
		dbCommands.add(new DeployContentDatabasesCommand());
		dbCommands.add(new DeployOtherDatabasesCommand());
		map.put("mlDatabaseCommands", dbCommands);

		// Schemas
		List<Command> schemaCommands = new ArrayList<>();
		schemaCommands.add(new LoadSchemasCommand());
		map.put("mlSchemaCommands", schemaCommands);

		// REST API instance creation
		List<Command> restApiCommands = new ArrayList<>();
		restApiCommands.add(new DeployRestApiServersCommand());
		map.put("mlRestApiCommands", restApiCommands);

		// App servers
		List<Command> serverCommands = new ArrayList<>();
		serverCommands.add(new DeployOtherServersCommand());
		serverCommands.add(new UpdateRestApiServersCommand());
		map.put("mlServerCommands", serverCommands);

		// Modules
		List<Command> moduleCommands = new ArrayList<>();
		moduleCommands.add(new LoadModulesCommand());
		moduleCommands.add(new DeleteTestModulesCommand());
		map.put("mlModuleCommands", moduleCommands);

		// Alerting
		List<Command> alertCommands = new ArrayList<Command>();
		alertCommands.add(new DeployAlertConfigsCommand());
		alertCommands.add(new DeployAlertActionsCommand());
		alertCommands.add(new DeployAlertRulesCommand());
		map.put("mlAlertCommands", alertCommands);

		// CPF
		List<Command> cpfCommands = new ArrayList<Command>();
		cpfCommands.add(new DeployCpfConfigsCommand());
		cpfCommands.add(new DeployDomainsCommand());
		cpfCommands.add(new DeployPipelinesCommand());
		map.put("mlCpfCommands", cpfCommands);

		// Flexrep
		List<Command> flexrepCommands = new ArrayList<Command>();
		flexrepCommands.add(new DeployConfigsCommand());
		flexrepCommands.add(new DeployTargetsCommand());
		flexrepCommands.add(new DeployFlexrepCommand());
		map.put("mlFlexrepCommands", flexrepCommands);

		// Groups
		List<Command> groupCommands = new ArrayList<Command>();
		groupCommands.add(new DeployGroupsCommand());
		map.put("mlGroupCommands", groupCommands);

		List<Command> mimetypeCommands = new ArrayList<Command>();
		mimetypeCommands.add(new DeployMimetypesCommand());
		map.put("mlMimetypeCommands", mimetypeCommands);

		// Hosts
		List<Command> hostCommands = new ArrayList<Command>();
		hostCommands.add(new AssignHostsToGroupsCommand());
		map.put("mlAssignHostsToGroups", hostCommands);

		// Forests
		List<Command> forestCommands = new ArrayList<Command>();
		forestCommands.add(new DeployCustomForestsCommand());
		map.put("mlForestCommands", forestCommands);

		// Forest replicas
		List<Command> replicaCommands = new ArrayList<Command>();
		replicaCommands.add(new ConfigureForestReplicasCommand());
		map.put("mlForestReplicaCommands", replicaCommands);

		// Tasks
		List<Command> taskCommands = new ArrayList<Command>();
		taskCommands.add(new DeployScheduledTasksCommand());
		taskCommands.add(new UpdateTaskServerCommand());
		map.put("mlTaskCommands", taskCommands);

		// Temporal
		List<Command> temporalCommands = new ArrayList<>();
		temporalCommands.add(new DeployTemporalAxesCommand());
		temporalCommands.add(new DeployTemporalCollectionsCommand());
		temporalCommands.add(new DeployTemporalCollectionsLSQTCommand());
		map.put("mlTemporalCommands", temporalCommands);

		// Triggers
		List<Command> triggerCommands = new ArrayList<Command>();
		triggerCommands.add(new DeployTriggersCommand());
		map.put("mlTriggerCommands", triggerCommands);


		// SQL Views
		List<Command> viewCommands = new ArrayList<Command>();
		viewCommands.add(new DeployViewSchemasCommand());
		map.put("mlViewCommands", viewCommands);

		return map;
	}
}
