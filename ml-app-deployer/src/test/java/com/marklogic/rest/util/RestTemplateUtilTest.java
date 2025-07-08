/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.rest.util;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Many of these tests are just smoke tests that can be used to inspect logging as well. Ideally, they can soon depend
 * on the Manage server being configured to use SSL. Although testing the use of the default keystore would likely
 * still be out of scope due to the difficulty of using a certificate signed by an authority that's trusted by the
 * default keystore.
 */
class RestTemplateUtilTest {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtilTest.class);

	private ManageConfig manageConfig = new ManageConfig();

	@BeforeEach
	void setup() {
		manageConfig.setUsername("someuser");
		manageConfig.setPassword("someword");
	}

	@Test
	public void configureSimpleSsl() {
		manageConfig.setConfigureSimpleSsl(true);
		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void simpleSslWithCustomProtocol() {
		manageConfig.setConfigureSimpleSsl(true);
		manageConfig.setSslProtocol("SSLv3");
		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void simpleSslWithInvalidProtocol() {
		manageConfig.setConfigureSimpleSsl(true);
		manageConfig.setSslProtocol("invalid");
		ManageClient client = new ManageClient(manageConfig);
		try {
			client.getRestTemplate();
			fail("Expected failure due to invalid protocol");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void useDefaultKeystore() {
		manageConfig.setUseDefaultKeystore(true);
		new ManageClient(manageConfig).getRestTemplate();
	}

	@Test
	public void defaultKeystoreWithInvalidProtocol() {
		manageConfig.setUseDefaultKeystore(true);
		manageConfig.setSslProtocol("invalid");
		ManageClient client = new ManageClient(manageConfig);
		try {
			client.getRestTemplate();
			fail("Expected failure due to invalid protocol");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void defaultKeystoreWithInvalidAlgorithm() {
		manageConfig.setUseDefaultKeystore(true);
		manageConfig.setTrustManagementAlgorithm("invalid");
		ManageClient client = new ManageClient(manageConfig);
		try {
			client.getRestTemplate();
			fail("Expected failure due to invalid algorithm");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	void noUsername() {
		manageConfig.setUsername(null);
		RuntimeException ex = assertThrows(RuntimeException.class,
			() -> RestTemplateUtil.newRestTemplate(manageConfig));

		assertEquals("Unable to connect to the MarkLogic app server at http://localhost:8002; cause: " +
				"Must specify a username when using digest authentication.",
			ex.getMessage(),
			"As of 4.5.0, since auth strategies other than basic/digest are now supported, the error message is expected " +
				"to identify which MarkLogic app server is being accessed but not any authentication details. This is " +
				"due to a change to toString of RestConfig/ManageConfig so that a username is not logged.");
	}
}
