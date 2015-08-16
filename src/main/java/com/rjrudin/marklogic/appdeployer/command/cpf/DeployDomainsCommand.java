package com.rjrudin.marklogic.appdeployer.command.cpf;

import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.cpf.AbstractCpfResourceManager;
import com.rjrudin.marklogic.mgmt.cpf.DomainManager;

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
