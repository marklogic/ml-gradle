package com.marklogic.client.ext.util;

import com.marklogic.client.eval.EvalResult;

import java.util.Iterator;

/**
 * Adapts an Iterator<EvalResult> to an Iterator<String> so that it can be used easily with a DMSDK QueryBatcher.
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
