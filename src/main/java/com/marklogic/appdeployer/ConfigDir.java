/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.appdeployer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines all of the directories where configuration files can be found.
 *
 * TODO Eventually turn this into an interface.
 */
public class ConfigDir {

	public final static String DEFAULT_PATH = "src/main/ml-config";

	private File baseDir;

	private String databasesPath = "databases";
	private String defaultContentDatabaseFilename = "content-database.json";

	private String restApiPath = "rest-api.json";

	private File projectDir;

	public static ConfigDir withProjectDir(File projectDir) {
		File baseDir = projectDir != null ? new File(projectDir, DEFAULT_PATH) : new File(DEFAULT_PATH);
		ConfigDir configDir = new ConfigDir(baseDir);
		configDir.projectDir = projectDir;
		return configDir;
	}

	public ConfigDir() {
		new File(DEFAULT_PATH);
	}

	public ConfigDir(File baseDir) {
		setBaseDir(baseDir);
	}

	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	public File getDatabasesDir() {
		return new File(baseDir, databasesPath);
	}

	/**
	 * Return a list of every directory under the databases directory. Each such directory is considered to contain
	 * resources for the database with a name matching that of the directory.
	 *
	 * @return
	 */
	public List<File> getDatabaseResourceDirectories() {
		File dbDir = getDatabasesDir();
		if (dbDir != null && dbDir.exists()) {
			File[] dirs = dbDir.listFiles(pathname -> pathname.isDirectory());
			return Arrays.asList(dirs);
		}
		return new ArrayList<>();
	}

	public File getRestApiFile() {
		return new File(baseDir, restApiPath);
	}

	public File getRestApiServerFile() {
		return new File(getServersDir(), "rest-api-server.json");
	}

	public File getConfigurationsDir() {
		return new File(baseDir, "configurations");
	}

	public File getSecurityDir() {
		return new File(baseDir, "security");
	}

	public File getAmpsDir() {
		return new File(getSecurityDir(), "amps");
	}

	public File getCertificateAuthoritiesDir() {
		return new File(getSecurityDir(), "certificate-authorities");
	}

	public File getCertificateTemplatesDir() {
		return new File(getSecurityDir(), "certificate-templates");
	}

	public File getExternalSecuritiesDir() {
		return new File(getSecurityDir(), "external-security");
	}

	public File getPrivilegesDir() {
		return new File(getSecurityDir(), "privileges");
	}

	public File getProtectedCollectionsDir() {
		return new File(getSecurityDir(), "protected-collections");
	}

	public File getRolesDir() {
		return new File(getSecurityDir(), "roles");
	}

	public File getTriggersDir(String databaseName) {
		if (databaseName == null) {
			return getTriggersDir();
		}
		File dir = getDatabasesDir();
		File databaseDir = new File(dir, databaseName);
		return new File(databaseDir, "triggers");
	}

	public File getTriggersDir() {
		return new File(getBaseDir(), "triggers");
	}

	// This is expected to be relative to a database-specific directory
	public File getPartitionsDir() {
		return new File(getBaseDir(), "partitions");
	}

	// This is expected to be relative to a database-specific directory
	public File getPartitionQueriesDir() {
		return new File(getBaseDir(), "partition-queries");
	}

	public File getUsersDir() {
		return new File(getSecurityDir(), "users");
	}

	public File getProtectedPathsDir() { return new File(getSecurityDir(), "protected-paths"); }

	public File getQueryRolesetsDir() { return new File(getSecurityDir(), "query-rolesets"); }

	public File getServersDir() {
		return new File(baseDir, "servers");
	}

	public File getForestsDir() {
		return new File(baseDir, "forests");
	}

	public File getCpfDir() {
		return new File(baseDir, "cpf");
	}

	public File getDomainsDir() {
		return new File(getCpfDir(), "domains");
	}

	public File getPipelinesDir() {
		return new File(getCpfDir(), "pipelines");
	}

	public File getCpfConfigsDir() {
		return new File(getCpfDir(), "cpf-configs");
	}

	public File getClustersDir() {
		return new File(baseDir, "clusters");
	}

	public File getAlertDir() {
		return new File(baseDir, "alert");
	}

	public File getAlertConfigsDir() {
		return new File(getAlertDir(), "configs");
	}

	public File getFlexrepDir() {
		return new File(baseDir, "flexrep");
	}

	public File getFlexrepConfigsDir() {
		return new File(getFlexrepDir(), "configs");
	}

	public File getFlexrepPullsDir() {
		return new File(getFlexrepDir(), "pulls");
	}

	public File getGroupsDir() {
		return new File(baseDir, "groups");
	}

	public File getMimetypesDir() {
		return new File(baseDir, "mimetypes");
	}

	public File getViewSchemasDir() {
		return new File(baseDir, "view-schemas");
	}

	public File getTemporalDir() {
		return new File(baseDir, "temporal");
	}

	public File getTemporalAxesDir() {
		return new File(getTemporalDir(), "axes");
	}

	public File getTemporalCollectionsDir() {
		return new File(getTemporalDir(), "collections");
	}

	public File getTemporalCollectionsLsqtDir() {
		return new File(getTemporalCollectionsDir(), "lsqt");
	}

	public File getTasksDir() {
		return new File(baseDir, "tasks");
	}

	public File getTaskServersDir() {
		return new File(baseDir, "task-servers");
	}

	public void setDatabasesPath(String databasesPath) {
		this.databasesPath = databasesPath;
	}

	public void setRestApiPath(String restApiPath) {
		this.restApiPath = restApiPath;
	}

	public File getBaseDir() {
		return baseDir;
	}

	public String getDefaultContentDatabaseFilename() {
		return defaultContentDatabaseFilename;
	}

	public void setDefaultContentDatabaseFilename(String contentDatabaseFilename) {
		this.defaultContentDatabaseFilename = contentDatabaseFilename;
	}

	public File getProjectDir() {
		return projectDir;
	}

	public File getSecureCredentialsDir() {
		return new File(getSecurityDir(), "secure-credentials");
	}
}
