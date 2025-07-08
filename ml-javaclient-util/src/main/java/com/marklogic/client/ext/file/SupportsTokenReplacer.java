/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.file;

import com.marklogic.client.ext.tokenreplacer.TokenReplacer;

/**
 * Intended to be implemented by instances of {@code DocumentFileProcessor} that wish to use a
 * {@code TokenReplacer} on the files that they read.
 */
public interface SupportsTokenReplacer {

	void setTokenReplacer(TokenReplacer tokenReplacer);
}
