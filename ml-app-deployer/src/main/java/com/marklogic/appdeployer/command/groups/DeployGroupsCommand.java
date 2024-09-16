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
package com.marklogic.appdeployer.command.groups;

import java.io.File;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.server.AppServicesServer;
import com.marklogic.mgmt.api.server.ManageServer;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.groups.GroupManager;

public class DeployGroupsCommand extends AbstractResourceCommand {

	private Server adminServerTemplate;
	private Server manageServerTemplate;
	private Server appServicesServerTemplate;

	private boolean fixAdminServerRewriter = true;
	private boolean createManageServer = true;
	private boolean createAppServicesServer = true;

	public DeployGroupsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_GROUPS);
        setUndoSortOrder(SortOrderConstants.DELETE_GROUPS);

        adminServerTemplate = new Server(null, "Admin");
    	adminServerTemplate.setUrlRewriter("rewriter.xqy");

    	manageServerTemplate = new ManageServer();
    	appServicesServerTemplate = new AppServicesServer();
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getGroupsDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new GroupManager(context.getManageClient());
    }

    /**
     * Does a simple check for a restart by checking for "cache-size" in the payload. This doesn't mean a
     * restart has occurred - the cache size may not changed - but that's fine, as the waitForRestart method on
     * AdminManager will quickly exit.
     */
    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, ResourceReference resourceReference,
            SaveReceipt receipt) {
        String payload = receipt.getPayload();
        if (payload != null) {
        	if (payload.contains("cache-size") && context.getAdminManager() != null) {
                if (logger.isDebugEnabled()) {
                    logger.info("Group payload contains cache-size parameter, so waiting for ML to restart");
                }
                context.getAdminManager().waitForRestart();
        	}
        }

        if (receipt.hasLocationHeader()) {
	        // When new groups are created, an Admin server is automatically created in that group.
	        // However, the Admin server's rewrite property is empty - causing problems with reading the timestamp
	        String groupName = new PayloadParser().getPayloadFieldValue(payload, "group-name", true);
	        ServerManager serverMgr = new ServerManager(context.getManageClient(), groupName);
	        if (fixAdminServerRewriter) {
		        if (logger.isInfoEnabled()) {
			        logger.info(format("Updating admin server in group %s to ensure that its url-rewriter is correct", groupName));
		        }
		        serverMgr.save(adminServerTemplate.getJson());
	        }

	        ensureGroupServersExist(serverMgr, groupName);
        }
    }

	/**
	 * Prior to 9.0-3 of MarkLogic, a Manage server isn't created by default for a new group. So this ensures that the
	 * Manage server will be created if such a version of ML is being used (see bug 46909).
	 *
	 * An App-Services server is created in the new group too. MarkLogic does not do this automatically, but it's done
	 * here because it's likely that modules will need to be loaded into one or more hosts within the new group. And
	 * the App-Services server on port 8000 is used by default for this.
	 *
	 * @param serverMgr
	 * @param groupName
	 */
	protected void ensureGroupServersExist(ServerManager serverMgr, String groupName) {
		if (createManageServer) {
			ensureServerExists(serverMgr, manageServerTemplate, groupName);
		}
		if (createAppServicesServer) {
			ensureServerExists(serverMgr, appServicesServerTemplate, groupName);
		}
    }

	protected void ensureServerExists(ServerManager serverMgr, Server server, String groupName) {
		final String name = server.getServerName();
		if (serverMgr.exists(name)) {
			logger.info(format("%s server already exists in group %s", name, groupName));
		} else {
			server.setGroupName(groupName);
			serverMgr.save(server.getJson());
			logger.info(format("Created the %s server in group %s", name, groupName));
		}
	}

	public Server getAdminServerTemplate() {
		return adminServerTemplate;
	}

	public void setAdminServerTemplate(Server adminServerTemplate) {
		this.adminServerTemplate = adminServerTemplate;
	}

	public Server getManageServerTemplate() {
		return manageServerTemplate;
	}

	public void setManageServerTemplate(Server manageServerTemplate) {
		this.manageServerTemplate = manageServerTemplate;
	}

	public Server getAppServicesServerTemplate() {
		return appServicesServerTemplate;
	}

	public void setAppServicesServerTemplate(Server appServicesServerTemplate) {
		this.appServicesServerTemplate = appServicesServerTemplate;
	}

	public boolean isFixAdminServerRewriter() {
		return fixAdminServerRewriter;
	}

	public void setFixAdminServerRewriter(boolean fixAdminServerRewriter) {
		this.fixAdminServerRewriter = fixAdminServerRewriter;
	}

	public boolean isCreateManageServer() {
		return createManageServer;
	}

	public void setCreateManageServer(boolean createManageServer) {
		this.createManageServer = createManageServer;
	}

	public boolean isCreateAppServicesServer() {
		return createAppServicesServer;
	}

	public void setCreateAppServicesServer(boolean createAppServicesServer) {
		this.createAppServicesServer = createAppServicesServer;
	}
}
