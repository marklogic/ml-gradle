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
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Inserts host certificates for each certificate template returned by the Manage API. Host certificates are inserted
 * via the endpoint at http://docs.marklogic.com/REST/POST/manage/v2/certificate-templates/[id-or-name]#insertHC .
 * <p>
 * To allow for host certificates to be associated with a certificate template, this command expects to find host
 * certificates in a directory named "(configuration directory)/security/certificate-templates/host-certificates/(name of template)/".
 * <p>
 * The public certificate file must be a PEM-formatted file with a file extension of ".crt". And the private key file must
 * be a PEM-formatted file with a file extension of ".key".
 */
public class InsertCertificateHostsTemplateCommand extends AbstractCommand {

	private String publicCertificateFileExtension = ".crt";
	private String privateKeyFileExtension = ".key";

	public InsertCertificateHostsTemplateCommand() {
		setExecuteSortOrder(SortOrderConstants.INSERT_HOST_CERTIFICATES);
	}

	@Override
	public void execute(CommandContext context) {
		List<String> templateNames = new CertificateTemplateManager(context.getManageClient()).getAsXml().getListItemNameRefs();
		if (templateNames != null && !templateNames.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("Looking for host certificates to insert for certificate templates: " + templateNames);
			}
			for (String templateName : templateNames) {
				insertHostCertificatesForTemplate(context, templateName);
			}
		}
	}

	/**
	 * Looks for host certificates for the given template name.
	 *
	 * @param context
	 * @param templateName Name of the template the host certificates should be inserted into
	 */
	protected void insertHostCertificatesForTemplate(CommandContext context, String templateName) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File hostCertDir = new File(configDir.getCertificateTemplatesDir() + File.separator + "host-certificates" + File.separator + templateName);
			logger.info(format("Looking for host certificate files ending in '%s' for template '%s' in: %s", publicCertificateFileExtension, templateName, hostCertDir.getAbsolutePath()));
			if (hostCertDir.exists()) {
				for (File f : hostCertDir.listFiles()) {
					if (f.getName().endsWith(publicCertificateFileExtension)) {
						File privateKeyFile = determinePrivateKeyFile(f);
						if (privateKeyFile.exists()) {
							logger.info("Found public certificate file at: " + f.getAbsolutePath() + ", and found corresponding private key file at: " + privateKeyFile.getAbsolutePath());
							this.insertHostCertificate(context, templateName, f, privateKeyFile);
						} else {
							logger.warn("Did not find expected private key file at: " + privateKeyFile.getAbsolutePath() + "; will ignore " +
								"public certificate file found at: " + f.getAbsolutePath());
						}
					}
				}
			}
		}
	}

	protected File determinePrivateKeyFile(File publicCertificateFile) {
		String path = publicCertificateFile.getAbsolutePath();
		return new File(path.substring(0, path.length() - publicCertificateFileExtension.length()) + privateKeyFileExtension);
	}

	/**
	 * @param context
	 * @param templateName   The name of the certificate template that the host certificate will be inserted into
	 *                       Assumes filename is hostname + .crt: ex: host1.marklogic.com.crt
	 * @param publicCertFile
	 * @param privateKeyFile
	 */
	protected void insertHostCertificate(CommandContext context, String templateName, File publicCertFile, File privateKeyFile) {
		if (!certificateExists(templateName, publicCertFile, context.getManageClient())) {
			logger.info(format("Inserting host certificate for certificate template '%s'", templateName));
			String pubCertString = copyFileToString(publicCertFile);
			String privateKeyString = copyFileToString(privateKeyFile);
			new CertificateTemplateManager(context.getManageClient()).insertHostCertificate(templateName, pubCertString, privateKeyString);
			logger.info(format("Inserted host certificate for certificate template '%s'", templateName));
		} else {
			logger.info(format("Host certificate already exists for certificate template '%s', so not inserting host certificate found at: %s",
				templateName, publicCertFile.getAbsolutePath()));
		}
	}

	/**
	 * @param templateName
	 * @param publicCertFile
	 * @param manageClient
	 * @return
	 */
	protected boolean certificateExists(String templateName, File publicCertFile, ManageClient manageClient) {
		CertificateTemplateManager mgr = new CertificateTemplateManager(manageClient);

		String hostName = null;
		try {
			hostName = getCertificateHostName(publicCertFile, manageClient);
		} catch (Exception ex) {
			logger.warn("Unable to determine host name for public certificate file: " + publicCertFile + "; cause: " + ex.getMessage() +
				". Due to this, the check to determine if the certificate exists already will not include a host name but will only be " +
				"based on the name of the template.");
		}

		if (hostName != null) {
			logger.info(format("Checking for existing certificate with name '%s' and host name '%s'", templateName, hostName));
			return mgr.certificateExists(templateName, hostName);
		}

		// This is very unexpected, as it would mean that the /v1/eval query was not able to extract a host name
		logger.info(format("Could not determine host name, so checking for existing certificate with name '%s'", templateName));
		return mgr.certificateExists(templateName);
	}

	/**
	 * Uses the /v1/eval endpoint on the Manage server to extract the host name from the given public certificate file.
	 *
	 * @param publicCertFile
	 * @param manageClient
	 * @return
	 */
	protected String getCertificateHostName(File publicCertFile, ManageClient manageClient) {
		final String query = makeQueryForHostName(publicCertFile);
		String response = manageClient.postForm("/v1/eval", "xquery", query).getBody();
		return extractHostNameFromEvalResponse(response);
	}

	/**
	 * Builds an XQuery query that can extract the host name from the given public certificate file.
	 *
	 * @param publicCertFile
	 * @return
	 */
	protected String makeQueryForHostName(File publicCertFile) {
		String certContents;
		try {
			certContents = new String(FileCopyUtils.copyToByteArray(publicCertFile));
		} catch (IOException e) {
			throw new RuntimeException("Unable to read certificate from file: " + publicCertFile + "; cause: " + e.getMessage());
		}

		return format("xdmp:x509-certificate-extract(\"%s\")/*:subject/*:commonName/fn:string()", certContents);
	}

	/**
	 * The /v1/eval endpoint returns a multipart/mixed response that Spring 5.x does not yet seem to handle, even though
	 * it appears that 5.2.x should. So there's some really hacky code here to extract the value of the host name
	 * from the /v1/eval response.
	 *
	 * @param response
	 * @return
	 */
	protected String extractHostNameFromEvalResponse(String response) {
		final String token = "X-Primitive: string";
		int pos = response.indexOf(token);
		if (pos < 0) {
			throw new IllegalArgumentException("Unable to extract host name from eval response: " + response + "; did not find: " + token);
		}
		response = response.substring(pos + token.length()).trim();
		pos = response.indexOf("--");
		if (pos < 0) {
			throw new IllegalArgumentException("Unable to extract host name from eval response: " + response + "; did not find '--' after " + token);
		}
		return response.substring(0, pos).trim();
	}

	public void setPublicCertificateFileExtension(String publicCertificateFileExtension) {
		this.publicCertificateFileExtension = publicCertificateFileExtension;
	}

	public void setPrivateKeyFileExtension(String privateKeyFileExtension) {
		this.privateKeyFileExtension = privateKeyFileExtension;
	}
}
