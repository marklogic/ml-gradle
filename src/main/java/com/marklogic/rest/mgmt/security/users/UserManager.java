package com.marklogic.rest.mgmt.security.users;

import org.jdom2.Namespace;

import com.marklogic.clientutil.LoggingObject;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

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
  
  public void createUser(String user) {
	  
  }

}
