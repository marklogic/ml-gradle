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
package com.marklogic.appdeployer.command;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Introduced in 3.14.0 to act as a data structure for all the files that have been merged together for a resource
 * (which most often will be a single file) along with the corresponding JSON, which will change as files are added
 * to this and they're merged into the existing ObjectNode.
 * <p>
 * If resource merging isn't needed, then the ObjectNode can safely be set to null, and this will just hold a single
 * file.
 */
public class ResourceReference {

	private List<File> files = new ArrayList<>();
	private ObjectNode objectNode;

	public ResourceReference(File file, ObjectNode objectNode) {
		if (file != null) {
			this.files.add(file);
		}
		this.objectNode = objectNode;
	}

	public File getLastFile() {
		if (files == null || files.isEmpty()) {
			return null;
		}
		return files.get(files.size() - 1);
	}

	public List<File> getFiles() {
		return files;
	}

	public ObjectNode getObjectNode() {
		return objectNode;
	}

	public void setObjectNode(ObjectNode objectNode) {
		this.objectNode = objectNode;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
}
