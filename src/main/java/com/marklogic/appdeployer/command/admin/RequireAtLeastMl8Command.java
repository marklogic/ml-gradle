package com.marklogic.appdeployer.command.admin;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;

/**
 * Version 2.+ of ml-app-deployer requires at least version 8.x of MarkLogic. There are features in ml-app-deployer 2.x
 * - such as support for alerts and triggers - that require a certain version of 8, but we at least want to make sure
 * that no one tries to run this against ML 7 or an older version.
 */
public class RequireAtLeastMl8Command extends AbstractCommand {

    @Override
    public void execute(CommandContext context) {
        int major = 0;
        try {
            String version = context.getAdminManager().getServerVersion();
            if (logger.isInfoEnabled()) {
                logger.info("Verifying MarkLogic version is at least 8 or higher; version: " + version);
            }
            major = Integer.parseInt(version.split("\\.")[0]);
        } catch (Exception e) {
            logger.warn("Unable to verify MarkLogic version is 8 or higher, will continue with deployment; error: "
                    + e.getMessage());
            major = 8;
        }
        if (major < 8) {
            throw new RuntimeException("Only MarkLogic versions 8 and higher are supported");
        }
    }

}
