package com.marklogic.appdeployer.util;

import com.marklogic.client.DatabaseClientFactory;

public interface JavaClientUtil {

	static DatabaseClientFactory.SSLHostnameVerifier toSSLHostnameVerifier(String type) {
		if ("any".equalsIgnoreCase(type)) {
			return DatabaseClientFactory.SSLHostnameVerifier.ANY;
		}
		if ("common".equalsIgnoreCase(type)) {
			return DatabaseClientFactory.SSLHostnameVerifier.COMMON;
		}
		if ("strict".equalsIgnoreCase(type)) {
			return DatabaseClientFactory.SSLHostnameVerifier.STRICT;
		}
		throw new IllegalArgumentException(String.format("Unrecognized SSLHostnameVerifier type: " + type));
	}
}
