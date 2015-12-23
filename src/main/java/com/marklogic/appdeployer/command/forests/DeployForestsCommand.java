package com.marklogic.appdeployer.command.forests;

import java.io.File;
import java.util.List;

import org.springframework.util.StringUtils;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.forests.ForestManager;
import com.marklogic.mgmt.hosts.HostManager;

/**
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
            File dir = new File(context.getAppConfig().getConfigDir().getBaseDir(), "forests");
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
        List<String> hostNames = new HostManager(context.getManageClient()).getHostNames();
        int size = hostNames.size();
        if (!createForestsOnEachHost) {
            logger.info(format("Only creating forests on the first host: " + hostNames.get(0)));
            size = 1;
        }
        for (int i = 0; i < size; i++) {
            String hostName = hostNames.get(i);
            logger.info(format("Creating forests on host %s", hostName));
            int startNumber = (i * forestsPerHost) + 1;
            int endNumber = (i + 1) * forestsPerHost;
            for (int j = startNumber; j <= endNumber; j++) {
                String payload = tokenReplacer.replaceTokens(originalPayload, appConfig, false);
                payload = payload.replace("%%FOREST_HOST%%", hostName);
                payload = payload.replace("%%FOREST_NAME%%", getForestName(appConfig, j));
                payload = payload.replace("%%FOREST_DATABASE%%", getForestDatabaseName(appConfig));
                mgr.save(payload);
            }
        }
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