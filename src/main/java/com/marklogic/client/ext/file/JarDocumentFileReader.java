package com.marklogic.client.ext.file;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implementation of DocumentFileReader for reading documents in a JAR file. Not as feature-rich as
 * DefaultDocumentFileReader, mostly because of the challenges of accessing such documents.
 */
public class JarDocumentFileReader extends AbstractDocumentFileReader implements DocumentFileReader {

	private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	private List<FilenameFilter> filenameFilters = new ArrayList<>();
	private String uriPrefix = "/";
	private String resourcePattern = "**/*";

	public JarDocumentFileReader() {
		super();
	}

	@Override
	public List<DocumentFile> readDocumentFiles(String... paths) {
		List<DocumentFile> documentFiles = new ArrayList<>();
		for (String path : paths) {
			findResources(path, resourcePattern).stream().forEach(resource -> {
				DocumentFile documentFile = buildDocumentFile(path, resource);
				if (documentFile != null) {
					documentFile = processDocumentFile(documentFile);
					if (documentFile != null) {
						documentFiles.add(documentFile);
					}
				}
			});
		}
		return documentFiles;
	}

	/**
	 * Uses Spring's PathMatchingResourcePatternResolver to find resources on the given paths, relative to the given
	 * base path.
	 *
	 * @param basePath
	 * @param paths
	 * @return
	 */
	protected List<Resource> findResources(String basePath, String... paths) {
		List<Resource> list = new ArrayList<>();
		for (String path : paths) {
			try {
				String finalPath = basePath;
				if (!finalPath.endsWith("/") && !path.startsWith("/")) {
					finalPath += "/";
				}
				finalPath += path;
				if (logger.isDebugEnabled()) {
					logger.debug("Finding resources in path: " + finalPath);
				}
				Resource[] resources = resolver.getResources(finalPath);
				for (Resource r : resources) {
					if (canReadResource(r)) {
						list.add(r);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to find resources at path: " + path, e);
			}
		}
		return list;
	}

	/**
	 * Uses the list of FilenameFilter objects to determine if a document can be read from the given resource.
	 *
	 * @param r
	 * @return
	 */
	protected boolean canReadResource(Resource r) {
		if (r == null) {
			return false;
		}
		String filename = r.getFilename();
		if (filename == null) {
			return false;
		}

		boolean canRead = true;

		if (filenameFilters != null) {
			for (FilenameFilter filter : filenameFilters) {
				if (!filter.accept(null, filename)) {
					canRead = false;
					break;
				}
			}
		}

		return canRead;
	}

	public void addFilenameFilter(FilenameFilter filenameFilter) {
		if (filenameFilters == null) {
			filenameFilters = new ArrayList<>();
		}
		filenameFilters.add(filenameFilter);
	}

	protected DocumentFile buildDocumentFile(String rootPath, Resource resource) {
		try {
			String uri = resource.getURI().toString().replaceAll(Pattern.quote(rootPath), "");
			if (uriPrefix != null) {
				uri = uriPrefix + uri;
			}

			// some extra sanity checking to avoid trying to load directories
			File f = null;
			try {
				f = resource.getFile();
			} catch (IOException e) {
			}

			if (!uri.endsWith("/") && (f == null || !f.isDirectory())) {
				DocumentFile df = new DocumentFile(uri, resource);
				return df;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	public void setResourcePattern(String resourcePattern) {
		this.resourcePattern = resourcePattern;
	}

	public List<FilenameFilter> getFilenameFilters() {
		return filenameFilters;
	}

	public void setFilenameFilters(List<FilenameFilter> filenameFilters) {
		this.filenameFilters = filenameFilters;
	}
}
