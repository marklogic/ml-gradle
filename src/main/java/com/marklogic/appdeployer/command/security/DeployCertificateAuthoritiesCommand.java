package com.marklogic.appdeployer.command.security;

import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.security.CertificateAuthorityManager;
import org.springframework.http.ResponseEntity;

import java.io.File;

public class DeployCertificateAuthoritiesCommand extends AbstractCommand {

    public DeployCertificateAuthoritiesCommand() {
        setExecuteSortOrder(SortOrderConstants.DEPLOY_CERTIFICATE_AUTHORITIES);
    }

    @Override
    public void execute(CommandContext context) {
        File dir = new File(context.getAppConfig().getConfigDir().getSecurityDir(), "certificate-authorities");
        if (dir.exists()) {
            CertificateAuthorityManager mgr = new CertificateAuthorityManager(context.getManageClient());
            for (File f : dir.listFiles()) {
                if (f.getName().endsWith("crt")) {
                    if (logger.isInfoEnabled()) {
                        logger.info("Creating certificate authority from file: " + f.getAbsolutePath());
                    }
                    String payload = copyFileToString(f, context);
                    ResponseEntity<String> response = mgr.create(payload);
                    if (logger.isInfoEnabled()) {
                        logger.info("Created certificate authority, location: " + response.getHeaders().getLocation());
                    }
                }
            }
        }
    }

}
