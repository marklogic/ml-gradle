/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.helper;

import java.io.File;

public abstract class FilenameUtil {

    public static boolean isXslFile(String filename) {
		return endsWithExtension(filename, ".xsl", ".xslt");
    }

    public static boolean isXqueryFile(String filename) {
		return endsWithExtension(filename, ".xqy", ".xqy");
    }

    public static boolean isJavascriptFile(String filename) {
		return endsWithExtension(filename, ".sjs", ".js");
    }

	public static boolean endsWithExtension(String filename, String... extensions) {
		if (filename == null || extensions == null) {
			return false;
		}
		filename = filename.toLowerCase();
		for (String extension : extensions) {
			if (extension != null && filename.endsWith(extension.toLowerCase())) {
				return true;
			}
		}
		return false;
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
