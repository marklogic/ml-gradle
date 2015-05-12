package com.marklogic.appdeployer.mgmt;

import org.jdom2.Namespace;
import org.springframework.web.client.RestTemplate;

import com.marklogic.appdeployer.util.Fragment;
import com.marklogic.clientutil.LoggingObject;

public class UserManager extends LoggingObject {
	
	private RestTemplate rt;
  private String baseUrl;

  public UserManager(RestTemplate rt, String baseUrl) {
      this.rt = rt;
      this.baseUrl = baseUrl;
  }

  public boolean userExists(String name) {
      String xml = rt.getForObject(baseUrl + "/manage/v2/users", String.class);
      Fragment f = new Fragment(xml, Namespace.getNamespace("sec", "http://marklogic.com/manage/security"));
      return f.elementExists(String.format("/sec:user-default-list/sec:list-items/sec:list-item[sec:nameref = '%s']", name));
  }

  public void setRt(RestTemplate rt) {
      this.rt = rt;
  }

  public void setBaseUrl(String baseUri) {
      this.baseUrl = baseUri;
  }

}
