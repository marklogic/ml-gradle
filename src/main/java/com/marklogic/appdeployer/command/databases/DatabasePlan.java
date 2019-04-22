package com.marklogic.appdeployer.command.databases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.api.database.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures all the inputs needed for creating, updating, or deleting a database.
 */
public class DatabasePlan {

	private String databaseName;
	private List<File> files;
	private boolean mainContentDatabase;
	private boolean testContentDatabase;
	private String payload;
	private ObjectNode mergedObjectNode;
	private Database databaseForSorting;
	private DeployDatabaseCommand deployDatabaseCommand;

	public DatabasePlan(String databaseName, List<File> files) {
		this.databaseName = databaseName;
		this.files = files;
	}

	public DatabasePlan(String databaseName, File file, boolean mainContentDatabase) {
		this.databaseName = databaseName;
		addFile(file);
		this.mainContentDatabase = mainContentDatabase;
	}

	public void addFile(File f) {
		if (files == null) {
			files = new ArrayList<>();
		}
		if (!files.contains(f)) {
			files.add(f);
		}
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public File getLastFile() {
		if (files == null || files.isEmpty()) {
			return null;
		}
		return files.get(files.size() - 1);
	}

	public List<File> getFiles() {
		return files;
	}

	public boolean isMainContentDatabase() {
		return mainContentDatabase;
	}

	public void setMainContentDatabase(boolean mainContentDatabase) {
		this.mainContentDatabase = mainContentDatabase;
	}

	public boolean isTestContentDatabase() {
		return testContentDatabase;
	}

	public void setTestContentDatabase(boolean testContentDatabase) {
		this.testContentDatabase = testContentDatabase;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public ObjectNode getMergedObjectNode() {
		return mergedObjectNode;
	}

	public void setMergedObjectNode(ObjectNode mergedObjectNode) {
		this.mergedObjectNode = mergedObjectNode;
		this.payload = mergedObjectNode.toString();
	}

	public Database getDatabaseForSorting() {
		return databaseForSorting;
	}

	public void setDatabaseForSorting(Database databaseForSorting) {
		this.databaseForSorting = databaseForSorting;
	}

	public DeployDatabaseCommand getDeployDatabaseCommand() {
		return deployDatabaseCommand;
	}

	public void setDeployDatabaseCommand(DeployDatabaseCommand deployDatabaseCommand) {
		this.deployDatabaseCommand = deployDatabaseCommand;
	}

	@Override
	public String toString() {
		return "DatabasePlan{" +
			"databaseName='" + databaseName + '\'' +
			", files=" + files +
			", mainContentDatabase=" + mainContentDatabase +
			'}';
	}
}
