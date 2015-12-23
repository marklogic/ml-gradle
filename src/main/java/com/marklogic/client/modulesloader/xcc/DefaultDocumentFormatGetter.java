package com.marklogic.client.modulesloader.xcc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.marklogic.xcc.DocumentFormat;

/**
 * Default impl. Feel free to enhance this, subclass it, or roll your own.
 */
public class DefaultDocumentFormatGetter implements DocumentFormatGetter {

    public final static String[] DEFAULT_BINARY_EXTENSIONS = new String[] { ".swf", ".jpeg", ".jpg", ".png", ".gif",
            ".svg", ".ttf", ".eot", ".woff", ".cur", ".ico" };

    private List<String> binaryExtensions = new ArrayList<String>();

    public DefaultDocumentFormatGetter() {
        for (String ext : DEFAULT_BINARY_EXTENSIONS) {
            binaryExtensions.add(ext);
        }
    }

    @Override
    public DocumentFormat getDocumentFormat(File file) {
        String name = file.getName();
        if (name.endsWith(".xml") || name.endsWith(".xsl") || name.endsWith(".xslt")) {
            return DocumentFormat.XML;
        } else if (name.endsWith(".json")) {
            return DocumentFormat.JSON;
        }

        boolean isBinary = false;
        for (String ext : binaryExtensions) {
            if (name.endsWith(ext)) {
                isBinary = true;
                break;
            }
        }
        return isBinary ? DocumentFormat.BINARY : DocumentFormat.TEXT;
    }

    public List<String> getBinaryExtensions() {
        return binaryExtensions;
    }

    public void setBinaryExtensions(List<String> binaryExtensions) {
        this.binaryExtensions = binaryExtensions;
    }

}
