/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SortOrderConstantsTest  {

	@Test
	public void undeployMimetypesBeforeUsers() {
		assertTrue(SortOrderConstants.DELETE_MIMETYPES < SortOrderConstants.DELETE_USERS,
			"Mimetypes need to be undeployed before users since the user doing the deleting may be deleted when users are undeployed");
	}
}
