package com.marklogic.mgmt.resource.security;

import com.marklogic.mgmt.AbstractMgmtTest;
import org.junit.Before;
import org.junit.Test;

/**
 * This is really just a smoke test, as testing these for real would involve setting up encryption keys and verifying
 * that they're rotated correctly.
 *
 * It also doesn't yet have tests for importing or exporting a wallet
 */
public class SecurityManagerTest extends AbstractMgmtTest {

	private SecurityManager securityManager;

	@Before
	public void setup() {
		securityManager = new SecurityManager(super.manageClient);
	}

	@Test
	public void rotateConfigEncryptionKey() {
		assertEquals(200, securityManager.rotateConfigEncryptionKey().getStatusCodeValue());
	}

	@Test
	public void rotateDateEncryptionKey() {
		assertEquals(200, securityManager.rotateDateEncryptionKey().getStatusCodeValue());
	}

	@Test
	public void rotateLogsEncryptionKey() {
		assertEquals(200, securityManager.rotateLogsEncryptionKey().getStatusCodeValue());
	}

}
