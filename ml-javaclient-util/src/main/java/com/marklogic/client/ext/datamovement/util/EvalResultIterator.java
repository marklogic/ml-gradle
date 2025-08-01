/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.datamovement.util;

import com.marklogic.client.eval.EvalResult;

import java.util.Iterator;

/**
 * Adapts an Iterator of EvalResults to an Iterator of Strings so that it can be used easily with a DMSDK QueryBatcher.
 */
public class EvalResultIterator implements Iterator<String> {

	private Iterator<EvalResult> evalResults;

	public EvalResultIterator(Iterator<EvalResult> evalResults) {
		this.evalResults = evalResults;
	}

	@Override
	public boolean hasNext() {
		return evalResults.hasNext();
	}

	@Override
	public String next() {
		return evalResults.next().getString();
	}
}
