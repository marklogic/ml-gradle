/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
