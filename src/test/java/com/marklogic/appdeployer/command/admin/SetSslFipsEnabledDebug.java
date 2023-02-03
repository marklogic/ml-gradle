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

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;

/**
 * TODO Convert this into a real test, adding a isSslFipsEnabled method to AdminManager.
 */
public class SetSslFipsEnabledDebug {

    public static void main(String[] args) {
        ManageConfig config = new ManageConfig("localhost", 8002, "admin", "admin");
        ManageClient manageClient = new ManageClient(config);
        AppConfig appConfig = new AppConfig();
        AdminManager adminManager = new AdminManager(new AdminConfig("localhost", "admin"));
        CommandContext context = new CommandContext(appConfig, manageClient, adminManager);

        SetSslFipsEnabledCommand command = new SetSslFipsEnabledCommand(false);
        command.execute(context);
    }
}
