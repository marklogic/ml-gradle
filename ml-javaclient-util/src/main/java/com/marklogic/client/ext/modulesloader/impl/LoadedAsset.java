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

public class LoadedAsset {

	private String uri;
	private File file;
	private boolean canBeStaticallyChecked;

	public LoadedAsset(String uri, File file, boolean canBeStaticallyChecked) {
		this.uri = uri;
		this.file = file;
		this.canBeStaticallyChecked = canBeStaticallyChecked;
	}

	public String getUri() {
		return uri;
	}

	public File getFile() {
		return file;
	}

	public boolean isCanBeStaticallyChecked() {
		return canBeStaticallyChecked;
	}
}
