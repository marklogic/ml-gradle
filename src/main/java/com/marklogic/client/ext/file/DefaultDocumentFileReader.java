package com.marklogic.client.ext.file;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Non-threadsafe implementation that implements FileVisitor as a way of descending one or more file paths.
 */
public class DefaultDocumentFileReader extends AbstractDocumentFileReader implements FileVisitor<Path>, DocumentFileReader {

	private Path currentRootPath;
	private List<FileFilter> fileFilters;
	private List<DocumentFile> documentFiles;
	private String uriPrefix = "/";

	// Each of these are eagerly instantiated, and we retain a reference in case a client wants to modify them
	private CollectionsFileDocumentFileProcessor collectionsFileDocumentFileProcessor;
	private PermissionsFileDocumentFileProcessor permissionsFileDocumentFileProcessor;

	/**
	 * Calls initialize to instantiate some default DocumentFileProcessor objects.
	 */
	public DefaultDocumentFileReader() {
		super();
		initialize();
	}

	/**
	 * Walk the file tree at each of the given paths, applying any configured DocumentFileProcessor instances on each
	 * DocumentFile that is constructed by a File.
	 *
	 * @param paths
	 * @return
	 */
	public List<DocumentFile> readDocumentFiles(String... paths) {
		documentFiles = new ArrayList<>();
		for (String path : paths) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("Finding documents at path: %s", path));
			}
			Path p = constructPath(path);
			if (p != null) {
				this.currentRootPath = p;
				try {
					Files.walkFileTree(this.currentRootPath, this);
				} catch (IOException ie) {
					throw new RuntimeException(format("IO error while walking file tree at path: %s", path), ie);
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Path does not exist, so not reading files from it: " + path);
				}
			}
		}
		return documentFiles;
	}

	/**
	 * The path is first wrapped in a File; this prevents a bug in Gradle when Gradle is run in daemon mode, where it
	 * will try to resolve the path from its daemon directory.
	 *
	 * @param path
	 * @return
	 */
	protected Path constructPath(String path) {
		File f;
		if (path.startsWith("classpath") || path.startsWith("file:")) {
			try {
				f = new File(new URI(path));
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		} else {
			f = new File(path);
		}
		return f.exists() ? Paths.get(f.getAbsolutePath()) : null;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		boolean accept = acceptPath(dir, attrs);
		if (accept) {
			if (logger.isDebugEnabled()) {
				logger.debug("Visiting directory: " + dir);
			}
			return FileVisitResult.CONTINUE;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping directory: " + dir);
			}
			return FileVisitResult.SKIP_SUBTREE;
		}
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		if (exc != null) {
			logger.warn("Failed visiting file: " + exc.getMessage(), exc);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (exc != null) {
			logger.warn("Error in postVisitDirectory: " + exc.getMessage(), exc);
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		if (acceptPath(path, attrs)) {
			DocumentFile documentFile = buildDocumentFile(path, currentRootPath);
			documentFile = processDocumentFile(documentFile);
			if (documentFile != null) {
				this.documentFiles.add(documentFile);
			}
		}
		return FileVisitResult.CONTINUE;
	}

	/**
	 * If any of the configured FileFilter objects do not accept the Path, then it is not accepted.
	 *
	 * @param path
	 * @param attrs
	 * @return
	 */
	protected boolean acceptPath(Path path, BasicFileAttributes attrs) {
		if (fileFilters != null) {
			File file = path.toFile();
			for (FileFilter filter : fileFilters) {
				if (!filter.accept(file)) {
					return false;
				}
			}
		}
		return true;
	}

	protected DocumentFile buildDocumentFile(Path path, Path currentRootPath) {
		Path relPath = currentRootPath.relativize(path);
		String uri = relPath.toString().replace("\\", "/");
		if (uriPrefix != null) {
			uri = uriPrefix + uri;
		}
		File f = path.toFile();
		DocumentFile df = new DocumentFile(uri, f);
		df.setRootPath(currentRootPath);
		return df;
	}

	protected void initialize() {
		collectionsFileDocumentFileProcessor = new CollectionsFileDocumentFileProcessor();
		permissionsFileDocumentFileProcessor = new PermissionsFileDocumentFileProcessor();

		addFileFilter(collectionsFileDocumentFileProcessor);
		addFileFilter(permissionsFileDocumentFileProcessor);

		addDocumentFileProcessor(collectionsFileDocumentFileProcessor);
		addDocumentFileProcessor(permissionsFileDocumentFileProcessor);
		addDocumentFileProcessor(getFormatDocumentFileProcessor());
	}

	public void addFileFilter(FileFilter fileFilter) {
		if (fileFilters == null) {
			fileFilters = new ArrayList<>();
		}
		fileFilters.add(fileFilter);
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

	public CollectionsFileDocumentFileProcessor getCollectionsFileDocumentFileProcessor() {
		return collectionsFileDocumentFileProcessor;
	}

	public PermissionsFileDocumentFileProcessor getPermissionsFileDocumentFileProcessor() {
		return permissionsFileDocumentFileProcessor;
	}
}
