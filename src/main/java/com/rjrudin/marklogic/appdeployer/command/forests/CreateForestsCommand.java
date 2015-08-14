package com.rjrudin.marklogic.appdeployer.command.forests;

import java.io.File;

import com.rjrudin.marklogic.appdeployer.AppConfig;
import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

/**
 * Doesn't yet support deleting forests - currently assumed that this will be done by deleting a database.
 */
public class CreateForestsCommand extends AbstractCommand {

    private int forestsPerHost = 1;
    private String databaseName;
    private String forestFilename;

    private boolean createTestForests = false;

    public CreateForestsCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_FORESTS);
    }

    @Override
    public void execute(CommandContext context) {
        File dir = new File(context.getAppConfig().getConfigDir().getBaseDir(), "forests");
        if (dir.exists()) {
            File f = new File(dir, forestFilename);
            if (f.exists()) {
                createForests(f, new ForestManager(context.getManageClient()), context);
            }
        }
    }

    /**
     * This command manages a couple of its own tokens, as it's expected that the host and forest name should be
     * dynamically generated based on what hosts exist and how many forests should be created on each host.
     * 
     * @param f
     * @param mgr
     * @param context
     */
    protected void createForests(File f, ForestManager mgr, CommandContext context) {
        String originalPayload = copyFileToString(f);
        AppConfig appConfig = context.getAppConfig();
        for (String hostName : new HostManager(context.getManageClient()).getHostNames()) {
            for (int i = 1; i <= forestsPerHost; i++) {
                String payload = tokenReplacer.replaceTokens(originalPayload, appConfig, false);
                payload = payload.replace("%%FOREST_HOST%%", hostName);
                payload = payload.replace("%%FOREST_NAME%%", getForestName(appConfig, i, false));
                payload = payload.replace("%%FOREST_DATABASE%%", getForestDatabaseName(appConfig, false));
                mgr.save(payload);
            }
        }

        if (createTestForests) {
            for (String hostName : new HostManager(context.getManageClient()).getHostNames()) {
                for (int i = 1; i <= forestsPerHost; i++) {
                    String payload = tokenReplacer.replaceTokens(originalPayload, appConfig, false);
                    payload = payload.replace("%%FOREST_HOST%%", hostName);
                    payload = payload.replace("%%FOREST_NAME%%", getForestName(appConfig, i, true));
                    payload = payload.replace("%%FOREST_DATABASE%%", getForestDatabaseName(appConfig, true));
                    mgr.save(payload);
                }
            }
        }
    }

    protected String getForestName(AppConfig appConfig, int forestNumber, boolean isTestDatabase) {
        return databaseName + "-" + forestNumber;
    }

    protected String getForestDatabaseName(AppConfig appConfig, boolean isTestDatabase) {
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

    public boolean isCreateTestForests() {
        return createTestForests;
    }

    public void setCreateTestForests(boolean createTestForests) {
        this.createTestForests = createTestForests;
    }
}