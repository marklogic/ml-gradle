package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.hosts.HostManager;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This command is for a simple use case where all the forests created for a database have the same structure,
 * but possibly exist on different forests. For more precise control over how forests are created, please see
 * DeployCustomForestsCommand.
 *
 * Doesn't yet support deleting forests - currently assumed that this will be done by deleting a database.
 */
public class DeployForestsCommand extends AbstractCommand {

    public static final String DEFAULT_FOREST_PAYLOAD = "{\"forest-name\": \"%%FOREST_NAME%%\", \"host\": \"%%FOREST_HOST%%\", "
            + "\"database\": \"%%FOREST_DATABASE%%\"}";

    private int forestsPerHost = 1;
    private String databaseName;
    private String forestFilename;
    private String forestPayload;
    private boolean createForestsOnEachHost = true;

    public DeployForestsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_FORESTS);
    }

    /**
     * Contrary to other commands that blindly process each file in a directory, this command first looks for a specific
     * file, as defined by the forestFilename attribute. If that file is found, then its contents are used as the
     * payload for creating forests. Otherwise, if the forestPayload attribute has been set, then its contents are used
     * as the payload. If neither is true, then no forests are created.
     */
    @Override
    public void execute(CommandContext context) {
        String payload = null;
        if (forestFilename != null) {
            File dir = context.getAppConfig().getConfigDir().getForestsDir();
            if (dir.exists()) {
                File f = new File(dir, forestFilename);
                if (f.exists()) {
                    payload = copyFileToString(f);
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
            createForests(payload, context);
        }
    }

    /**
     * This command manages a couple of its own tokens, as it's expected that the host and forest name should be
     * dynamically generated based on what hosts exist and how many forests should be created on each host.
     */
    protected void createForests(String originalPayload, CommandContext context) {
        ForestManager mgr = new ForestManager(context.getManageClient());
        AppConfig appConfig = context.getAppConfig();

        // Find out which hosts to create forests on
        List<String> hostNames = new HostManager(context.getManageClient()).getHostNames();
        hostNames = determineHostNamesForForest(context, hostNames);

        // Find out how many forests exist already
        int countOfExistingForests = new DatabaseManager(context.getManageClient()).getPrimaryForestIds(getForestDatabaseName(appConfig)).size();
        int desiredNumberOfForests = hostNames.size() * forestsPerHost;

        // Loop over the number of forests to create, starting with count + 1, and iterating over the hosts
        for (int i = countOfExistingForests + 1; i <= desiredNumberOfForests;) {
            for (String hostName : hostNames) {
                if (i <= desiredNumberOfForests) {
                    String payload = payloadTokenReplacer.replaceTokens(originalPayload, appConfig, false);
                    payload = payload.replace("%%FOREST_HOST%%", hostName);
                    String forestName = getForestName(appConfig, i);
                    payload = payload.replace("%%FOREST_NAME%%", forestName);
                    payload = payload.replace("%%FOREST_DATABASE%%", getForestDatabaseName(appConfig));
                    logger.info(format("Creating forest %s on host %s", forestName, hostName));
                    mgr.save(payload);
                }
                i++;
            }
        }
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

	    Map<String, Set<String>> databaseHosts = context.getAppConfig().getDatabaseHosts();
	    if (databaseHosts != null) {
	    	Set<String> selectedHostNames = databaseHosts.get(this.databaseName);
	    	if (selectedHostNames != null) {
	    		List<String> newHostNames = new ArrayList<>();
	    		for (String name : selectedHostNames) {
	    			if (hostNames.contains(name)) {
	    				newHostNames.add(name);
				    } else {
	    				logger.warn(format("Host '%s' for database '%s' is not recognized, ignoring", name, this.databaseName));
				    }
			    }
			    return newHostNames;
		    }
	    }

	    return hostNames;
    }

    protected String getForestName(AppConfig appConfig, int forestNumber) {
        return databaseName + "-" + forestNumber;
    }

    protected String getForestDatabaseName(AppConfig appConfig) {
        return databaseName;
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

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
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
}
