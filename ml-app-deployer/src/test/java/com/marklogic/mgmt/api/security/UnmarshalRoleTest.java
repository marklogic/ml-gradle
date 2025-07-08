/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.security;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalRoleTest  {

	@Test
	public void xmlSmokeTest() {
		String xml = "<role-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <role-name>manage-admin</role-name>\n" +
			"  <description>management services admin</description>\n" +
			"  <roles>\n" +
			"    <role>manage-user</role>\n" +
			"    <role>tiered-storage-admin</role>\n" +
			"    <role>temporal-admin</role>\n" +
			"    <role>infostudio-user</role>\n" +
			"  </roles>\n" +
			"  <privileges>\n" +
			"    <privilege>\n" +
			"      <privilege-name>get-system-logs</privilege-name>\n" +
			"      <action>http://marklogic.com/xdmp/privileges/logs/system</action>\n" +
			"      <kind>execute</kind>\n" +
			"    </privilege>\n" +
			"    <privilege>\n" +
			"      <privilege-name>environment-ui</privilege-name>\n" +
			"      <action>http://marklogic.com/xdmp/privileges/environment-ui</action>\n" +
			"      <kind>execute</kind>\n" +
			"    </privilege>\n" +
			"    <privilege>\n" +
			"      <privilege-name>get-taskserver-logs</privilege-name>\n" +
			"      <action>http://marklogic.com/xdmp/privileges/logs/taskserver</action>\n" +
			"      <kind>execute</kind>\n" +
			"    </privilege>\n" +
			"    <privilege>\n" +
			"      <privilege-name>manage-admin</privilege-name>\n" +
			"      <action>http://marklogic.com/xdmp/privileges/manage-admin</action>\n" +
			"      <kind>execute</kind>\n" +
			"    </privilege>\n" +
			"    <privilege>\n" +
			"      <privilege-name>get-logs</privilege-name>\n" +
			"      <action>http://marklogic.com/xdmp/privileges/logs</action>\n" +
			"      <kind>execute</kind>\n" +
			"    </privilege>\n" +
			"  </privileges>\n" +
			"</role-properties>\n";

		Role role = new DefaultResourceMapper().readResource(xml, Role.class);
		assertEquals("manage-admin", role.getRoleName());
		assertEquals(4, role.getRole().size());
		assertEquals(5, role.getPrivilege().size());
	}
}
