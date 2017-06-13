package com.marklogic.appdeployer.command.alert;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.alert.AlertRuleManager;

import java.io.File;

public class DeployAlertRulesCommand extends AbstractCommand {

    private String databaseIdOrName;
    private String rulesDirectorySuffix = "-rules";
    private PayloadParser payloadParser = new PayloadParser();

    public DeployAlertRulesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_ALERT_RULES);
    }

    @Override
    public void execute(CommandContext context) {
        File configDir = new File(context.getAppConfig().getConfigDir().getAlertDir(), "configs");
        if (configDir != null && configDir.exists()) {
            for (File f : configDir.listFiles()) {
                if (f.isDirectory() && f.getName().endsWith(rulesDirectorySuffix)) {
                    deployRulesInDirectory(f, context);
                }
            }
        }
    }

    protected void deployRulesInDirectory(File dir, CommandContext context) {
        String dbName = databaseIdOrName != null ? databaseIdOrName : context.getAppConfig().getContentDatabaseName();
        String configUri = extractConfigUriFromDirectory(dir);

        if (logger.isInfoEnabled()) {
            logger.info(format("Deploying alert rules with config URI '%s' in directory: %s", configUri,
                    dir.getAbsolutePath()));
        }

        /**
         * We have to build an AlertRuleManager each time, as we don't know the action name until we load the file and
         * parse its contents.
         */
        for (File f : listFilesInDirectory(dir)) {
            String payload = copyFileToString(f, context);
            String actionName = payloadParser.getPayloadFieldValue(payload, "action-name");
            AlertRuleManager mgr = new AlertRuleManager(context.getManageClient(), dbName, configUri, actionName);
            saveResource(mgr, context, f);
        }
    }

    protected String extractConfigUriFromDirectory(File dir) {
        String name = dir.getName();
        return name.substring(0, name.length() - rulesDirectorySuffix.length());
    }

    public void setDatabaseIdOrName(String databaseIdOrName) {
        this.databaseIdOrName = databaseIdOrName;
    }

    public void setRulesDirectorySuffix(String targetDirectorySuffix) {
        this.rulesDirectorySuffix = targetDirectorySuffix;
    }

    public void setPayloadParser(PayloadParser payloadParser) {
        this.payloadParser = payloadParser;
    }
}
