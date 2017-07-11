package com.marklogic.appdeployer.export.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.export.AbstractNamedResourceExporter;
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
		return new File(new ConfigDir(baseDir).getSecurityDir(), "users");
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
	 * @param resourceName
	 * @param payload
	 * @return
	 */
	@Override
	protected String beforeResourceWrittenToFile(String resourceName, String payload) {
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
			logger.warn("Unable to add a default password to exported user: " + resourceName + "; still exporting user but without a password; exception message: " + ex.getMessage());
		}
		return payload;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}
}
