package com.marklogic.mgmt.api.forest;

import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UnmarshalForestTest  {

	@Test
	public void xmlSmokeTest() {
		String xml = "<forest-properties xmlns=\"http://marklogic.com/manage\">\n" +
			"  <forest-name>Documents</forest-name>\n" +
			"  <host>localhost</host>\n" +
			"  <enabled>true</enabled>\n" +
			"  <data-directory/>\n" +
			"  <large-data-directory/>\n" +
			"  <fast-data-directory/>\n" +
			"  <fast-data-max-size>0</fast-data-max-size>\n" +
			"  <updates-allowed>all</updates-allowed>\n" +
			"  <availability>online</availability>\n" +
			"  <rebalancer-enable>true</rebalancer-enable>\n" +
			"  <range/>\n" +
			"  <failover-enable>true</failover-enable>\n" +
			"  <failover-hosts/>\n" +
			"  <forest-backups/>\n" +
			"  <forest-replicas/>\n" +
			"  <database-replication/>\n" +
			"  <database>Documents</database>\n" +
			"</forest-properties>";

		Forest f = new DefaultResourceMapper().readResource(xml, Forest.class);
		assertEquals("Documents", f.getForestName());
		assertEquals("localhost", f.getHost());
	}
}
