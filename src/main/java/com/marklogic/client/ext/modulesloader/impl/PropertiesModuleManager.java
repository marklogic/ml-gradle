package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.Properties;

public class PropertiesModuleManager extends LoggingObject implements ModulesManager {

    public static final String DEFAULT_FILE_PATH = "build/ml-javaclient-util/module-timestamps.properties";

    private Properties props;
    private String propertiesFilePath;
    private long minimumFileTimestampToLoad;
    private String host;

    public PropertiesModuleManager() {
        this(DEFAULT_FILE_PATH);
    }

    public PropertiesModuleManager(String propertiesFilePath) {
        props = new Properties();
        this.propertiesFilePath = propertiesFilePath;
    }

	/**
	 * Use this constructor so that the keys generated for the properties file account for the host associated with the
	 * given DatabaseClient.
	 *
	 * @param propertiesFilePath
	 * @param client
	 */
	public PropertiesModuleManager(String propertiesFilePath, DatabaseClient client) {
    	this(propertiesFilePath);
    	if (client != null) {
    		host = client.getHost();
	    }
    }

    @Override
    public void initialize() {
    	this.props = new Properties();

    	File propertiesFile = new File(propertiesFilePath);

    	if (propertiesFile.getParentFile() != null) {
		    propertiesFile.getParentFile().mkdirs();
	    }

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
		File propertiesFile = new File(propertiesFilePath);
        if (propertiesFile.exists()) {
            propertiesFile.delete();
            props.clear();
        }
    }

	/**
	 *
	 * @param file
	 * @return
	 */
	@Override
	public boolean hasFileBeenModifiedSinceLastLoaded(File file) {
    	if (minimumFileTimestampToLoad > 0 && file.lastModified() <= minimumFileTimestampToLoad) {
    		if (logger.isDebugEnabled()) {
    			logger.debug(String.format("lastModified for file '%s' is %d, which is before the minimumFileTimestampToLoad of %d",
				    file.getAbsolutePath(), file.lastModified(), minimumFileTimestampToLoad));
		    }
    		return false;
	    }

        String key = buildKey(file);
        String value = props.getProperty(key);
        if (value != null) {
            long lastModified = file.lastModified();
            long lastLoaded = Long.parseLong(value);
            return lastModified > lastLoaded;
        }
        return true;
    }

	/**
	 *
	 * @param file
	 * @param date
	 */
	@Override
	public void saveLastLoadedTimestamp(File file, Date date) {
        String key = buildKey(file);
        props.setProperty(key, date.getTime() + "");
        try (FileWriter fw = new FileWriter(propertiesFilePath)) {
            props.store(fw, "");
        } catch (Exception e) {
            logger.warn("Unable to store properties, cause: " + e.getMessage());
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
        String path = file.getAbsolutePath().toLowerCase();
        final String key = host != null ? host + ":" + path : path;
        if (logger.isDebugEnabled()) {
        	logger.debug("Key for file " + file.getAbsolutePath() + ": " + key);
        }
        return key;
    }

	public void setMinimumFileTimestampToLoad(long minimumFileTimestampToLoad) {
		this.minimumFileTimestampToLoad = minimumFileTimestampToLoad;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
