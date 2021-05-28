package com.marklogic.mgmt;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.security.Role;
import org.junit.jupiter.api.Test;
import org.springframework.util.ClassUtils;

import static org.springframework.test.util.AssertionErrors.fail;

/**
 * This test is used for manual inspection of the errors that are logged. Any resource works here, roles are just easy
 * to test with.
 */
public class InvalidResourcePayloadTest extends AbstractMgmtTest {

	@Test
	public void createNewRole() {
		Role role = new Role(new API(manageClient), ClassUtils.getShortName(getClass()) + "-test");
		role.addRole("INVALID_ROLE_THAT_SHOULD_CAUSE_A_FAILURE");

		try {
			role.save();
			fail("The role should have failed to save, which should have resulted in the request body being logged at the ERROR level");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}

	@Test
	public void updateRole() {
		Role role = new Role(new API(manageClient), "rest-reader");
		role.addRole("INVALID_ROLE_THAT_SHOULD_CAUSE_A_FAILURE");

		try {
			role.save();
			fail("The role should have failed to save, which should have resulted in the request body being logged at the ERROR level");
		} catch (Exception ex) {
			logger.info("Caught expected exception: " + ex.getMessage());
		}
	}
}
