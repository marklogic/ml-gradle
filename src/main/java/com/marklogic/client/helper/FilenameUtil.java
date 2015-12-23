package com.rjrudin.marklogic.client;

import java.io.File;

public abstract class FilenameUtil {

    public static boolean isXslFile(String filename) {
        return filename.endsWith(".xsl") || filename.endsWith(".xslt");
    }

    public static boolean isXqueryFile(String filename) {
        return filename.endsWith(".xqy") || filename.endsWith(".xq");
    }

    public static boolean isJavascriptFile(String filename) {
        return filename.endsWith(".sjs") || filename.endsWith(".js");
    }

    public static String getFileExtension(File f) {
        String[] split = f.getName().split("\\.");
        if (split.length > 1) {
            return split[1];
        } else {
            return null;
        }
    }
}
