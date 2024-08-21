/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
