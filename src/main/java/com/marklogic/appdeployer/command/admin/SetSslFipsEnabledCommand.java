package com.marklogic.appdeployer.command.admin;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;

public class SetSslFipsEnabledCommand extends AbstractCommand {

    private boolean sslFipsEnabled = false;

    public SetSslFipsEnabledCommand(boolean sslFipsEnabled) {
        this.sslFipsEnabled = sslFipsEnabled;
    }

    @Override
    public void execute(CommandContext context) {
        context.getAdminManager().setSslFipsEnabled(sslFipsEnabled, context.getAppConfig().getAppServicesPort());
    }

    public boolean isSslFipsEnabled() {
        return sslFipsEnabled;
    }

    public void setSslFipsEnabled(boolean sslFipsEnabled) {
        this.sslFipsEnabled = sslFipsEnabled;
    }

}
