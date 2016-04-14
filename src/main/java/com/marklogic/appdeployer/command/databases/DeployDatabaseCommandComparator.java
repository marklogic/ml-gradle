package com.marklogic.appdeployer.command.databases;

import java.util.Comparator;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.client.helper.LoggingObject;

/**
 * Used for sorting instances of DeployDatabaseCommand so that databases are created in a correct order - i.e. an order
 * in which we don't get errors from databases being created before their dependent databases are created.
 */
public class DeployDatabaseCommandComparator extends LoggingObject implements Comparator<DeployDatabaseCommand> {

    private CommandContext context;
    private boolean reverseOrder = false;

    public DeployDatabaseCommandComparator(CommandContext context, boolean reverseOrder) {
        this.context = context;
        this.reverseOrder = reverseOrder;
    }

    @Override
    public int compare(DeployDatabaseCommand o1, DeployDatabaseCommand o2) {
        String p1 = o1.buildPayload(context);
        String p2 = o2.buildPayload(context);
        if (p1 == null || p2 == null) {
            return 0;
        }

        boolean b1 = payloadDependsOnOtherDatabase(p1);
        boolean b2 = payloadDependsOnOtherDatabase(p2);
        if (b1 && !b2) {
            return reverseOrder ? -1 : 1;
        }
        if (b2 && !b1) {
            return reverseOrder ? 1 : -1;
        }
        return 0;
    }

    /**
     * If the payload has a triggers-database or schemas-database, we consider it to depend on some other database. We
     * don't check for a security database yet, as it's very rare to use a custom security database.
     * 
     * @param payload
     * @return
     */
    protected boolean payloadDependsOnOtherDatabase(String payload) {
        return payload.contains("triggers-database") || payload.contains("schema-database");
    }
}
