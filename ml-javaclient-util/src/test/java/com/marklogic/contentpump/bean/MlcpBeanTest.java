/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.contentpump.bean;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MlcpBeanTest {

	MlcpBean bean = new MlcpBean();

	@Test
	void buildArgs() {
		bean.setCommand("IMPORT");
		bean.setUsername("user");
		bean.setPassword("password");
		bean.setHost("somehost");

		String[] args = bean.buildArgs();
		assertEquals(7, args.length);
		assertEquals("IMPORT", args[0]);
		assertEquals("-host", args[1]);
		assertEquals("somehost", args[2]);
		assertEquals("-password", args[3]);
		assertEquals("password", args[4]);
		assertEquals("-username", args[5]);
		assertEquals("user", args[6]);
	}

	@Test
	void excludePasswordWhenViewingArgs() {
		bean.setCommand("IMPORT");
		bean.setUsername("user");
		bean.setPassword("mySecret");

		String view = bean.viewArgs(bean.buildArgs());
		assertTrue(view.contains("-username"));
		assertTrue(view.contains(", user"));
		assertFalse(view.contains("-password"));
		assertFalse(view.contains("mySecret"));
	}
}
