package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.hosts.DefaultHostNameProvider;
import com.marklogic.mgmt.resource.hosts.HostManager;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This command constructs forests based on properties found in the AppConfig object associated with the incoming
 * CommandContext. For more precise control over how forests are created, please see DeployCustomForestsCommand.
 * <p>
 * Doesn't yet support deleting forests - currently assumes that this will be done by deleting a database.
 * </p>
 * <p>
 * This class also does not support creating replica forests - these are handled by ConfigureForestReplicasCommand.
 * </p>
 */
public class DeployForestsCommand extends AbstractCommand {

	/**
	 * This was added back in 3.8.2 to preserve backwards compatibility, as it was removed in 3.7.0.
	 */
	public static final String DEFAULT_FOREST_PAYLOAD = "{\"forest-name\": \"%%FOREST_NAME%%\", \"host\": \"%%FOREST_HOST%%\", "
		+ "\"database\": \"%%FOREST_DATABASE%%\"}";

	private int forestsPerHost = 1;
	private String databaseName;
	private String forestFilename;
	private String forestPayload;
	private boolean createForestsOnEachHost = true;
	private HostCalculator hostCalculator;

	private ForestBuilder forestBuilder = new ForestBuilder();

	/**
	 * This was added back in 3.8.2 to preserve backwards compatibility, as it was removed in 3.7.0. If you use this
	 * constructor, be sure to call setDatabaseName.
	 */
	public DeployForestsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FORESTS);
	}

	/**
	 * This is the preferred constructor to use for this class, as it requires a database name, which is required for
	 * this command to execute correctly.
	 *
	 * @param databaseName
	 */
	public DeployForestsCommand(String databaseName) {
		this();
		this.databaseName = databaseName;
	}

	/**
	 * Contrary to other commands that blindly process each file in a directory, this command first looks for a specific
	 * file, as defined by the forestFilename attribute. If that file is found, then its contents are used as a
	 * template for creating forests (note that this command will determine the host, name, and database for each forest
	 * regardless of what's in the template).
	 */
	@Override
	public void execute(CommandContext context) {
		// Replicas are currently handled by ConfigureForestReplicasCommand
		List<Forest> forests = buildForests(context, false);

		if (context.getAppConfig().getCmaConfig().isDeployForests() && !forests.isEmpty() && cmaEndpointExists(context)) {
			createForestsViaCma(context, forests);
		} else {
			createForestsViaForestEndpoint(context, forests);
		}
	}

	protected void createForestsViaCma(CommandContext context, List<Forest> forests) {
		Configuration config = new Configuration();
		forests.forEach(forest -> config.addForest(forest.toObjectNode()));
		new Configurations(config).submit(context.getManageClient());
	}

	protected void createForestsViaForestEndpoint(CommandContext context, List<Forest> forests) {
		ForestManager forestManager = new ForestManager(context.getManageClient());
		for (Forest f : forests) {
			forestManager.save(f.getJson());
		}
	}

	/**
	 * Public so that it can be reused without actually saving any of the forests.
	 *
	 * This also facilitates the creation of forests for many databases at one time. A client can call this on a set of
	 * these commands to construct a list of many forests that can be created via CMA in one request.
	 *
	 * @param context
	 * @param includeReplicas This command currently doesn't make use of this feature; it's here so that other clients
	 *                        can get a preview of the forests to be created, including replicas.
	 * @return
	 */
	public List<Forest> buildForests(CommandContext context, boolean includeReplicas) {
		String template = buildForestTemplate(context, new ForestManager(context.getManageClient()));

		List<String> hostNames = new HostManager(context.getManageClient()).getHostNames();
		hostNames = determineHostNamesForForest(context, hostNames);

		int countOfExistingForests = new DatabaseManager(context.getManageClient()).getPrimaryForestIds(this.databaseName).size();

		ForestPlan forestPlan = new ForestPlan(this.databaseName, hostNames)
			.withTemplate(template)
			.withForestsPerDataDirectory(this.forestsPerHost)
			.withExistingForestsPerDataDirectory(countOfExistingForests);

		if (includeReplicas) {
			Map<String, Integer> map = context.getAppConfig().getDatabaseNamesAndReplicaCounts();
			if (map != null) {
				int count = map.get(this.databaseName);
				if (count > 0) {
					forestPlan.withReplicaCount(count);
				}
			}
		}

		return forestBuilder.buildForests(forestPlan, context.getAppConfig());
	}

	protected String buildForestTemplate(CommandContext context, ForestManager forestManager) {
		String payload = null;
		if (forestFilename != null) {
			for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
				File dir = configDir.getForestsDir();
				if (dir.exists()) {
					File f = new File(dir, forestFilename);
					if (f.exists()) {
						payload = copyFileToString(f);
					}
				}
			}
		}

		if (payload == null && StringUtils.hasText(forestPayload)) {
			if (logger.isInfoEnabled()) {
				logger.info("Creating forests using configured payload: " + forestPayload);
			}
			payload = forestPayload;
		}

		if (payload != null) {
			return adjustPayloadBeforeSavingResource(context, null, payload);
		}

		return null;
	}

	/**
	 * @param context
	 * @param hostNames
	 * @return
	 */
	protected List<String> determineHostNamesForForest(CommandContext context, List<String> hostNames) {
		Set<String> databaseNames = context.getAppConfig().getDatabasesWithForestsOnOneHost();
		boolean onlyOnOneHost = databaseNames != null && databaseNames.contains(this.databaseName);

		if (!createForestsOnEachHost || onlyOnOneHost) {
			String first = hostNames.get(0);
			logger.info(format("Only creating forests on the first host: " + first));
			hostNames = new ArrayList<>();
			hostNames.add(first);
			return hostNames;
		}

		if (hostCalculator == null) {
			return new DefaultHostCalculator(new DefaultHostNameProvider(context.getManageClient())).calculateHostNames(this.databaseName, context);
		}

		return hostCalculator.calculateHostNames(this.databaseName, context);
	}

	public int getForestsPerHost() {
		return forestsPerHost;
	}

	public void setForestsPerHost(int forestsPerHost) {
		this.forestsPerHost = forestsPerHost;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getForestFilename() {
		return forestFilename;
	}

	public void setForestFilename(String forestFilename) {
		this.forestFilename = forestFilename;
	}

	public String getForestPayload() {
		return forestPayload;
	}

	public void setForestPayload(String forestPayload) {
		this.forestPayload = forestPayload;
	}

	public boolean isCreateForestsOnEachHost() {
		return createForestsOnEachHost;
	}

	public void setCreateForestsOnEachHost(boolean createForestsOnEachHost) {
		this.createForestsOnEachHost = createForestsOnEachHost;
	}

	public void setHostCalculator(HostCalculator hostCalculator) {
		this.hostCalculator = hostCalculator;
	}

	/**
	 * This was added back in 3.8.2 to preserve backwards compatibility, as it was removed in 3.7.0.
	 *
	 * @param databaseName
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setForestBuilder(ForestBuilder forestBuilder) {
		this.forestBuilder = forestBuilder;
	}
}
