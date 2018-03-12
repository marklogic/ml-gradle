package com.marklogic.mgmt.api.server;

public class ManageServer extends Server {
	public static final String MANAGE_SERVER_NAME = "Manage";
    
	public ManageServer() {
		setServerName(MANAGE_SERVER_NAME);
		setServerType("http");
		setRoot("Apps/");
		setPort(8002);
		setContentDatabase("App-Services");
		setErrorHandler("manage/error-handler.xqy");
		setUrlRewriter("manage/rewriter.xqy");
		setPrivilege("http://marklogic.com/xdmp/privileges/manage");
	}

}
