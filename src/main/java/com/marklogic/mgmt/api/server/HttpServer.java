package com.marklogic.mgmt.api.server;

import com.marklogic.mgmt.api.API;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "http-server-properties")
public class HttpServer extends Server {

	public HttpServer() {
	}

	public HttpServer(API api, String serverName) {
		super(api, serverName);
	}
}
