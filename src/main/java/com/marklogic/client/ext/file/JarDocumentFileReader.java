package com.marklogic.client.ext.file;

import com.marklogic.client.ext.helper.LoggingObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JarDocumentFileReader extends LoggingObject implements DocumentFileReader {

	private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	private List<FileFilter> fileFilters;
	private List<DocumentFileProcessor> documentFileProcessors = new ArrayList<>();
	FormatDocumentFileProcessor formatDocumentFileProcessor;
	private String uriPrefix = "/";

	public JarDocumentFileReader() {
		initialize();
	}

	@Override
	public List<DocumentFile> readDocumentFiles(String... paths) {
		List<DocumentFile> documentFiles = new ArrayList<>();
		for (String path : paths) {
			findResources(path, "**/*.*").stream().forEach(resource -> {
				DocumentFile documentFile = buildDocumentFile(path, resource);
				if (documentFile != null) {
					documentFile = processDocumentFile(documentFile);
					documentFiles.add(documentFile);
				}
			});
		}
		return documentFiles;
	}

	public DocumentFileProcessor getDocumentFileProcessor(String classShortName) {
		for (DocumentFileProcessor processor : documentFileProcessors) {
			if (ClassUtils.getShortName(processor.getClass()).equals(classShortName)) {
				return processor;
			}
		}
		return null;
	}

	public void addDocumentFileProcessor(DocumentFileProcessor processor) {
		if (documentFileProcessors == null) {
			documentFileProcessors = new ArrayList<>();
		}
		documentFileProcessors.add(processor);
	}

	public void addFileFilter(FileFilter fileFilter) {
		if (fileFilters == null) {
			fileFilters = new ArrayList<>();
		}
		fileFilters.add(fileFilter);
	}

	public List<DocumentFileProcessor> getDocumentFileProcessors() {
		return documentFileProcessors;
	}

	public void setDocumentFileProcessors(List<DocumentFileProcessor> documentFileProcessors) {
		this.documentFileProcessors = documentFileProcessors;
	}

	public List<FileFilter> getFileFilters() {
		return fileFilters;
	}

	public void setFileFilters(List<FileFilter> fileFilters) {
		this.fileFilters = fileFilters;
	}

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	private List<Resource> findResources(String basePath, String... paths) {
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

	protected void initialize() {
		formatDocumentFileProcessor = new FormatDocumentFileProcessor();
		addDocumentFileProcessor(formatDocumentFileProcessor);
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
			} catch (IOException e) {}

			if (!uri.endsWith("/") && (f == null || !f.isDirectory())) {
				DocumentFile df = new DocumentFile(uri, resource);
				return df;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return null;
	}

	protected DocumentFile processDocumentFile(DocumentFile documentFile) {
		for (DocumentFileProcessor processor : documentFileProcessors) {
			try {
				documentFile = processor.processDocumentFile(documentFile);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (documentFile == null) {
				break;
			}
		}
		return documentFile;
	}
}
