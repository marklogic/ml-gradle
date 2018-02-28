package com.marklogic.appdeployer.command;

import org.junit.Assert;
import org.junit.Test;

public class SortOrderConstantsTest extends Assert {

	@Test
	public void undeployMimetypesBeforeUsers() {
		assertTrue("Mimetypes need to be undeployed before users since the user doing the deleting may be deleted when users are undeployed",
			SortOrderConstants.DELETE_MIMETYPES < SortOrderConstants.DELETE_USERS);
	}
}
