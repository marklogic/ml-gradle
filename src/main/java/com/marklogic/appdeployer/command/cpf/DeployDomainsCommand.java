package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.DomainManager;

public class DeployDomainsCommand extends AbstractCpfResourceCommand {

    public DeployDomainsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_DOMAINS);
    }

    @Override
    protected String getCpfDirectoryName() {
        return "domains";
    }

    @Override
    protected AbstractCpfResourceManager getResourceManager(CommandContext context) {
        return new DomainManager(context.getManageClient());
    }

}
