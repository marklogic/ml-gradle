package com.marklogic.clientutil;

public abstract class FilenameUtil {

    public static boolean isXslFile(String filename) {
        return filename.endsWith(".xsl") || filename.endsWith(".xslt");
    }

    public static boolean isXqueryFile(String filename) {
        return filename.endsWith(".xqy") || filename.endsWith(".xq");
    }
}
