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
package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.ProtectedCollectionsManager;

import java.io.File;

public class DeployProtectedCollectionsCommand extends AbstractResourceCommand {

    public DeployProtectedCollectionsCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_PROTECTED_COLLECTIONS);
        setUndoSortOrder(SortOrderConstants.DELETE_PROTECTED_COLLECTIONS);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getProtectedCollectionsDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new ProtectedCollectionsManager(context.getManageClient());
    }

}
