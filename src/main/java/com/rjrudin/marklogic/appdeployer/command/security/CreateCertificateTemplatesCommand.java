package com.rjrudin.marklogic.appdeployer.command.security;

import java.io.File;
import java.net.URI;
import java.util.Map;

import com.rjrudin.marklogic.appdeployer.command.AbstractResourceCommand;
import com.rjrudin.marklogic.appdeployer.command.CommandContext;
import com.rjrudin.marklogic.appdeployer.command.SortOrderConstants;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.SaveReceipt;
import com.rjrudin.marklogic.mgmt.security.CertificateTemplateManager;

public class CreateCertificateTemplatesCommand extends AbstractResourceCommand {

    public CreateCertificateTemplatesCommand() {
        setExecuteSortOrder(SortOrderConstants.CREATE_CERTIFICATE_TEMPLATES);
    }

    @Override
    protected File getResourcesDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "certificate-templates");
    }

    @Override
    protected ResourceManager getResourceManager(CommandContext context) {
        return new CertificateTemplateManager(context.getManageClient());
    }

    @Override
    protected void afterResourceSaved(ResourceManager mgr, CommandContext context, File resourceFile,
            SaveReceipt receipt) {
        URI location = receipt.getResponse().getHeaders().getLocation();
        Map<String, String> customTokens = context.getAppConfig().getCustomTokens();
        if (location != null) {
            // Create
            String[] tokens = location.getPath().split("/");
            String id = tokens[tokens.length - 1];
            String key = "certificate-template-" + receipt.getResourceId();
            if (logger.isInfoEnabled()) {
                logger.info(format("Storing token with key '%s' and value '%s'", key, id));
            }
            customTokens.put(key, id);
        } else {
            // Update
            String path = receipt.getPath();
            String[] tokens = path.split("/");
            // Path is expected to be /manage/v2/certificate-templates/id/properties
            String id = tokens[tokens.length - 2];
            String key = "certificate-template-" + receipt.getResourceId();
            if (logger.isInfoEnabled()) {
                logger.info(format("Storing token with key '%s' and value '%s'", key, id));
            }
            customTokens.put(key, id);
        }
    }

}
