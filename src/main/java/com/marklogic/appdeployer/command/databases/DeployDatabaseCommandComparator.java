package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.mapper.DefaultResourceMapper;
import com.marklogic.mgmt.mapper.ResourceMapper;

import java.util.Comparator;

/**
 * Used for sorting instances of DeployDatabaseCommand so that databases are created in a correct order - i.e. an order
 * in which we don't get errors from databases being created before their dependent databases are created.
 */
public class DeployDatabaseCommandComparator extends LoggingObject implements Comparator<DeployDatabaseCommand> {

	private CommandContext context;
	private boolean reverseOrder = false;
	private ResourceMapper resourceMapper;

	public DeployDatabaseCommandComparator(CommandContext context, boolean reverseOrder) {
		this.context = context;
		this.reverseOrder = reverseOrder;
		this.resourceMapper = new DefaultResourceMapper(new API(null));
	}

	@Override
	public int compare(DeployDatabaseCommand o1, DeployDatabaseCommand o2) {
		String p1 = o1.buildPayload(context);
		String p2 = o2.buildPayload(context);
		if (p1 == null || p2 == null) {
			return 0;
		}

		Database db1, db2;
		try {
			db1 = resourceMapper.readResource(p1, Database.class);
			db2 = resourceMapper.readResource(p2, Database.class);
		} catch (Exception ex) {
			logger.warn("Unable to map database payload to Database object, " +
				"will not be able to determine the right order in which to deploy this database; cause: " + ex.getMessage());
			return 0;
		}

		int result = db1.compareTo(db2);
		if (result == 0) {
			return 0;
		}
		if (reverseOrder) {
			return result == 1 ? -1 : 1;
		}
		return result;
	}
}
