package com.marklogic.clientutil.modulesloader.xcc;

import java.io.File;

import com.marklogic.xcc.DocumentFormat;

/**
 * Encapsulates the logic for determining the document format of a given file.
 */
public interface DocumentFormatGetter {

    public DocumentFormat getDocumentFormat(File file);
}
