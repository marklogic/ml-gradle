package com.rjrudin.marklogic.appdeployer.command.admin;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;

public class SetSslFipsEnabledCommand extends AbstractCommand {

    private boolean sslFipsEnabled = false;

    public SetSslFipsEnabledCommand(boolean sslFipsEnabled) {
        this.sslFipsEnabled = sslFipsEnabled;
    }

    @Override
    public void execute(CommandContext context) {
        context.getAdminManager().setSslFipsEnabled(sslFipsEnabled);
    }

    public boolean isSslFipsEnabled() {
        return sslFipsEnabled;
    }

    public void setSslFipsEnabled(boolean sslFipsEnabled) {
        this.sslFipsEnabled = sslFipsEnabled;
    }

}
