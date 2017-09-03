package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.cpf.AbstractCpfResourceManager;
import com.marklogic.mgmt.resource.cpf.CpfConfigManager;

public class DeployCpfConfigsCommand extends AbstractCpfResourceCommand {

    public DeployCpfConfigsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_CPF_CONFIGS);
    }

    @Override
    protected String getCpfDirectoryName() {
        return "cpf-configs";
    }

    @Override
    protected AbstractCpfResourceManager getResourceManager(CommandContext context) {
        return new CpfConfigManager(context.getManageClient());
    }

}
