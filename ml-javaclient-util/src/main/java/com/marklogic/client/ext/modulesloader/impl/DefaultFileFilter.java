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
package com.marklogic.client.ext.modulesloader.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Simple implementation that accepts every file and ignores anything starting with ".".
 */
public class DefaultFileFilter implements FileFilter, FilenameFilter {

    @Override
    public boolean accept(File f) {
    	return accept(null, f.getName());
    }

	/**
	 * @param dir
	 * @param name
	 * @return Ignores the directory, returns false if the name starts with ".", otherwise true
	 */
	@Override
	public boolean accept(File dir, String name) {
		return name != null && !name.startsWith(".");
	}
}
