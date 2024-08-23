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
package com.marklogic.client.ext.modulesloader;

import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

public class Modules {

	/**
	 * DHF is making use of these so they can't be removed, even though they're not used within this project.
	 */
	private List<Resource> assets;
	private List<Resource> assetDirectories;
	private List<Resource> namespaces;
	private List<Resource> options;
	private List<Resource> services;
	private List<Resource> transforms;
	private Resource propertiesFile;

	public void addModules(Modules modules) {
		assetDirectories = addLists(assetDirectories, modules.getAssetDirectories());
		namespaces = addLists(namespaces, modules.getNamespaces());
		options = addLists(options, modules.getOptions());
		services = addLists(services, modules.getServices());
		transforms = addLists(transforms, modules.getTransforms());
		if (modules.getPropertiesFile() != null) {
			propertiesFile = modules.getPropertiesFile();
		}
	}

	protected List<Resource> addLists(List<Resource> myList, List<Resource> otherList) {
		if (otherList == null) {
			return myList;
		}
		if (myList == null) {
			myList = new ArrayList<>();
		}
		myList.addAll(otherList);
		return myList;
	}

	public List<Resource> getServices() {
		return services;
	}

	public List<Resource> getTransforms() {
		return transforms;
	}

	public List<Resource> getOptions() {
		return options;
	}

	public void setServices(List<Resource> resources) {
		this.services = resources;
	}

	public void setTransforms(List<Resource> transforms) {
		this.transforms = transforms;
	}

	public void setOptions(List<Resource> queryOptions) {
		this.options = queryOptions;
	}

	public List<Resource> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<Resource> namespaces) {
		this.namespaces = namespaces;
	}

	public Resource getPropertiesFile() {
		return propertiesFile;
	}

	public void setPropertiesFile(Resource propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public List<Resource> getAssetDirectories() {
		return assetDirectories;
	}

	public void setAssetDirectories(List<Resource> assetDirectories) {
		this.assetDirectories = assetDirectories;
	}

	public List<Resource> getAssets() {
		return assets;
	}

	public void setAssets(List<Resource> assets) {
		this.assets = assets;
	}
}
