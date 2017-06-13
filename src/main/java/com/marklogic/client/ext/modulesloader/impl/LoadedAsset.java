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
