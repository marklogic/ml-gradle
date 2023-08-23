package com.marklogic.mgmt.resource.forests;

import com.marklogic.mgmt.AbstractMgmtTest;
import com.marklogic.mgmt.api.forest.Forest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetPrimaryForestsTest extends AbstractMgmtTest {

	/**
	 * Simple test for verifying this method works for a couple OOTB databases. It is expected that the app-deployer
	 * tests will stress this method given that CMA is used by a default, and thus this method will be used anytime
	 * a database is being deployed.
	 */
	@Test
	void test() {
		Map<String, List<Forest>> forestMap = new ForestManager(manageClient)
			.getPrimaryForestsForDatabases("App-Services", "Documents");

		Set<String> dbNames = forestMap.keySet();
		assertTrue(dbNames.contains("App-Services"));
		assertTrue(dbNames.contains("Documents"));
		assertEquals(2, dbNames.size());
		assertTrue(forestMap.get("App-Services").size() > 0);
		assertTrue(forestMap.get("Documents").size() > 0);
	}
}
