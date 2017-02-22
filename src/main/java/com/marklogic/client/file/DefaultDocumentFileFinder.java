package com.marklogic.client.file;

import com.marklogic.client.helper.LoggingObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class DefaultDocumentFileFinder extends LoggingObject implements FileVisitor<Path>, DocumentFileFinder {

	private Path currentAssetPath;
	private FileFilter fileFilter;
	private List<DocumentFile> documentFiles;

	public List<DocumentFile> findDocumentFiles(String... paths) {
		documentFiles = new ArrayList<>();
		for (String path : paths) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("Finding documents at path: %s", path));
			}
			this.currentAssetPath = Paths.get(path);
			try {
				Files.walkFileTree(this.currentAssetPath, this);
			} catch (IOException ie) {
				throw new RuntimeException(format("IO error while walking file tree at path: %s", path), ie);
			}
		}
		return documentFiles;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		boolean accept = fileFilter == null || fileFilter.accept(dir.toFile());
		if (accept) {
			if (logger.isTraceEnabled()) {
				logger.trace("Visiting directory: " + dir);
			}
			return FileVisitResult.CONTINUE;
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping directory: " + dir);
			}
			return FileVisitResult.SKIP_SUBTREE;
		}
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		if (fileFilter == null || fileFilter.accept(path.toFile())) {
			Path relPath = currentAssetPath.relativize(path);
			String uri = "/" + relPath.toString().replace("\\", "/");
			File f = path.toFile();
			this.documentFiles.add(new DocumentFile(uri, f));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}
}
