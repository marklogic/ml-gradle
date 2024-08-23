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
package com.marklogic.client.ext.schemasloader.impl;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class TdeUtil {

	public static final String TDE_COLLECTION = "http://marklogic.com/xdmp/tde";

	private final static Logger logger = LoggerFactory.getLogger(TdeUtil.class);

	public static boolean templateBatchInsertSupported(DatabaseClient client) {
		final int markLogic10Dot9 = 10000900;
		String result = client.newServerEval().javascript("xdmp.effectiveVersion()").eval(new StringHandle()).get();
		if (logger.isDebugEnabled()) {
			logger.debug("Checking if templateBatchInsert is supported; MarkLogic version: " + result);
		}
		return Integer.parseInt(result) >= markLogic10Dot9;
	}
}
