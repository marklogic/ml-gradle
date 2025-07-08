/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
	private List<Resource> options;
	private List<Resource> services;
	private List<Resource> transforms;
	private Resource propertiesFile;

	public void addModules(Modules modules) {
		assetDirectories = addLists(assetDirectories, modules.getAssetDirectories());
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
