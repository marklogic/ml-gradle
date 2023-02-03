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
package com.marklogic.client.ext.qconsole.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;

import java.io.IOException;

/**
 * Debug-style program for manually verifying how DefaultWorkspaceManager works.
 * <p>
 * To try this out locally, manually create a new workspace in qconsole and add some queries to it. Then
 * export it with this program. Then delete it and its queries from the App-Services database. Then run this
 * program to import it back in.
 */
public class DefaultWorkspaceManagerDebug {

    public static void main(String[] args) throws IOException {
        DatabaseClient client = DatabaseClientFactory.newClient("localhost", 8000, "App-Services",
	        new DatabaseClientFactory.DigestAuthContext("admin", "admin"));
        DefaultWorkspaceManager dwm = new DefaultWorkspaceManager(client);
        String user = "admin";
        final String workspaceName = "Workspace 1";
		//System.out.println(dwm.exportWorkspaces(user, workspaceName));
		System.out.println(dwm.importWorkspaces(user, workspaceName));
    }

}
