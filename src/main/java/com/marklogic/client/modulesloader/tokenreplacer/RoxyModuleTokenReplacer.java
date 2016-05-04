package com.marklogic.client.modulesloader.tokenreplacer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.PropertyPlaceholderHelper;

import com.marklogic.client.helper.LoggingObject;

/**
 * Follows the conventions used by Roxy, where properties are prefixed with "@ml.".
 * 
 * By default, this will load properties from "deploy/default.properties", "deploy/build.properties", and
 * "deploy/local.properties", if any of those exist.
 */
public class RoxyModuleTokenReplacer extends LoggingObject implements ModuleTokenReplacer {

    private Properties properties;
    private PropertyPlaceholderHelper helper;
    private String propertyPrefix = "@ml.";

    public RoxyModuleTokenReplacer() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("deploy/default.properties");
        filePaths.add("deploy/build.properties");
        filePaths.add("deploy/local.properties");
        initializeProperties(filePaths);
        initializeHelper();
    }

    public RoxyModuleTokenReplacer(List<String> filePaths) {
        initializeProperties(filePaths);
        initializeHelper();
    }

    protected void initializeProperties(List<String> filePaths) {
        properties = new Properties();
        for (String path : filePaths) {
            loadPropertiesFromFile(new File(path));
        }
    }

    protected void initializeHelper() {
        helper = new PropertyPlaceholderHelper(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
                PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
                PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, true);
    }

    protected void loadPropertiesFromFile(File file) {
        if (file.exists()) {
            FileReader reader = null;
            try {
                reader = new FileReader(file);
                if (logger.isInfoEnabled()) {
                    logger.info("Loading module properties from: " + file.getAbsolutePath());
                }
                properties.load(reader);
            } catch (IOException ex) {
                logger.warn(
                        "Unable to load properties from file " + file.getAbsolutePath() + "; cause: " + ex.getMessage(),
                        ex);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ie) {
                        // Ignore
                    }
                }
            }
        }
    }

    @Override
    public String replaceTokensInModule(String moduleText) {
        for (Object key : properties.keySet()) {
            String skey = propertyPrefix != null ? propertyPrefix + key : key.toString();
            if (logger.isTraceEnabled()) {
                logger.trace("Checking for key in module text: " + skey);
            }
            if (moduleText.contains(skey)) {
                String value = properties.getProperty(key.toString());
                value = helper.replacePlaceholders(value, properties);
                if (logger.isDebugEnabled()) {
                    logger.debug(format("Replacing %s with %s", skey, value));
                }
                moduleText = moduleText.replace(skey, value);
            }
        }
        return moduleText;
    }

    public void setPropertyPrefix(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }

}
