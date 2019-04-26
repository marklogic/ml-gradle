package com.marklogic.mgmt.api.server;

import com.marklogic.mgmt.api.API;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xdbc-server-properties")
public class XdbcServer extends Server {

	public XdbcServer() {
	}

	public XdbcServer(API api, String serverName) {
		super(api, serverName);
	}
}
