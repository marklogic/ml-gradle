/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext;

/**
 * The Authentication enum in marklogic-client-api was deprecated in 4.0.1, but this enum is useful in
 * DatabaseClientConfig as a way of referencing a particular SecurityContext implementation to use.
 */
public enum SecurityContextType {

	BASIC,
	CERTIFICATE,
	/**
	 * @since 4.5.0
	 */
	CLOUD,
	DIGEST,
	KERBEROS,
	/**
	 * @since 4.5.0
	 */
	SAML,
	/**
	 * @since 6.0.0
	 */
	OAUTH
}
