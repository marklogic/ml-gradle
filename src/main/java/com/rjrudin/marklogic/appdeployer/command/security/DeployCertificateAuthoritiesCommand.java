package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;

import org.springframework.http.ResponseEntity;

import com.rjrudin.marklogic.appdeployer.command.AbstractCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.security.CertificateAuthorityManager;

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
                    String payload = copyFileToString(f);
                    ResponseEntity<String> response = mgr.create(payload);
                    if (logger.isInfoEnabled()) {
                        logger.info("Created certificate authority, location: " + response.getHeaders().getLocation());
                    }
                }
            }
        }
    }

}
