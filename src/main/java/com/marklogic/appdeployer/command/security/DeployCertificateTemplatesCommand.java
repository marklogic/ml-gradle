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
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;

import java.io.File;

public class DeployCertificateTemplatesCommand extends AbstractResourceCommand {

    public DeployCertificateTemplatesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_CERTIFICATE_TEMPLATES);
        setUndoSortOrder(SortOrderConstants.DELETE_CERTIFICATE_TEMPLATES);

        // Since an HTTP server file needs to refer to a certificate template by its ID, this is set to true
        setStoreResourceIdsAsCustomTokens(true);
    }

    @Override
    protected File[] getResourceDirs(CommandContext context) {
    	return findResourceDirs(context, configDir -> configDir.getCertificateTemplatesDir());
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new CertificateTemplateManager(context.getManageClient());
    }

}
