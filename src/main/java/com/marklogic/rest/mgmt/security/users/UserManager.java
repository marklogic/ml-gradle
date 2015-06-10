package com.marklogic.rest.mgmt.security.users;

import org.jdom2.Namespace;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

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
  
  public void createUser(String json) throws Exception {
	  ResponseEntity<String> response = client.postJson("/manage/v2/users", json);
	  logger.info(response.toString());
	  logger.info(response.getStatusCode().toString());
	  logger.info(HttpStatus.ACCEPTED.toString());
	  if (response.getStatusCode() == HttpStatus.CREATED) {
		  return;
	  } else {
		  logger.error("Invalid User JSON");
		  throw new Exception("Invalid JSON for User");
	  }
  }

}
