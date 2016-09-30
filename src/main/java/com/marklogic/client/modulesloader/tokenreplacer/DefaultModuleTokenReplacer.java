package com.marklogic.client.modulesloader.tokenreplacer;

import com.marklogic.client.helper.LoggingObject;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Default implementation of ModuleTokenReplacer that relies on a list of PropertiesSource implementations for
 * finding tokens to replace in modules.
 * <p>Based on Roxy conventions of referencing properties in module text with "@ml." as a prefix, this class also by
 * default will attempt to find property names in module text with "@ml." as a prefix. This can be adjusted via the
 * propertyPrefix property.</p>
 */
public class DefaultModuleTokenReplacer extends LoggingObject implements ModuleTokenReplacer {

    private Properties properties;
    private PropertyPlaceholderHelper helper;
    private List<PropertiesSource> propertiesSources = new ArrayList<>();
    private String propertyPrefix;

    public void addPropertiesSource(PropertiesSource source) {
        this.propertiesSources.add(source);
    }

    protected void initializeHelper() {
        helper = new PropertyPlaceholderHelper(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX,
                PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX,
                PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR, true);
    }

    /**
     * Initialize the Properties instance based on all the PropertiesSources that have been registered.
     */
    protected void initializeProperties() {
        properties = new Properties();
        for (PropertiesSource source : propertiesSources) {
            Properties p = source.getProperties();
            if (p != null) {
                properties.putAll(p);
            }
        }
    }

    @Override
    public String replaceTokensInModule(String moduleText) {
        if (properties == null) {
            initializeProperties();
        }
        if (helper == null) {
            initializeHelper();
        }

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

    public List<PropertiesSource> getPropertiesSources() {
        return propertiesSources;
    }

    public void setPropertiesSources(List<PropertiesSource> propertiesSources) {
        this.propertiesSources = propertiesSources;
    }

    public void setPropertyPlaceholderHelper(PropertyPlaceholderHelper helper) {
        this.helper = helper;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setPropertyPrefix(String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
    }
}
