/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalUserTest  {

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
