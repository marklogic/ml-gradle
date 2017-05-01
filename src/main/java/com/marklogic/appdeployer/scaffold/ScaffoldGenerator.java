package com.marklogic.appdeployer.scaffold;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.util.RestApiUtil;
import com.marklogic.client.helper.LoggingObject;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Lots of protected methods in here to encourage subclassing and overriding behavior.
 */
public class ScaffoldGenerator extends LoggingObject {

	protected ObjectMapper objectMapper = new ObjectMapper();
	private PrettyPrinter prettyPrinter = new DefaultPrettyPrinter();

	public void generateScaffold(String path, AppConfig config) {
		File rootDir = new File(path);

		File configDir = getConfigDir(rootDir);
		configDir.mkdirs();

		File modulesDir = getModulesDir(rootDir);
		modulesDir.mkdirs();

		generateContentDatabaseFile(configDir, config);
		generateSecurityFiles(configDir, config);

		if (!config.isNoRestServer()) {
			generateRestApiFile(configDir, config);
			generateRestPropertiesFile(modulesDir, config);
			generateSearchOptions(modulesDir, config);
		}

	}

	private void generateSearchOptions(File modulesDir, AppConfig config) {
		File optionsDir = new File(modulesDir, "options");
		optionsDir.mkdirs();
		String xml = "<options xmlns='http://marklogic.com/appservices/search'>\n  <search-option>unfiltered</search-option>\n  <quality-weight>0</quality-weight>\n</options>";
		writeFile(xml.getBytes(), new File(optionsDir, config.getName() + "-options.xml"));
	}

	protected void generateRestPropertiesFile(File modulesDir, AppConfig config) {
		writeFile(buildRestPropertiesJson(config), new File(modulesDir, "rest-properties.json"));
	}

	protected ObjectNode buildRestPropertiesJson(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("debug", false);
		node.put("validate-queries", true);
		node.put("document-transform-all", false);
		node.put("validate-options", true);
		return node;
	}

	protected void generateSecurityFiles(File configDir, AppConfig config) {
		File rolesDir = new File(configDir, "security/roles");
		rolesDir.mkdirs();
		writeFile(buildNobodyRole(config), new File(rolesDir, "1-" + config.getName() + "-nobody-role.json"));
		writeFile(buildReaderRole(config), new File(rolesDir, "2-" + config.getName() + "-reader-role.json"));
		writeFile(buildWriterRole(config), new File(rolesDir, "3-" + config.getName() + "-writer-role.json"));
		writeFile(buildInternalRole(config), new File(rolesDir, "4-" + config.getName() + "-internal-role.json"));
		writeFile(buildAdminRole(config), new File(rolesDir, "5-" + config.getName() + "-admin-role.json"));

		File usersDir = new File(configDir, "security/users");
		usersDir.mkdirs();
		writeFile(buildReaderUser(config), new File(usersDir, config.getName() + "-reader-user.json"));
		writeFile(buildWriterUser(config), new File(usersDir, config.getName() + "-writer-user.json"));
		writeFile(buildAdminUser(config), new File(usersDir, config.getName() + "-admin-user.json"));
	}

	protected ObjectNode buildNobodyRole(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", config.getName() + "-nobody");
		node.put("description", "Unauthenticated user");
		node.putArray("role");
		return node;
	}

	protected ObjectNode buildReaderRole(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", config.getName() + "-reader");
		node.put("description", "Can view documents, but not edit");
		ArrayNode array = node.putArray("role");
		array.add("rest-reader");
		array.add(config.getName() + "-nobody");
		return node;
	}

	protected ObjectNode buildWriterRole(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", config.getName() + "-writer");
		node.put("description", "Can read and write documents");
		ArrayNode array = node.putArray("role");
		array.add("rest-writer");
		array.add(config.getName() + "-reader");
		array = node.putArray("privilege");
		array.add(buildPrivilege("any-uri", "http://marklogic.com/xdmp/privileges/any-uri", "execute"));
		array.add(buildPrivilege("unprotected-collections", "http://marklogic.com/xdmp/privileges/unprotected-collections", "execute"));
		return node;
	}

	protected ObjectNode buildInternalRole(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", config.getName() + "-internal");
		node.put("description", "Internal role used for amping");
		ArrayNode array = node.putArray("role");
		array.add(config.getName() + "-writer");
		return node;
	}

	protected ObjectNode buildAdminRole(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("role-name", config.getName() + "-admin");
		node.put("description", "Non-admin administrator");
		ArrayNode array = node.putArray("role");
		array.add("rest-admin");
		array.add("manage-admin");
		array.add(config.getName() + "-writer");
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

	protected ObjectNode buildReaderUser(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		String name = config.getName() + "-reader";
		node.put("user-name", name);
		node.put("password", name);
		ArrayNode roles = node.putArray("role");
		roles.add(config.getName() + "-reader");
		return node;
	}

	protected ObjectNode buildWriterUser(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		String name = config.getName() + "-writer";
		node.put("user-name", name);
		node.put("password", name);
		ArrayNode roles = node.putArray("role");
		roles.add(config.getName() + "-writer");
		return node;
	}

	protected ObjectNode buildAdminUser(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		String name = config.getName() + "-admin";
		node.put("user-name", name);
		node.put("password", name);
		ArrayNode roles = node.putArray("role");
		roles.add(config.getName() + "-admin");
		return node;
	}

	protected void generateRestApiFile(File configDir, AppConfig config) {
		writeFile(buildRestApiJson(config).getBytes(), new File(configDir, "rest-api.json"));
	}

	protected String buildRestApiJson(AppConfig config) {
		return RestApiUtil.buildDefaultRestApiJson();
	}

	protected void generateContentDatabaseFile(File configDir, AppConfig config) {
		File databasesDir = new File(configDir, "databases");
		databasesDir.mkdirs();

		writeFile(buildContentDatabaseJson(config), new File(databasesDir, "content-database.json"));
	}

	protected ObjectNode buildContentDatabaseJson(AppConfig config) {
		ObjectNode node = objectMapper.createObjectNode();
		node.put("database-name", "%%DATABASE%%");
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

}
