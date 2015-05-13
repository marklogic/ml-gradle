package com.marklogic.appdeployer.mgmt.security;

import org.jdom2.Namespace;

import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.clientutil.LoggingObject;

public class UserManager extends LoggingObject {
	
	private ManageClient client;

  public UserManager(ManageClient client) {
      this.client = client;
  }

  public boolean userExists(String name) {
  		logger.info("Checking for existence of username: " + name);
      String xml = client.getRestTemplate().getForObject(client.getBaseUrl() + "/manage/v2/users", String.class);
      Fragment f = new Fragment(xml, Namespace.getNamespace("sec", "http://marklogic.com/manage/security"));
      return f.elementExists(String.format("/sec:user-default-list/sec:list-items/sec:list-item[sec:nameref = '%s']", name));
  }

}
