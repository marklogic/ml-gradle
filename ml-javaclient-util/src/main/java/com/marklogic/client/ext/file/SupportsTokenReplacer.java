package com.marklogic.client.ext.file;

import com.marklogic.client.ext.tokenreplacer.TokenReplacer;

/**
 * Intended to be implemented by instances of {@code DocumentFileProcessor} that wish to use a
 * {@code TokenReplacer} on the files that they read.
 */
public interface SupportsTokenReplacer {

	void setTokenReplacer(TokenReplacer tokenReplacer);
}
