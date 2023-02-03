/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.ModulesManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Path;
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
	 * @return true if file has been modified
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
     *
     * @param file
     * @return a string that can be used as a key for a Properties object
     */
	protected String buildKey(File file) {
		String path = normalizeDriveLetter(file);
		final String key = host != null ? host + ":" + path : path;
		if (logger.isDebugEnabled()) {
			logger.debug("Key for file " + file.getAbsolutePath() + ": " + key);
		}
		return key;
	}

	/**
	 * Lower-casing avoids some annoying issues on Windows where sometimes you get "C:" at the start, and other times
	 * you get "c:".
	 *
	 * @param file
	 * @return
	 */
	protected String normalizeDriveLetter(File file) {
		Path absolutePath = file.toPath().toAbsolutePath();
		String path = absolutePath.toString();
		Path root = absolutePath.getRoot();
		if (root != null) {
			String drive = root.toString();
			if (path.startsWith(drive)) {
				path = drive.toLowerCase() + path.substring(drive.length());
			}
		}
		return path;
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
