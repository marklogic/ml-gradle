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
package com.marklogic.appdeployer.command;

import com.marklogic.appdeployer.AppConfig;

import java.util.Map;

public class DefaultPayloadTokenReplacer implements PayloadTokenReplacer {

    public String replaceTokens(String payload, AppConfig appConfig, boolean isTestResource) {
        payload = replaceDefaultTokens(payload, appConfig, isTestResource);
        return replaceCustomTokens(payload, appConfig, isTestResource);
    }

    protected String replaceDefaultTokens(String payload, AppConfig appConfig, boolean isTestResource) {
        payload = payload.replace("%%NAME%%",
                isTestResource ? appConfig.getTestRestServerName() : appConfig.getRestServerName());
        payload = payload.replace("%%GROUP%%", appConfig.getGroupName());
        payload = payload.replace("%%DATABASE%%",
                isTestResource ? appConfig.getTestContentDatabaseName() : appConfig.getContentDatabaseName());
        payload = payload.replace("%%MODULES_DATABASE%%", appConfig.getModulesDatabaseName());
        payload = payload.replace("%%TRIGGERS_DATABASE%%", appConfig.getTriggersDatabaseName());
        payload = payload.replace("%%SCHEMAS_DATABASE%%", appConfig.getSchemasDatabaseName());
        payload = payload.replace("%%PORT%%", isTestResource ? appConfig.getTestRestPort().toString() : appConfig
                .getRestPort().toString());
        return payload;
    }

    protected String replaceCustomTokens(String payload, AppConfig appConfig, boolean isTestResource) {
    	if (payload != null) {
		    Map<String, String> customTokens = appConfig.getCustomTokens();
		    if (customTokens != null) {
			    for (String key : customTokens.keySet()) {
			    	if (key != null) {
			    		String value = customTokens.get(key);
			    		if (value != null) {
						    payload = payload.replace(key, value);
					    }
				    }
			    }
		    }
	    }
        return payload;
    }
}
