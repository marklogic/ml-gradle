package com.marklogic.client.modulesloader.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.Properties;

import com.marklogic.client.helper.LoggingObject;
import com.marklogic.client.modulesloader.ModulesManager;

public class PropertiesModuleManager extends LoggingObject implements ModulesManager {

    public static final String DEFAULT_FILE_PATH = "build/ml-javaclient-util/module-timestamps.properties";

    private Properties props;
    private File propertiesFile;

    public PropertiesModuleManager() {
        this(new File(DEFAULT_FILE_PATH));
    }

    public PropertiesModuleManager(File propertiesFile) {
        props = new Properties();
        this.propertiesFile = propertiesFile;
    }

    @Override
    public void initialize() {
        this.propertiesFile.getParentFile().mkdirs();
        if (propertiesFile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(propertiesFile);
                if (logger.isDebugEnabled()) {
                    logger.debug("Loading properties from: " + propertiesFile.getAbsolutePath());
                }
                props.load(fis);
            } catch (Exception e) {
                logger.warn("Unable to load properties, cause: " + e.getMessage());
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

    public void deletePropertiesFile() {
        if (propertiesFile.exists()) {
            propertiesFile.delete();
        }
    }

    public boolean hasFileBeenModifiedSinceLastInstalled(File file) {
        String key = buildKey(file);
        String value = props.getProperty(key);
        if (value != null) {
            long lastModified = file.lastModified();
            long lastInstalled = Long.parseLong(value);
            return lastModified > lastInstalled;
        }
        return true;
    }

    public void saveLastInstalledTimestamp(File file, Date date) {
        String key = buildKey(file);
        props.setProperty(key, date.getTime() + "");
        FileWriter fw = null;
        try {
            fw = new FileWriter(propertiesFile);
            props.store(fw, "");
        } catch (Exception e) {
            logger.warn("Unable to store properties, cause: " + e.getMessage());
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    /**
     * Lower-casing avoids some annoying issues on Windows where sometimes you get "C:" at the start, and other times
     * you get "c:". This of course will be a problem if you for some reason have modules with the same names but
     * differing in some cases, but I'm not sure why anyone would do that.
     *
     * @param file
     * @return
     */
    protected String buildKey(File file) {
        return file.getAbsolutePath().toLowerCase();
    }
}
