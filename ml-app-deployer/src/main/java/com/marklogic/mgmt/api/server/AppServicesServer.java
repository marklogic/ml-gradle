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
package com.marklogic.mgmt.api.server;

public class AppServicesServer extends Server {
	public static final String APP_SERVICES_SERVER_NAME = "App-Services";
	
	public AppServicesServer() {
		setServerName(APP_SERVICES_SERVER_NAME);
		setServerType("http");
		setRoot("/");
		setPort(8000);
		setModulesDatabase("Modules");
		setContentDatabase("Documents");
		setErrorHandler("/MarkLogic/rest-api/8000-error-handler.xqy");
		setUrlRewriter("/MarkLogic/rest-api/8000-rewriter.xml");
		setRewriteResolvesGlobally(true);
	}
}
