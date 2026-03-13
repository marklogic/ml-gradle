/*
 * Copyright (c) 2015-2026 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.progress.pdc.client;

import com.progress.pdc.client.generated.ApiException;

/**
 * Intended to provide additional context without losing the original ApiException.
 */
public class PdcClientException extends RuntimeException {

	public PdcClientException(String message, ApiException cause) {
		super(message, cause);
	}
}
