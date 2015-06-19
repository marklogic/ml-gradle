package com.marklogic.appdeployer.command.cpf;

import java.io.File;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.rest.mgmt.cpf.PipelineManager;

public class CreatePipelinesCommand extends AbstractCommand {

    @Override
    public Integer getExecuteSortOrder() {
        return 920;
    }

    @Override
    public void execute(CommandContext context) {
        AppConfig config = context.getAppConfig();
        File dir = new File(config.getConfigDir().getCpfDir(), "pipelines");
        if (dir.exists()) {
            PipelineManager mgr = new PipelineManager(context.getManageClient());
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
