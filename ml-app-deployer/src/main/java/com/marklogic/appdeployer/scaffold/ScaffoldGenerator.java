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
package com.marklogic.appdeployer.scaffold;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.util.RestApiUtil;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Lots of protected methods in here to encourage subclassing and overriding behavior.
 */
public class ScaffoldGenerator extends LoggingObject {

	protected ObjectMapper objectMapper;
	private PrettyPrinter prettyPrinter = new DefaultPrettyPrinter();

	public void generateScaffold(String path, AppInputs appInputs) {
		if (objectMapper == null) {
			objectMapper = ObjectMapperFactory.getObjectMapper();
		}
		File rootDir = new File(path);

		File configDir = getConfigDir(rootDir);
		configDir.mkdirs();

		File modulesDir = getModulesDir(rootDir);
		modulesDir.mkdirs();

		generateDatabaseFiles(configDir);
		if (appInputs.isWithUsersAndRoles()) {
			generateSecurityFiles(configDir, appInputs.getAppName());
		}
		if (appInputs.isWithRestServer()) {
			generateRestApiFile(configDir);
			generateRestPropertiesFile(modulesDir);
			generateSearchOptions(modulesDir, appInputs.getAppName());
		}
	}

	/**
	 *
	 * @param path
	 * @param config
	 * @deprecated since 4.6.0; use the method using {@code ScaffoldInputs} instead.
	 */
	@Deprecated
	public void generateScaffold(String path, AppConfig config) {
		if (objectMapper == null) {
			objectMapper = ObjectMapperFactory.getObjectMapper();
		}
		File rootDir = new File(path);

		File configDir = getConfigDir(rootDir);
		configDir.mkdirs();

		File modulesDir = getModulesDir(rootDir);
		modulesDir.mkdirs();

		generateDatabaseFiles(configDir);
		generateSecurityFiles(configDir, config.getName());
		if (!config.isNoRestServer()) {
			generateRestApiFile(configDir);
			generateRestPropertiesFile(modulesDir);
			generateSearchOptions(modulesDir, config.getName());
		}

	}

	private void generateSearchOptions(File modulesDir, String appName) {
		File optionsDir = new File(modulesDir, "options");
		optionsDir.mkdirs();
		String xml = "<options xmlns='http://marklogic.com/appservices/search'>\n  <search-option>unfiltered</search-option>\n  <quality-weight>0</quality-weight>\n</options>";
		writeFile(xml.getBytes(), new File(optionsDir, appName + "-options.xml"));
	}

	protected void generateRestPropertiesFile(File modulesDir) {
		writeFile(buildRestPropertiesJson(), new File(modulesDir, "rest-properties.json"));
	}

	protected ObjectNode buildRestPropertiesJson() {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("debug", false);
		node.put("validate-queries", true);
		node.put("document-transform-all", false);
		node.put("validate-options", true);
		return node;
	}

	protected void generateSecurityFiles(File configDir, String appName) {
		File rolesDir = new File(configDir, "security/roles");
		rolesDir.mkdirs();
		writeFile(buildNobodyRole(appName), new File(rolesDir, "1-" + appName + "-nobody-role.json"));
		writeFile(buildReaderRole(appName), new File(rolesDir, "2-" + appName + "-reader-role.json"));
		writeFile(buildWriterRole(appName), new File(rolesDir, "3-" + appName + "-writer-role.json"));
		writeFile(buildInternalRole(appName), new File(rolesDir, "4-" + appName + "-internal-role.json"));
		writeFile(buildAdminRole(appName), new File(rolesDir, "5-" + appName + "-admin-role.json"));

		File usersDir = new File(configDir, "security/users");
		usersDir.mkdirs();
		writeFile(buildReaderUser(appName), new File(usersDir, appName + "-reader-user.json"));
		writeFile(buildWriterUser(appName), new File(usersDir, appName + "-writer-user.json"));
		writeFile(buildAdminUser(appName), new File(usersDir, appName + "-admin-user.json"));
	}

	protected ObjectNode buildNobodyRole(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", appName + "-nobody");
		node.put("description", "Unauthenticated user");
		node.putArray("role");
		return node;
	}

	protected ObjectNode buildReaderRole(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", appName + "-reader");
		node.put("description", "Can view documents, but not edit");
		ArrayNode array = node.putArray("role");
		array.add(appName + "-nobody");
		array = node.putArray("privilege");
		array.add(buildPrivilege("rest-reader", "http://marklogic.com/xdmp/privileges/rest-reader", "execute"));
		return node;
	}

	protected ObjectNode buildWriterRole(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", appName + "-writer");
		node.put("description", "Can read and write documents");
		ArrayNode array = node.putArray("role");
		array.add(appName + "-reader");
		array = node.putArray("privilege");
		array.add(buildPrivilege("rest-writer", "http://marklogic.com/xdmp/privileges/rest-writer", "execute"));
		array.add(buildPrivilege("any-uri", "http://marklogic.com/xdmp/privileges/any-uri", "execute"));
		array.add(buildPrivilege("unprotected-collections", "http://marklogic.com/xdmp/privileges/unprotected-collections", "execute"));
		return node;
	}

