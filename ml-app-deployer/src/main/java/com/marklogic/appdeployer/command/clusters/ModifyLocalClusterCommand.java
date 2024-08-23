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
package com.marklogic.appdeployer.command.clusters;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.clusters.ClusterManager;

import java.io.File;

/**
 * Looks for a file with a name starting with "local-cluster" (e.g. "local-cluster.json" or "local-cluster.xml") in the
 * "clusters" directory. The cluster endpoints - https://docs.marklogic.com/REST/management/clusters - offer a lot more
 * functionality; this command is just for the https://docs.marklogic.com/REST/PUT/manage/v2/properties endpoint.
 */
public class ModifyLocalClusterCommand extends AbstractCommand {

	public ModifyLocalClusterCommand() {
		setExecuteSortOrder(SortOrderConstants.MODIFY_LOCAL_CLUSTER);
	}

	@Override
	public void execute(CommandContext context) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File clustersDir = configDir.getClustersDir();
			if (clustersDir != null && clustersDir.exists()) {
				for (File f : clustersDir.listFiles()) {
					if (f.isFile() && f.getName().startsWith("local-cluster")) {
						String payload = copyFileToString(f, context);
						new ClusterManager(context.getManageClient()).modifyLocalCluster(payload, context.getAdminManager());
					}
				}
			} else {
				logResourceDirectoryNotFound(clustersDir);
			}
		}
	}

}
