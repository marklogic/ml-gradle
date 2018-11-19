package com.acme;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SecurityContext;

public class DataServiceTest {
  @Test
  public void testRuleDefinitions() {
    // Note: The user must have the `rest-extension-user` role to be able to
    // access Data Service endpoints, by default.
    // This example does not include the security set-up in order to focus
    // on the Data Services aspects only.
    final String user = "sally";
    final String password = "********";
    final SecurityContext auth = new DatabaseClientFactory.DigestAuthContext(
        user, password);
    DatabaseClient db = DatabaseClientFactory.newClient("localhost", 8099,
        auth);
    assertEquals("Hey! Hey!", HelloWorld.on(db).whatsUp("Hey!", 2L));
  }
}