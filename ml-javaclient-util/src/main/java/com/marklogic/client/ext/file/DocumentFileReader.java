/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

import java.util.List;

/**
 * Strategy interface for determining which files to load into MarkLogic, with those files being captured as a List of
 * DocumentFile objects.
 *
 * Note that with the new DataMovementManager in ML9, it should soon make sense to load each file after it's read,
 * with DMM batching up and flushing writes to MarkLogic. An implementation of this can then use DMM to both read and
 * write all files as documents into MarkLogic, and then the returned List of a list of all the files that were
 * processed.
 */
public interface DocumentFileReader {

	List<DocumentFile> readDocumentFiles(String... paths);
}
