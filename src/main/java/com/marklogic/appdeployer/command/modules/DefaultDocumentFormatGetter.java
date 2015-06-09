package com.marklogic.appdeployer.command.modules;

import java.io.File;

import com.marklogic.xcc.DocumentFormat;

public class DefaultDocumentFormatGetter implements DocumentFormatGetter {

    @Override
    public DocumentFormat getDocumentFormat(File file) {
        String name = file.getName();
        if (name.endsWith(".xml") || name.endsWith(".xsl")) {
            return DocumentFormat.XML;
        }
        return DocumentFormat.TEXT;
    }

}
