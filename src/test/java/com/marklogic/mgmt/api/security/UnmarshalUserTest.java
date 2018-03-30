package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import org.junit.Assert;
import org.junit.Test;

public class UnmarshalUserTest extends Assert {

	@Test
	public void xmlSmokeTest() {
		String xml = "<user-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <user-name>infostudio-admin</user-name>\n" +
			"  <description>Information Studio CPF pipeline and task runner</description>\n" +
			"  <roles>\n" +
			"    <role>cpf-restart</role>\n" +
			"    <role>infostudio-user</role>\n" +
			"  </roles>\n" +
			"</user-properties>\n";

		User user = new DefaultResourceMapper().readResource(xml, User.class);
		assertEquals("infostudio-admin", user.getUserName());
		assertEquals(2, user.getRole().size());
	}
}
