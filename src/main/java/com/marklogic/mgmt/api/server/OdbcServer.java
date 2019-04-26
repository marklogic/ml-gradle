package com.marklogic.mgmt.api.server;

import com.marklogic.mgmt.api.API;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "odbc-server-properties")
public class OdbcServer extends Server {

	public OdbcServer() {
	}

	public OdbcServer(API api, String serverName) {
		super(api, serverName);
	}
}
