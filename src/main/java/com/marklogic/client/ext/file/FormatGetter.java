package com.marklogic.client.ext.file;

import java.io.File;

import com.marklogic.client.io.Format;

/**
 * Strategy interface for the Format that should be used when writing the given File as a document into MarkLogic.
 */
public interface FormatGetter {

    Format getFormat(File file);
}
