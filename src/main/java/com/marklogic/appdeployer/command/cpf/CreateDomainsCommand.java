package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.cpf.AbstractCpfResourceManager;
import com.marklogic.rest.mgmt.cpf.DomainManager;

public class CreateDomainsCommand extends AbstractCpfResourceCommand {

    public CreateDomainsCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_DOMAINS);
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
