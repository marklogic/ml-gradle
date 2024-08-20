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
package com.marklogic.appdeployer.export.security;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractResourceExporter;
import com.marklogic.appdeployer.export.ExportedResources;
import com.marklogic.appdeployer.export.impl.SimpleExportInputs;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.security.AmpManager;
import com.marklogic.rest.util.ResourcesFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AmpExporter extends AbstractResourceExporter {

	private String[] ampUriRefs;

	public AmpExporter(ManageClient manageClient, String... ampUriRefs) {
		super(manageClient);
		this.ampUriRefs = ampUriRefs;
	}

	@Override
	public ExportedResources exportResources(File baseDir) {
		List<File> files = new ArrayList<>();
		String message = null;
		if (ampUriRefs != null && ampUriRefs.length > 0) {
			AmpManager mgr = new AmpManager(getManageClient());
			ResourcesFragment amps = mgr.getAsXml();
			File resourceDir = new ConfigDir(baseDir).getAmpsDir();
			resourceDir.mkdirs();
			for (String ampUriRef : ampUriRefs) {
				String nameRef = amps.getNameRefForUriRef(ampUriRef);
				if (nameRef == null || nameRef.trim().length() == 0) {
					logger.warn("Could not find amp with uriref: " + ampUriRef);
				}
				else {
					AmpExportInputs inputs = new AmpExportInputs(ampUriRef, nameRef, buildUrlParamsFromUriRef(ampUriRef));
					File f = exportToFile(mgr, inputs, resourceDir);
					if (f != null) {
						files.add(f);
					}
				}
			}

			message = "Each amp is exported to a file named after its nameref and the hash of its uriref.";
		}

		return new ExportedResources(files, message);
	}

	/**
	 * TODO Could move this to a utility class.
	 *
	 * @param ampUriRef
	 * @return
	 */
	protected String[] buildUrlParamsFromUriRef(String ampUriRef) {
		int pos = ampUriRef.indexOf("?");
		String qs = ampUriRef.substring(pos + 1);
		String[] params = qs.split("&");
		List<String> urlParams = new ArrayList<>();
		for (String param : params) {
			String[] tokens = param.split("=");
			urlParams.add(tokens[0]);
			urlParams.add(tokens[1]);
		}
		return urlParams.toArray(new String[]{});
	}
}

class AmpExportInputs extends SimpleExportInputs {

	private String ampUriRef;

	public AmpExportInputs(String ampUriRef, String resourceName, String... resourceUrlParams) {
		super(resourceName, resourceUrlParams);
		this.ampUriRef = ampUriRef;
	}

	@Override
	public String buildFilename(String suffix) {
		return getResourceName() + "-" + ampUriRef.hashCode() + "." + suffix;
	}
}


