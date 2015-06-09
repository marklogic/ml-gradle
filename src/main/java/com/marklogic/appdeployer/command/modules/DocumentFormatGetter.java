package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.xcc.DocumentFormat;

public interface DocumentFormatGetter {

    public DocumentFormat getDocumentFormat(File file);
}
