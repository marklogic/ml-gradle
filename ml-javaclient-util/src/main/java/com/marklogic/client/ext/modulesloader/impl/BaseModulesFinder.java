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

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract implementation that provides implementations for loading all the different kinds of modules.
 * Subclasses need to override the findModulesWithResolvedBaseDir method.
 */
public abstract class BaseModulesFinder extends LoggingObject implements ModulesFinder {

	private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Whether to treat paths that aren't recognized by this class (i.e. not services, options, namespaces, or
     * transforms) as asset paths that will then be loaded as asset modules.
     */
    private boolean includeUnrecognizedPathsAsAssetPaths = true;

    private String servicesPath = "services";
    private String optionsPath = "options";
    private String namespacesPath = "namespaces";
    private String transformsPath = "transforms";
    // special case for excluding from assets.
    private String schemasPath = "schemas";

    /**
     * Only supports JSON for now.
     *
     * @param modules
     * @param baseDir
     */
    protected void addPropertiesFile(Modules modules, String baseDir) {
		List<Resource> resources = findResources("REST properties file", baseDir, "rest-properties.json", "rest-properties.xml");
		for (Resource r : resources) {
			if (r.exists()) {
				modules.setPropertiesFile(r);
				// Only one should exist, so use the first one that exists
				break;
			}
		}
    }

    protected void addAssetDirectories(Modules modules, String baseDir) {
		List<Resource> dirs = new ArrayList<>();

		List<String> recognizedPaths = getRecognizedPaths();

		// classpath needs the trailing / to find child dirs
		findResources("non-REST module directories", baseDir, "*", "*/").stream().forEach(resource -> {
			try {
				String resourceFile = resource.getURL().getFile();
				if (logger.isDebugEnabled()) {
					logger.debug("Checking resource to see if it's a valid non-REST module directory: " + resourceFile);
				}

				resourceFile = decodeAssetDirectoryResource(resourceFile);
				File f = new File(resourceFile);
				String uri = resource.getURI().toString();
				boolean isRecognized = recognizedPaths.contains(f.getName());
				// when the modules are in a jar inside a war
				boolean hasWeirdWarPath = uri.contains("jar:war");
				if (!(isRecognized || hasWeirdWarPath)) {
					boolean isDir = (resource instanceof FileSystemResource && f.isDirectory());
					boolean isUrlResource = (resource instanceof UrlResource);
					boolean notInList = !dirs.contains(resource);
					if ((isDir || isUrlResource) && notInList) {
						dirs.add(resource);
					}
				}
			} catch (IOException e) {}
		});

        modules.setAssetDirectories(dirs);
    }

	/**
	 * There may be other characters that need decoding, but for now, only %20 is being converted back into a space.
	 *
	 * The reason %20 exists is because a Resource that represents a potential asset directory is accessed as a URL in
	 * order to support jar and war files. Accessing the directory as a URL results in spaces being converted to %20.
	 * In order to construct a File, these must be converted back into spaces.
	 *
	 * It may be that performing a full URL decoding on the resourceFile is the correct solution, just don't have enough
	 * test cases to know that this is safe for sure.
	 *
	 * @param resourceFile
	 * @return decoded resource path
	 */
	protected String decodeAssetDirectoryResource(String resourceFile) {
	    if (resourceFile.contains("%20")) {
		    resourceFile = resourceFile.replaceAll("%20", " ");
		    if (logger.isDebugEnabled()) {
			    logger.debug("Replaced occurrences of %20 with a space in potential non-REST module directory: " + resourceFile);
		    }
	    }
	    return resourceFile;
    }

    protected List<String> getRecognizedPaths() {
        return Arrays.asList(optionsPath, servicesPath, transformsPath, namespacesPath, schemasPath);
    }

	protected void addNamespaces(Modules modules, String baseDir) {
		modules.setNamespaces(findResources("namespaces", baseDir, namespacesPath + "/*.*"));
	}

	protected void addOptions(Modules modules, String baseDir) {
        modules.setOptions(findResources("options modules", baseDir,
	        optionsPath + "/*.xml",
	        optionsPath + "/*.json"
        ));
    }

	protected void addServices(Modules modules, String baseDir) {
		modules.setServices(findResources("service modules", baseDir,
			servicesPath + "/*.xq*",
			servicesPath + "/*.sjs"
		));
	}

	protected void addTransforms(Modules modules, String baseDir) {
        modules.setTransforms(findResources("transform modules", baseDir,
			transformsPath + "/*.xq*",
	        transformsPath + "/*.xsl*",
	        transformsPath + "/*.sjs"
        ));
    }

	/**
	 * @param moduleType used for a log message
	 * @param basePath
	 * @param paths
	 * @return list of resources
	 */
	protected List<Resource> findResources(String moduleType, String basePath, String... paths) {
		List<Resource> list = new ArrayList<>();
		for (String path : paths) {
			try {
				String finalPath = basePath;
				if (!finalPath.endsWith("/") && !path.startsWith("/")) {
					finalPath += "/";
				}
				finalPath += path;
				if (logger.isDebugEnabled()) {
					logger.debug("Finding " + moduleType + " at path: " + finalPath);
				}
				Resource[] r = resolver.getResources(finalPath);
				list.addAll(Arrays.asList(r));
			} catch (IOException e) {
				throw new RuntimeException("Unable to find resources at path: " + path, e);
			}
		}
		return list;
	}

    public void setServicesPath(String servicesPath) {
        this.servicesPath = servicesPath;
    }

    public void setOptionsPath(String optionsPath) {
        this.optionsPath = optionsPath;
    }

    public void setNamespacesPath(String namespacesPath) {
        this.namespacesPath = namespacesPath;
    }

    public void setTransformsPath(String transformsPath) {
        this.transformsPath = transformsPath;
    }

    public boolean isIncludeUnrecognizedPathsAsAssetPaths() {
        return includeUnrecognizedPathsAsAssetPaths;
    }

    public void setIncludeUnrecognizedPathsAsAssetPaths(boolean includeUnrecognizedPathsAsAssetPaths) {
        this.includeUnrecognizedPathsAsAssetPaths = includeUnrecognizedPathsAsAssetPaths;
    }

	@Override
	public final Modules findModules(String baseDir) {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Finding modules in baseDir: " + baseDir);
	    }
		if (!baseDir.startsWith("file:") && !baseDir.startsWith("classpath")) {
			/**
			 * Have to wrap this in a File first to address an issue where Gradle, when running in daemon mode, will
			 * resolve values passed into the Paths class from the directory where the daemon mode was launched, which
			 * may not be the current directory.
			 */
			baseDir = new File(baseDir).toURI().toString();
			if (logger.isDebugEnabled()) {
				logger.debug("Finding modules in baseDir, which was modified to be: " + baseDir);
			}
		}
		return findModulesWithResolvedBaseDir(baseDir);
	}

	protected abstract Modules findModulesWithResolvedBaseDir(String resolvedBaseDir);
}
