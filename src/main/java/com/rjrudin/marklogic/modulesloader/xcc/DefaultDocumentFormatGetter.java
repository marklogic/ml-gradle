package com.rjrudin.marklogic.modulesloader.xcc;

import java.io.File;

import com.marklogic.xcc.DocumentFormat;

/**
 * Default impl that currently doesn't provide any support for binary. Feel free to enhance this, subclass it, or roll
 * your own.
 */
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
