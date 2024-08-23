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

/**
 * Version 2.+ of ml-app-deployer requires at least version 8.x of MarkLogic. There are features in ml-app-deployer 2.x
 * - such as support for alerts and triggers - that require a certain version of 8, but we at least want to make sure
 * that no one tries to run this against ML 7 or an older version.
 */
public class RequireAtLeastMl8Command extends AbstractCommand {

    @Override
    public void execute(CommandContext context) {
        int major = 0;
        try {
            String version = context.getAdminManager().getServerVersion();
            if (logger.isInfoEnabled()) {
                logger.info("Verifying MarkLogic version is at least 8 or higher; version: " + version);
            }
            major = Integer.parseInt(version.split("\\.")[0]);
        } catch (Exception e) {
            logger.warn("Unable to verify MarkLogic version is 8 or higher, will continue with deployment; error: "
                    + e.getMessage());
            major = 8;
        }
        if (major < 8) {
            throw new RuntimeException("Only MarkLogic versions 8 and higher are supported");
        }
    }

}
