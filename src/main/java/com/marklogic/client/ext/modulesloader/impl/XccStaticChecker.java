package com.marklogic.client.ext.modulesloader.impl;

import com.marklogic.xcc.template.XccTemplate;

/**
 * XCC gives more information about a static check error - specifically, line-precise information - and
 * the REST API does not by default. But a REST API implementation can be easily created by subclassing
 * this class's parent class.
 */
public class XccStaticChecker extends AbstractStaticChecker {

	private XccTemplate xccTemplate;

	public XccStaticChecker(XccTemplate xccTemplate) {
		this.xccTemplate = xccTemplate;
	}

	@Override
	protected void executeQuery(String xquery) {
		if (logger.isDebugEnabled()) {
			logger.debug(xquery);
		}
		xccTemplate.executeAdhocQuery(xquery);
	}
}
