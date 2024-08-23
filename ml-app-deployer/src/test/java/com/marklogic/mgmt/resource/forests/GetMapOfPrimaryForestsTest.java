package com.marklogic.mgmt.resource.forests;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.mgmt.api.forest.Forest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetMapOfPrimaryForestsTest extends AbstractMgmtTest {

	/**
	 * Simple test for verifying this method works for a couple OOTB databases. It is expected that the app-deployer
	 * tests will stress this method given that CMA is used by a default, and thus this method will be used anytime
	 * a database is being deployed.
	 */
	@Test
	void test() {
		Map<String, List<Forest>> mapOfPrimaryForests = new ForestManager(manageClient).getMapOfPrimaryForests();

		Set<String> dbNames = mapOfPrimaryForests.keySet();
		assertTrue(dbNames.contains("App-Services"));
		assertTrue(dbNames.contains("Documents"));
		assertTrue(mapOfPrimaryForests.get("App-Services").size() > 0);
		assertTrue(mapOfPrimaryForests.get("Documents").size() > 0);
	}
}
