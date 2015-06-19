package com.marklogic.appdeployer.command.cpf;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.rest.mgmt.cpf.AbstractCpfResourceManager;
import com.marklogic.rest.mgmt.cpf.CpfConfigManager;

public class CreateCpfConfigsCommand extends AbstractCpfResourceCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_CPF_CONFIGS_ORDER;
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
