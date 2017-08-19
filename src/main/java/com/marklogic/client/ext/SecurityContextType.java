package com.marklogic.client.ext;

/**
 * The Authentication enum in marklogic-client-api was deprecated in 4.0.1, but this enum is useful in
 * DatabaseClientConfig as a way of referencing a particular SecurityContext implementation to use.
 */
public enum SecurityContextType {

	BASIC,
	CERTIFICATE,
	DIGEST,
	KERBEROS,
	NONE
}
