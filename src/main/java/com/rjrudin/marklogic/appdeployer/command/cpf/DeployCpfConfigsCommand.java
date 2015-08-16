package com.rjrudin.marklogic.appdeployer.command.cpf;

import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.cpf.AbstractCpfResourceManager;
import com.rjrudin.marklogic.mgmt.cpf.CpfConfigManager;

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
