package com.marklogic.appdeployer.command.security;

import java.io.File;
import java.util.List;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.resource.security.CertificateTemplateManager;

/**
 * Inserts host certificates for each certificate template returned by the Manage API. Host certificates are inserted
 * via the endpoint at http://docs.marklogic.com/REST/POST/manage/v2/certificate-templates/[id-or-name]#insertHC .
 *
 * To allow for host certificates to be associated with a certificate template, this command expects to find host
 * certificates in a directory named "(configuration directory)/security/certificate-templates/host-certificates/(name of template)/".
 *
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
			if (hostCertDir.exists()){
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
	 * @param templateName The name of the certificate template that the host certificate will be inserted into
	 * @param publicCertFile
	 * @param privateKeyFile
	 */
	protected void insertHostCertificate(CommandContext context, String templateName, File publicCertFile, File privateKeyFile) {
		CertificateTemplateManager mgr = new CertificateTemplateManager(context.getManageClient());
		if (!mgr.certificateExists(templateName)) {
			logger.info(format("Inserting host certificate for certificate template '%s'", templateName));
			String pubCertString = copyFileToString(publicCertFile);
			String privateKeyString = copyFileToString(privateKeyFile);
			mgr.insertHostCertificate(templateName, pubCertString, privateKeyString);
			logger.info(format("Inserted host certificate for certificate template '%s'", templateName));
		} else {
			logger.info(format("Host certificate already exists for certificate template '%s', so not inserting host certificate found at: %s",
				templateName, publicCertFile.getAbsolutePath()));
		}
	}

	public void setPublicCertificateFileExtension(String publicCertificateFileExtension) {
		this.publicCertificateFileExtension = publicCertificateFileExtension;
	}

	public void setPrivateKeyFileExtension(String privateKeyFileExtension) {
		this.privateKeyFileExtension = privateKeyFileExtension;
	}
}
