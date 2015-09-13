package com.rjrudin.marklogic.appdeployer.command.failover;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.hosts.HostManager;

public class CreateMlAppReplicasCommand extends AbstractCommand {

    private int securityReplicasPerHost = 1;
    private int schemasReplicasPerHost = 1;
    private int appServicesReplicasPerHost = 1;
    private int metersReplicasPerHost = 1;

    public CreateMlAppReplicasCommand() {
        // As this task has no dependencies, it should be safe to execute it first
        setExecuteSortOrder(0);
    }

    @Override
    public void execute(CommandContext context) {
        HostManager hostMgr = new HostManager(context.getManageClient());
        ForestManager forestMgr = new ForestManager(context.getManageClient());

        logger.info("All host ids: " + hostMgr.getHostIds());

        // Find the host that has the default security/schemas/app-services/meters forests on it
        String hostId = forestMgr.getHostId("Security");
        logger.info("Security host: " + hostId);
    }

    public void setSecurityReplicasPerHost(int securityReplicasPerHost) {
        this.securityReplicasPerHost = securityReplicasPerHost;
    }

    public void setSchemasReplicasPerHost(int schemasReplicasPerHost) {
        this.schemasReplicasPerHost = schemasReplicasPerHost;
    }

    public void setAppServicesReplicasPerHost(int appServicesReplicasPerHost) {
        this.appServicesReplicasPerHost = appServicesReplicasPerHost;
    }

    public void setMetersReplicasPerHost(int metersReplicasPerHost) {
        this.metersReplicasPerHost = metersReplicasPerHost;
    }
}
