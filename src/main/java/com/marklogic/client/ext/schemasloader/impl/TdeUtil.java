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
