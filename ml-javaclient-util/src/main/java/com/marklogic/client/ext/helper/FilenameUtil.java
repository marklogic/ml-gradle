/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
