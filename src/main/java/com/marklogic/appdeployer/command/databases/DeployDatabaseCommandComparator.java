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

		// These should never happen, but just in case
		if (db1 == null) {
			return reverseOrder ? 1 : -1;
		} else if (db2 == null) {
			return reverseOrder ? -1 : 1;
		}

		final String dbName1 = db1.getDatabaseName();
		final String dbName2 = db2.getDatabaseName();

		if (db1.dependsOnDatabase(db2)) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("%s depends on %s", dbName1, dbName2));
			}
			return reverseOrder ? -1 : 1;
		} else if (db2.dependsOnDatabase(db1)) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("%s depends on %s", dbName2, dbName1));
			}
			return reverseOrder ? 1 : -1;
		} else if (db1.hasDatabaseDependencies() && db2.hasDatabaseDependencies()) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("Both %s and %s have database dependencies, just not on each other", dbName1, dbName2));
			}
			return 0;
		} else if (db1.hasDatabaseDependencies()) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("%s has database dependencies and %s does not", dbName1, dbName2));
			}
			return reverseOrder ? -1 : 1;
		} else if (db2.hasDatabaseDependencies()) {
			if (logger.isDebugEnabled()) {
				logger.debug(format("%s has database dependencies and %s does not", dbName2, dbName1));
			}
			return reverseOrder ? 1 : -1;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(format("%s and %s do not have any dependencies", dbName1, dbName2));
		}
		return 0;
	}
}
