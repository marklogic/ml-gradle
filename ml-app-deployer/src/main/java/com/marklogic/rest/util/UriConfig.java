/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

import java.net.URI;
import java.net.URISyntaxException;

public class UriConfig {

	private String host;
	private int port;
	private String scheme = "http";
	private String basePath;

	public UriConfig() {
	}

	public UriConfig(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Using the java.net.URI constructor that takes a string. Using any other constructor runs into encoding problems,
	 * e.g. when a mimetype has a plus in it, that plus needs to be encoded, but doing as %2B will result in the % being
	 * double encoded. Unfortunately, it seems some encoding is still needed - e.g. for a pipeline like "Flexible Replication"
	 * with a space in its name, the space must be encoded properly as a "+".
	 *
	 * @param path
	 * @return
	 */
	public URI buildUri(String path) {
		String basePathToAppend = "";
		if (basePath != null) {
			if (!basePath.startsWith("/")) {
				basePathToAppend = "/";
			}
			basePathToAppend += basePath;
			if (path.startsWith("/") && basePathToAppend.endsWith("/")) {
				basePathToAppend = basePathToAppend.substring(0, basePathToAppend.length() - 1);
			}
		}
		try {
			return new URI(String.format("%s://%s:%d%s%s", getScheme(), getHost(), getPort(), basePathToAppend, path.replace(" ", "+")));
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Unable to build URI for path: " + path + "; cause: " + ex.getMessage(), ex);
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
}
