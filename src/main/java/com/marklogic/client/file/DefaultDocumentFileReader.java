package com.marklogic.client.file;

import com.marklogic.client.helper.LoggingObject;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Non-threadsafe implementation that implements FileVisitor as a way of descending one or more file paths.
 */
public class DefaultDocumentFileReader extends LoggingObject implements FileVisitor<Path>, DocumentFileReader {

	private Path currentRootPath;
	private List<FileFilter> fileFilters;
	private List<DocumentFile> documentFiles;
	private List<DocumentFileProcessor> documentFileProcessors;
	private String uriPrefix = "/";

	public DefaultDocumentFileReader() {
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
			if (logger.isInfoEnabled()) {
				logger.info(format("Finding documents at path: %s", path));
			}
			this.currentRootPath = Paths.get(path);
			this.currentRootPath.toFile().mkdirs();
			try {
				File rob = currentRootPath.toFile();
				logger.info("CRP: " + currentRootPath.toFile().getAbsolutePath());
				logger.info("exists: " + rob.exists());
				logger.info("dir: " + rob.isDirectory());
				Files.walkFileTree(this.currentRootPath, this);
			} catch (IOException ie) {
				throw new RuntimeException(format("IO error while walking file tree at path: %s", path), ie);
			}
		}
		return documentFiles;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		logger.info("visit dir: " + dir.toFile().getAbsolutePath());
		boolean accept = acceptPath(dir, attrs);
		if (accept) {
			if (logger.isInfoEnabled()) {
				logger.info("Visiting directory: " + dir);
			}
			return FileVisitResult.CONTINUE;
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Skipping directory: " + dir);
			}
			return FileVisitResult.SKIP_SUBTREE;
		}
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		if (exc != null) {
			logger.info("vff: " + exc.getMessage());
			exc.printStackTrace();
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (exc != null) {
			logger.info("pvd: " + exc.getMessage());
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		logger.info("visit file: " + path.toFile().getAbsolutePath());
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
		logger.info("acceptPath: " + path.toFile().getAbsolutePath());
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
		return new DocumentFile(uri, f);
	}

	protected DocumentFile processDocumentFile(DocumentFile documentFile) {
		for (DocumentFileProcessor processor : documentFileProcessors) {
			documentFile = processor.processDocumentFile(documentFile);
			if (documentFile == null) {
				break;
			}
		}
		return documentFile;
	}

	protected void initialize() {
		CollectionsDocumentFileProcessor cdfp = new CollectionsDocumentFileProcessor();
		PermissionsDocumentFileProcessor pdfp = new PermissionsDocumentFileProcessor();

		addFileFilter(cdfp);
		addFileFilter(pdfp);

		addDocumentFileProcessor(cdfp);
		addDocumentFileProcessor(pdfp);
		addDocumentFileProcessor(new FormatDocumentFileProcessor());
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
}
