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

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceFilenameFilter;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.security.CertificateAuthorityManager;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DeployCertificateAuthoritiesCommand extends AbstractCommand {

    public DeployCertificateAuthoritiesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_CERTIFICATE_AUTHORITIES);

	    ResourceFilenameFilter filter = new ResourceFilenameFilter();
	    Set<String> extensions = new HashSet<>();
	    extensions.add(".cer");
	    extensions.add(".crt");
	    extensions.add(".der");
	    extensions.add(".p12");
	    extensions.add(".p7b");
	    extensions.add(".p7r");
	    extensions.add(".pem");
	    extensions.add(".pfx");
	    extensions.add(".spc");
	    filter.setSupportedFilenameExtensions(extensions);
	    setResourceFilenameFilter(filter);
    }

    @Override
    public void execute(CommandContext context) {
    	for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
		    File dir = configDir.getCertificateAuthoritiesDir();
		    if (dir.exists()) {
			    CertificateAuthorityManager mgr = new CertificateAuthorityManager(context.getManageClient());
			    for (File f : listFilesInDirectory(dir)) {
				    if (logger.isInfoEnabled()) {
					    logger.info("Creating certificate authority from file: " + f.getAbsolutePath());
				    }
				    String payload = copyFileToString(f, context);
				    ResponseEntity<String> response = mgr.create(payload);
				    if (logger.isInfoEnabled()) {
					    logger.info("Created certificate authority, location: " + response.getHeaders().getLocation());
				    }
			    }
		    } else {
			    logResourceDirectoryNotFound(dir);
		    }
	    }
    }

}
