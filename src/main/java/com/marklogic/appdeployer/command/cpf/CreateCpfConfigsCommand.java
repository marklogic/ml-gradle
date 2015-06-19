package com.marklogic.appdeployer.command.cpf;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.rest.mgmt.cpf.CpfConfigManager;

public class CreateCpfConfigsCommand extends AbstractCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return 910;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();
        File dir = new File(config.getConfigDir().getCpfDir(), "cpf-configs");
        if (dir.exists()) {
            CpfConfigManager mgr = new CpfConfigManager(context.getManageClient());
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith(".json")) {
                    String payload = copyFileToString(f);
                    payload = tokenReplacer.replaceTokens(payload, config, false);
                    mgr.save(config.getTriggersDatabaseName(), payload);
                }
            }
        }
    }

}
