/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

import java.nio.file.FileVisitor;
import java.nio.file.Path;

/**
 * Marker interface for DocumentFileProcessor implementations that also implement FileVisitor<Path>.
 * This allows for type-safe casting without unchecked warnings.
 */
public interface DocumentFileProcessorWithFileVisitor extends DocumentFileProcessor, FileVisitor<Path> {
}
