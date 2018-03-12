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
