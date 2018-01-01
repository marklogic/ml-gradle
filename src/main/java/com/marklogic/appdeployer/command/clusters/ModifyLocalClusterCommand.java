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
