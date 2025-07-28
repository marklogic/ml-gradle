/*
 * Copyright (c) 2010-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

/**
 * @since 6.0.0
 */
public class RetryConfig {

	private boolean retryConnectionFailure = false;
	private int retryMaxAttempts = 3;
	private long retryInitialDelay = 1000;
	private double retryDelayMultiplier = 2;
	private long retryMaxDelay = 10000;

	public boolean isRetryConnectionFailure() {
		return retryConnectionFailure;
	}

	public void setRetryConnectionFailure(boolean retryConnectionFailure) {
		this.retryConnectionFailure = retryConnectionFailure;
	}

	public int getRetryMaxAttempts() {
		return retryMaxAttempts;
	}

	public void setRetryMaxAttempts(int retryMaxAttempts) {
		this.retryMaxAttempts = retryMaxAttempts;
	}

	public long getRetryInitialDelay() {
		return retryInitialDelay;
	}

	public void setRetryInitialDelay(long retryInitialDelay) {
		this.retryInitialDelay = retryInitialDelay;
	}

	public double getRetryDelayMultiplier() {
		return retryDelayMultiplier;
	}

	public void setRetryDelayMultiplier(double retryDelayMultiplier) {
		this.retryDelayMultiplier = retryDelayMultiplier;
	}

	public long getRetryMaxDelay() {
		return retryMaxDelay;
	}

	public void setRetryMaxDelay(long retryMaxDelay) {
		this.retryMaxDelay = retryMaxDelay;
	}
}
