package com.marklogic.appdeployer.command;

import com.marklogic.client.ext.helper.LoggingObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Defines operations for managing whether a module needs to be installed or not.
 */
public class ResourceFileManagerImpl extends LoggingObject implements ResourceFileManager {

	public static final String DEFAULT_FILE_PATH = "build/ml-gradle/resource-timestamps.properties";

	private Properties props;
	private String propertiesFilePath;

	public ResourceFileManagerImpl() {
		this(DEFAULT_FILE_PATH);
	}

	public ResourceFileManagerImpl(String propertiesFilePath) {
		props = new Properties();
		this.propertiesFilePath = propertiesFilePath;
		initialize();
	}

	@Override
	public void initialize() {
		File propertiesFile = new File(propertiesFilePath);
		logger.info("Loading properties from: " + propertiesFile.getAbsolutePath());
		propertiesFile.getParentFile().mkdirs();
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

	@Override
	public boolean hasFileBeenModifiedSinceLastDeployed(File file) {
			String key = buildKey(file);
			Long lastFileTimestamp = file.lastModified();
			String lastDeployedTimestampPropertyValue = props.getProperty(key);
			if (lastDeployedTimestampPropertyValue != null) {
				Long lastDeployedTimestamp = null;
				try {
					lastDeployedTimestamp = Long.parseLong(lastDeployedTimestampPropertyValue);
				} catch (Exception e) {}
				if (lastDeployedTimestamp != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("lastDeployedTimestamp: " + lastDeployedTimestamp);
						logger.debug("key: " + key);
						logger.debug("lastFileTimestamp: " + lastFileTimestamp);
					}
					if (lastFileTimestamp != null) {
						return (lastFileTimestamp > lastDeployedTimestamp);
					} else {
						return true;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
	}

	@Override
	public void saveLastDeployedHash(File file) {
		String key = buildKey(file);
		props.setProperty(key, file.lastModified() + "");
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(propertiesFilePath));
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
