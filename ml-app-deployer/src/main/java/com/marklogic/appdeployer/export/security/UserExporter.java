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

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.impl.AbstractNamedResourceExporter;
import com.marklogic.appdeployer.export.impl.ExportInputs;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.security.UserManager;
import com.marklogic.rest.util.Fragment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;

public class UserExporter extends AbstractNamedResourceExporter {

	private PayloadParser payloadParser = new PayloadParser();
	private String defaultPassword = "CHANGEME";

	public UserExporter(ManageClient manageClient, String... usernames) {
		super(manageClient, usernames);
	}

	@Override
	protected ResourceManager newResourceManager(ManageClient manageClient) {
		return new UserManager(manageClient);
	}

	@Override
	protected File getResourceDirectory(File baseDir) {
		return new ConfigDir(baseDir).getUsersDir();
	}

	@Override
	protected String[] getExportMessages() {
		return new String[]{"The exported user files each have a default password in them, as the real password cannot be exported for security reasons."};
	}

	/**
	 * As of version 2.9.0 of ml-app-deployer, an attempt is made to add a default password to each exported user. This
	 * allows the user to be immediately deployed, and the developer can then change the password to a real one at a
	 * later date.
	 *
	 * @param payload
	 * @return
	 */
	@Override
	protected String beforeResourceWrittenToFile(ExportInputs exportInputs, String payload) {
		try {
			if (payloadParser.isJsonPayload(payload)) {
				ObjectNode json = (ObjectNode) payloadParser.parseJson(payload);
				if (!json.has("password")) {
					json.put("password", defaultPassword);
					return objectMapper.writeValueAsString(json);
				}
			} else {
				Fragment xml = new Fragment(payload);
				if (!xml.elementExists("/node()/m:password")) {
					Document doc = xml.getInternalDoc();
					doc.getRootElement().addContent(new Element("password", "http://marklogic.com/manage").setText(defaultPassword));
					return new XMLOutputter(Format.getPrettyFormat()).outputString(doc);
				}
			}
		} catch (Exception ex) {
			logger.warn("Unable to add a default password to exported user: " + exportInputs.getResourceName() +
				"; still exporting user but without a password; exception message: " + ex.getMessage());
		}
		return payload;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}
}
