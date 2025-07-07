/*
 * Copyright © 2025 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.databases.DatabasePlan;
import com.marklogic.appdeployer.command.databases.DeployDatabaseCommand;
import com.marklogic.appdeployer.command.databases.DeployOtherDatabasesCommand;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.forest.Forest;

import java.util.ArrayList;
import java.util.List;

/**
 * Intended to be the central interface for "Give me a database name and a set of inputs, and I'll give you back a list
 * of forests with replicas that should be created for that database". We'll sort out the naming of this and the
 * related classes later.
 */
public class ForestPlanner {

    private final ManageClient manageClient;

    public ForestPlanner(ManageClient manageClient) {
        this.manageClient = manageClient;
    }

    public List<Forest> previewForestPlan(String database, AppConfig appConfig) {
        // We unfortunately still need a CommandContext here, even though this is just for previewing forests. The
        // classes that this depends on would need to be modified first to only require an AppConfig and/or ManageClient.
        final CommandContext context = new CommandContext(appConfig, this.manageClient, null);

        List<DatabasePlan> plans = new DeployOtherDatabasesCommand().buildDatabasePlans(context);
        DeployDatabaseCommand dbCommand = null;
        for (DatabasePlan plan : plans) {
            if (plan.getDatabaseName().equals(database)) {
                dbCommand = plan.getDeployDatabaseCommand();
                break;
            }
        }
        if (dbCommand == null) {
            throw new IllegalArgumentException("Did not find any database plan with a database name of: " + database);
        }

        DeployForestsCommand deployForestsCommand = dbCommand.buildDeployForestsCommand(database, context);
        return deployForestsCommand != null ?
                deployForestsCommand.buildForests(context, true) :
                new ArrayList<>();
    }
}
