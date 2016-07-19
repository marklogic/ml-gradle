package com.marklogic.appdeployer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

public class DefaultAppConfigFactory extends PropertySourceFactory implements AppConfigFactory {

    public DefaultAppConfigFactory() {
        super();
    };

    public DefaultAppConfigFactory(PropertySource propertySource) {
        super(propertySource);
    }

    @Override
    public AppConfig newAppConfig() {
        AppConfig c = new AppConfig();

        String prop = null;
        String mlUsername = getProperty("mlUsername");
        String mlPassword = getProperty("mlPassword");

        prop = getProperty("mlAppName");
        if (prop != null) {
            logger.info("App name: " + prop);
            c.setName(prop);
        }

        prop = getProperty("mlConfigDir");
        if (prop != null) {
            logger.info("Config dir: " + prop);
            c.setConfigDir(new ConfigDir(new File(prop)));
        }
        
        prop = getProperty("mlSchemasPath");
        if (prop != null) {
        	logger.info("Schemas path: " + prop);
        	c.setSchemasPath(prop);
        }

        prop = getProperty("mlHost");
        if (prop != null) {
            logger.info("App host: " + prop);
            c.setHost(prop);
        }

        prop = getProperty("mlRestPort");
        if (prop != null) {
            logger.info("App REST port: " + prop);
            c.setRestPort(Integer.parseInt(prop));
        }

        prop = getProperty("mlTestRestPort");
        if (prop != null) {
            logger.info("App test REST port: " + prop);
            c.setTestRestPort(Integer.parseInt(prop));
        }

        prop = getProperty("mlAppServicesPort");
        if (prop != null) {
            logger.info("App services port: " + prop);
            c.setAppServicesPort(Integer.parseInt(prop));
        }

        prop = getProperty("mlRestAdminUsername");
        if (prop != null) {
            logger.info("REST admin username: " + prop);
            c.setRestAdminUsername(prop);
        } else if (mlUsername != null) {
            logger.info("REST admin username: " + mlUsername);
            c.setRestAdminUsername(mlUsername);
        }

        prop = getProperty("mlRestAdminPassword");
        if (prop != null) {
            c.setRestAdminPassword(prop);
        } else if (mlPassword != null) {
            c.setRestAdminPassword(mlPassword);
        }

        prop = getProperty("mlContentForestsPerHost");
        if (prop != null) {
            logger.info("Content forests per host: " + prop);
            c.setContentForestsPerHost(Integer.parseInt(prop));
        }

        prop = getProperty("mlModulePermissions");
        if (prop != null) {
            logger.info("Module permissions: " + prop);
            c.setModulePermissions(prop);
        }

        prop = getProperty("mlModulesDatabaseName");
        if (prop != null) {
            logger.info("Modules database name: " + prop);
            c.setModulesDatabaseName(prop);
        }
        
        prop = getProperty("mlAdditionalBinaryExtensions");
        if (prop != null) {
            String[] values = prop.split(",");
            logger.info("Additional binary extensions for loading modules: " + Arrays.asList(values));
            c.setAdditionalBinaryExtensions(values);
        }

        if (getProperty("mlSimpleSsl") != null) {
            logger.info(
                    "Using simple SSL context and 'ANY' hostname verifier for authenticating against client REST API server");
            c.setSimpleSslConfig();
        }

        prop = getProperty("mlDatabaseNamesAndReplicaCounts");
        if (prop != null) {
            logger.info("Database names and replica counts: " + prop);
            c.setDatabaseNamesAndReplicaCounts(prop);
        }

        prop = getProperty("mlFlexrepPath");
        if (prop != null) {
            logger.info("Flexrep path: " + prop);
            c.setFlexrepPath(prop);
        }

        prop = getProperty("mlGroupName");
        if (prop != null) {
            logger.info("Group name: " + prop);
            c.setGroupName(prop);
        }

        prop = getProperty("mlReplaceTokensInModules");
        if (prop != null) {
            logger.info("Replace tokens in modules: " + prop);
            c.setReplaceTokensInModules(Boolean.parseBoolean(prop));
        }

        prop = getProperty("mlUseRoxyTokenPrefix");
        if (prop != null) {
            logger.info("Use Roxy token prefix of '@ml.': " + prop);
            c.setUseRoxyTokenPrefix(Boolean.parseBoolean(prop));
        }

        prop = getProperty("mlModulePaths");
        if (prop != null) {
            logger.info("Module paths: " + prop);
            String[] paths = prop.split(",");
            // Ensure we have a modifiable list
            List<String> list = new ArrayList<>();
            for (String s : paths) {
                list.add(s);
            }
            c.setModulePaths(list);
        }

        return c;
    }

}
