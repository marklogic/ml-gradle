package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.client.ext.helper.FilenameUtil;
import com.marklogic.client.ext.modulesloader.Modules;
import com.marklogic.client.ext.modulesloader.ModulesFinder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract implementation that provides implementations for loading all the different kinds of modules.
 * Subclasses need to override the findModulesWithResolvedBaseDir method.
 */
public abstract class BaseModulesFinder implements ModulesFinder {

    private FilenameFilter transformFilenameFilter = new TransformFilenameFilter();
    private FilenameFilter namespaceFilenameFilter = new NamespaceFilenameFilter();
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
		List<Resource> properties = findResources(baseDir, "rest-properties.json");
		if (properties.size() == 1) {
			modules.setPropertiesFile(properties.get(0));
		}
    }

    protected void addServices(Modules modules, String baseDir) {
		modules.setServices(findResources(baseDir,
			servicesPath + "/*.xq*",
			servicesPath + "/*.sjs"));
    }

    protected void addAssetDirectories(Modules modules, String baseDir) {
		List<Resource> dirs = new ArrayList<>();

		List<String> recognizedPaths = getRecognizedPaths();

		// classpath needs the trailing / to find child dirs
		findResources(baseDir, "*", "*/").stream().forEach(resource -> {
			File f = null;
			try {
				f = resource.getFile();
			} catch (IOException e) {}

			if (!recognizedPaths.contains(resource.getFilename())) {
				if (f == null || f.isDirectory()) {
					if (dirs.indexOf(resource) < 0) {
						dirs.add(resource);
					}
				}
			}
		});

        modules.setAssetDirectories(dirs);
    }

    protected List<String> getRecognizedPaths() {
        return Arrays.asList(optionsPath, servicesPath, transformsPath, namespacesPath, schemasPath);
    }

    protected void addOptions(Modules modules, String baseDir) {
        modules.setOptions(findResources(baseDir, optionsPath + "**/*.*"));
    }

    protected void addNamespaces(Modules modules, String baseDir) {
        modules.setNamespaces(findResources(baseDir, namespacesPath + "**/*.*"));
    }

    protected void addTransforms(Modules modules, String baseDir) {
        modules.setTransforms(findResources(
        	baseDir + "/" + transformsPath,
			"*.xq*", "*.xsl*", "*.sjs"));
    }

	protected List<Resource> findResources(String basePath, String... paths) {
		List<Resource> list = new ArrayList<>();
		for (String path : paths) {
			try {
				String finalPath = basePath;
				if (!finalPath.endsWith("/") && !path.startsWith("/")) {
					finalPath += "/";
				}
				finalPath += path;
				Resource[] r = resolver.getResources(finalPath);
				list.addAll(Arrays.asList(r));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}


	public FilenameFilter getTransformFilenameFilter() {
        return transformFilenameFilter;
    }

    public void setTransformFilenameFilter(FilenameFilter transformFilenameFilter) {
        this.transformFilenameFilter = transformFilenameFilter;
    }

    public FilenameFilter getNamespaceFilenameFilter() {
        return namespaceFilenameFilter;
    }

    public void setNamespaceFilenameFilter(FilenameFilter namespaceFilenameFilter) {
        this.namespaceFilenameFilter = namespaceFilenameFilter;
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
		if (!baseDir.startsWith("file:") && !baseDir.startsWith("classpath")) {
			baseDir = Paths.get(baseDir).toUri().toString();
		}

		return findModulesWithResolvedBaseDir(baseDir);
	}

	protected abstract Modules findModulesWithResolvedBaseDir(String resolvedBaseDir);
}

class TransformFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return FilenameUtil.isXslFile(name) || FilenameUtil.isXqueryFile(name) || FilenameUtil.isJavascriptFile(name);
    }
}

class NamespaceFilenameFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return true;
    }
}