	protected ObjectNode buildInternalRole(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", appName + "-internal");
		node.put("description", "Internal role used for amping");
		ArrayNode array = node.putArray("role");
		array.add(appName + "-writer");
		return node;
	}

	protected ObjectNode buildAdminRole(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", appName + "-admin");
		node.put("description", "Non-admin administrator");
		ArrayNode array = node.putArray("role");
		array.add("rest-admin");
		array.add("manage-admin");
		array.add(appName + "-writer");
		array = node.putArray("privilege");
		array.add(buildPrivilege("any-uri", "http://marklogic.com/xdmp/privileges/any-uri", "execute"));
		array.add(buildPrivilege("xdbc:insert-in", "http://marklogic.com/xdmp/privileges/xdbc-insert-in", "execute"));
		array.add(buildPrivilege("xdmp:eval-in", "http://marklogic.com/xdmp/privileges/xdmp-eval-in", "execute"));
		return node;
	}

	protected ObjectNode buildPrivilege(String name, String action, String kind) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("privilege-name", name);
		node.put("action", action);
		node.put("kind", kind);
		return node;
	}

	protected ObjectNode buildReaderUser(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		String name = appName + "-reader";
		node.put("user-name", name);
		node.put("password", name);
		ArrayNode roles = node.putArray("role");
		roles.add(appName + "-reader");
		return node;
	}

	protected ObjectNode buildWriterUser(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		String name = appName + "-writer";
		node.put("user-name", name);
		node.put("password", name);
		ArrayNode roles = node.putArray("role");
		roles.add(appName + "-writer");
		return node;
	}

	protected ObjectNode buildAdminUser(String appName) {
		ObjectNode node = objectMapper.createObjectNode();
		String name = appName + "-admin";
		node.put("user-name", name);
		node.put("password", name);
		ArrayNode roles = node.putArray("role");
		roles.add(appName + "-admin");
		return node;
	}

	protected void generateRestApiFile(File configDir) {
		writeFile(buildRestApiJson().getBytes(), new File(configDir, "rest-api.json"));
	}

	protected String buildRestApiJson() {
		return RestApiUtil.buildDefaultRestApiJson();
	}

	protected void generateDatabaseFiles(File configDir) {
		File databasesDir = new File(configDir, "databases");
		databasesDir.mkdirs();

		writeFile(buildContentDatabaseJson(), new File(databasesDir, "content-database.json"));
		writeFile(buildSchemasDatabaseJson(), new File(databasesDir, "schemas-database.json"));
	}

	protected ObjectNode buildContentDatabaseJson() {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("database-name", "%%DATABASE%%");
		node.put("schema-database", "%%SCHEMAS_DATABASE%%");
		ArrayNode array = node.putArray("range-element-index");
		ObjectNode index = array.addObject();
		index.put("scalar-type", "string");
		index.put("namespace-uri", "CHANGEME");
		index.put("localname", "CHANGEME");
		index.put("collation", "http://marklogic.com/collation/");
		index.put("range-value-positions", false);
		index.put("invalid-values", "reject");
		return node;
	}

	protected ObjectNode buildSchemasDatabaseJson() {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("database-name", "%%SCHEMAS_DATABASE%%");
		return node;
	}

	protected void writeFile(ObjectNode node, File f) {
		try {
			byte[] bytes = objectMapper.writer(prettyPrinter).writeValueAsBytes(node);
			writeFile(bytes, f);
		} catch (JsonProcessingException je) {
			throw new RuntimeException("Unable to process JSON for file: " + f.getAbsolutePath() + "; cause: "
				+ je.getMessage(), je);
		}
	}

	protected void writeFile(byte[] bytes, File f) {
		if (f.exists()) {
			logger.info("Not writing file, as it already exists: " + f.getAbsolutePath());
		} else {
			try {
				logger.info("Writing: " + f.getAbsolutePath());
				FileCopyUtils.copy(bytes, f);
			} catch (IOException ie) {
				throw new RuntimeException("Unable to write file at: " + f.getAbsolutePath() + "; cause: "
					+ ie.getMessage(), ie);
			}
		}
	}

	protected File getConfigDir(File rootDir) {
		return new File(rootDir, "src/main/ml-config");
	}

	protected File getModulesDir(File rootDir) {
		return new File(rootDir, "src/main/ml-modules");
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setPrettyPrinter(PrettyPrinter prettyPrinter) {
		this.prettyPrinter = prettyPrinter;
	}

	public static class AppInputs {
		final private String appName;
		final private boolean withRestServer;
		final private boolean withUsersAndRoles;

		public AppInputs(String appName) {
			this(appName, true, true);
		}

		public AppInputs(String appName, boolean withRestServer, boolean withUsersAndRoles) {
			this.appName = appName;
			this.withRestServer = withRestServer;
			this.withUsersAndRoles = withUsersAndRoles;
		}

		public String getAppName() {
			return appName;
		}

		public boolean isWithRestServer() {
			return withRestServer;
		}

		public boolean isWithUsersAndRoles() {
			return withUsersAndRoles;
		}
	}
}
