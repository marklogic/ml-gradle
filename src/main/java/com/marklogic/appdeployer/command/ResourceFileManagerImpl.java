package com.marklogic.appdeployer.command;

import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager;

import java.io.File;
import java.util.Date;

/**
 * Extends the module-specific PropertiesModuleManager, as that class's behavior is exactly what's needed here as well.
 * Just uses a different default file path for the properties file that keeps track of timestamps.
 */
public class ResourceFileManagerImpl extends PropertiesModuleManager implements ResourceFileManager {

	public static final String DEFAULT_FILE_PATH = "build/com.marklogic.ml-app-deployer/resource-timestamps.properties";

	public ResourceFileManagerImpl() {
		this(DEFAULT_FILE_PATH);
	}

	public ResourceFileManagerImpl(String propertiesFilePath) {
		super(propertiesFilePath);
	}

	/**
	 * One difference between this and the parent class is that if the resource file should be processed, its timestamp
	 * in the properties file is immediately updated, as opposed to that being done via a separate method.
	 * <p>
	 * Note that this doesn't indicate that the resource file was successfully deployed. If an error occurs while
	 * deploying the resource, it is expected that a developer would then make a change to the resource file. If that
	 * doesn't occur - e.g. if the failure occurred due to an authentication issue, and thus the resource file doesn't
	 * have to be changed - the developer would need to turn off incremental support or delete the properties file that
	 * contains the timestamps.
	 *
	 * @param file
	 * @return
	 */
	@Override
	public boolean shouldResourceFileBeProcessed(File file) {
		// Need to initialize this on every check because the properties file may have been updated by
		// some other command
		this.initialize();
		boolean shouldBeProcessed = hasFileBeenModifiedSinceLastLoaded(file);
		if (shouldBeProcessed) {
			if (logger.isDebugEnabled()) {
				logger.debug("File is new or has been modified: " + file.getAbsolutePath());
			}
			super.saveLastLoadedTimestamp(file, new Date());
		} else if (logger.isInfoEnabled()) {
			logger.info("File is neither new nor modified: " + file.getAbsolutePath());
		}
		return shouldBeProcessed;
	}

}
