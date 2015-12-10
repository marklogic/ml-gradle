package com.rjrudin.marklogic.modulesloader.xcc;

import java.io.File;

import com.marklogic.xcc.DocumentFormat;

/**
 * Default impl. Feel free to enhance this, subclass it, or roll your own.
 */
public class DefaultDocumentFormatGetter implements DocumentFormatGetter {

    @Override
    public DocumentFormat getDocumentFormat(File file) {
        String name = file.getName();
        if (name.endsWith(".xml") || name.endsWith(".xsl")) {
            return DocumentFormat.XML;
        } else if (name.endsWith(".json")) {
            return DocumentFormat.JSON;
        } else if (name.endsWith(".swf") || name.endsWith(".jpeg") || name.endsWith(".jpg") || name.endsWith(".png")
                || name.endsWith(".gif") || name.endsWith(".svg") || name.endsWith(".ttf") || name.endsWith(".eot")
                || name.endsWith(".woff") || name.endsWith(".cur") || name.endsWith(".ico")) {
            return DocumentFormat.BINARY;
        }
        return DocumentFormat.TEXT;
    }

}
