package com.marklogic.appdeployer;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.SecurityContextType;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class DefaultAppConfigFactoryTest extends Assert {

	private DefaultAppConfigFactory sut;

	/**
	 * Added in 3.12.0 to verify that if a projectDir is set on the factory, it's correctly applied to path-related
	 * properties.
	 */
	@Test
	public void withProjectDir() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource(
			"mlConfigPaths", "path1,path2",
			"mlModulePaths", "modulesPath1,modulesPath2",
			"mlSchemasPath", "schemasPath"));

		final String testPath = new File("").getAbsolutePath();

		AppConfig appConfig = sut.newAppConfig();
		assertEquals("modulesPath1", appConfig.getModulePaths().get(0));
		assertEquals("modulesPath2", appConfig.getModulePaths().get(1));
		assertEquals(testPath + "/path1", appConfig.getConfigDirs().get(0).getBaseDir().getAbsolutePath());
		assertEquals(testPath + "/path2", appConfig.getConfigDirs().get(1).getBaseDir().getAbsolutePath());
		assertEquals("schemasPath", appConfig.getSchemaPaths().get(0));

		File projectDir = new File("src/test/resources/sample-app");
		final String projectPath = projectDir.getAbsolutePath();
		sut.setProjectDir(projectDir);
		appConfig = sut.newAppConfig();
		assertEquals(projectPath + "/modulesPath1", appConfig.getModulePaths().get(0));
		assertEquals(projectPath + "/modulesPath2", appConfig.getModulePaths().get(1));
		assertEquals(projectPath + "/path1", appConfig.getConfigDirs().get(0).getBaseDir().getAbsolutePath());
		assertEquals(projectPath + "/path2", appConfig.getConfigDirs().get(1).getBaseDir().getAbsolutePath());
		assertEquals(projectPath + "/schemasPath", appConfig.getSchemaPaths().get(0));
	}

	/**
	 * Verifies that when no properties are set for paths, the default paths used in AppConfig still honor the
	 * projectDir.
	 */
	@Test
	public void withProjectDirAndDefaultPaths() {
		sut = new DefaultAppConfigFactory();

		final String testPath = new File("").getAbsolutePath();

		AppConfig appConfig = sut.newAppConfig();
		assertEquals("src/main/ml-modules", appConfig.getModulePaths().get(0));
		assertEquals(testPath + "/src/main/ml-config", appConfig.getConfigDirs().get(0).getBaseDir().getAbsolutePath());
		assertEquals("src/main/ml-schemas", appConfig.getSchemaPaths().get(0));

		File projectDir = new File("src/test/resources/sample-app");
		final String projectPath = projectDir.getAbsolutePath();
		sut.setProjectDir(projectDir);
		appConfig = sut.newAppConfig();
		assertEquals(projectPath + "/src/main/ml-modules", appConfig.getModulePaths().get(0));
		assertEquals(projectPath + "/src/main/ml-config", appConfig.getConfigDirs().get(0).getBaseDir().getAbsolutePath());
		assertEquals(projectPath + "/src/main/ml-schemas", appConfig.getSchemaPaths().get(0));
	}

	@Test
	public void trimProperties() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlHost", "  has-spaces   ", "mlUsername", "has spaces"));
		AppConfig config = sut.newAppConfig();
		assertEquals("has-spaces", config.getHost());
		assertEquals("has spaces", config.getRestAdminUsername());
	}

	@Test
	public void gradleStyleProperties() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlHost", "somehost", "mlUsername", "someuser"));
		AppConfig config = sut.newAppConfig();
		assertEquals("somehost", config.getHost());
		assertEquals("someuser", config.getRestAdminUsername());
	}

	@Test
	public void springStyleProperties() {
		sut = new DefaultAppConfigFactory(
			new SimplePropertySource("marklogic.mlHost", "springhost", "marklogic.mlUsername", "springuser"));
		AppConfig config = sut.newAppConfig();
		assertEquals("springhost", config.getHost());
		assertEquals("springuser", config.getRestAdminUsername());
	}

	@Test
	public void moduleTimestampsPath() {
		Properties p = new Properties();
		p.setProperty("mlModuleTimestampsPath", "");
		AppConfig config = new DefaultAppConfigFactory(new SimplePropertySource(p)).newAppConfig();
		assertNull(config.getModuleTimestampsPath());
	}

	@Test
	public void unrecognizedProperties() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("foo.mlHost", "host", "foo.mlUsername", "user"));
		AppConfig config = sut.newAppConfig();
		assertEquals("Should use default", "localhost", config.getHost());
		assertEquals("Should use default", AppConfig.DEFAULT_USERNAME, config.getRestAdminUsername());
	}

	@Test
	public void appServicesDefaultsToDefaultUsernamePassword() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlUsername", "someuser", "mlPassword", "somepassword"));
		AppConfig config = sut.newAppConfig();
		assertEquals("someuser", config.getRestAdminUsername());
		assertEquals("somepassword", config.getRestAdminPassword());
		assertEquals("someuser", config.getAppServicesUsername());
		assertEquals("somepassword", config.getAppServicesPassword());
	}

	@Test
	public void appServicesDefaultsToRestAdminUsernamePassword() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlRestAdminUsername", "someuser", "mlRestAdminPassword", "somepassword"));
		AppConfig config = sut.newAppConfig();
		assertEquals("someuser", config.getRestAdminUsername());
		assertEquals("somepassword", config.getRestAdminPassword());
		assertEquals("someuser", config.getAppServicesUsername());
		assertEquals("somepassword", config.getAppServicesPassword());
	}

	@Test
	public void appServicesDiffersFromRestAdmin() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource(
			"mlRestAdminUsername", "someuser", "mlRestAdminPassword", "somepassword",
			"mlAppServicesUsername", "appuser", "mlAppServicesPassword", "appword"));
		AppConfig config = sut.newAppConfig();
		assertEquals("someuser", config.getRestAdminUsername());
		assertEquals("somepassword", config.getRestAdminPassword());
		assertEquals("appuser", config.getAppServicesUsername());
		assertEquals("appword", config.getAppServicesPassword());
	}

	@Test
	public void cpfDatabaseName() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlAppName", "test"));
		AppConfig config = sut.newAppConfig();
		assertEquals("test-triggers", config.getTriggersDatabaseName());
		assertEquals("CPF database should default to the triggers database when not specified",
			"test-triggers", config.getCpfDatabaseName());

		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlAppName", "test", "mlCpfDatabaseName", "my-cpf-db"));
		config = sut.newAppConfig();
		assertEquals("test-triggers", config.getTriggersDatabaseName());
		assertEquals("my-cpf-db", config.getCpfDatabaseName());
	}

	@Test
	public void dataConfigProperties() {
		Properties p = new Properties();
		p.setProperty("mlDataPaths", "src/main/ml-data,src/main/more-data");
		p.setProperty("mlDataCollections", "apple,banana");
		p.setProperty("mlDataBatchSize", "123");
		p.setProperty("mlDataDatabaseName", "Documents");
		p.setProperty("mlDataPermissions", "manage-user,read,manage-user,update");
		p.setProperty("mlDataReplaceTokens", "false");
		p.setProperty("mlDataLoadingEnabled", "false");
		p.setProperty("mlDataLogUris", "false");

		sut = new DefaultAppConfigFactory(new SimplePropertySource(p));
		final File projectDir = new File("src/test/resources/sample-app");
		sut.setProjectDir(projectDir);

		DataConfig config = sut.newAppConfig().getDataConfig();
		assertEquals(new File(projectDir, "src/main/ml-data").getAbsolutePath(), config.getDataPaths().get(0));
		assertEquals(new File(projectDir, "src/main/more-data").getAbsolutePath(), config.getDataPaths().get(1));
		assertEquals("apple", config.getCollections()[0]);
		assertEquals("banana", config.getCollections()[1]);
		assertEquals(new Integer(123), config.getBatchSize());
		assertEquals("Documents", config.getDatabaseName());
		assertEquals("manage-user,read,manage-user,update", config.getPermissions());
		assertFalse(config.isReplaceTokensInData());
		assertFalse(config.isDataLoadingEnabled());
		assertFalse(config.isLogUris());
	}

	@Test
	public void pluginConfigProperties() {
		Properties p = new Properties();
		p.setProperty("mlPluginPaths", "src/main/ml-plugins,src/main/more-plugins");
		p.setProperty("mlPluginInstallationEnabled", "false");
		p.setProperty("mlPluginDatabaseName", "Documents");
		p.setProperty("mlPluginUriPrefix", "/some/prefix/");

		sut = new DefaultAppConfigFactory(new SimplePropertySource(p));
		final File projectDir = new File("src/test/resources/plugin-project");
		sut.setProjectDir(projectDir);

		PluginConfig config = sut.newAppConfig().getPluginConfig();
		assertEquals(new File(projectDir, "src/main/ml-plugins").getAbsolutePath(), config.getPluginPaths().get(0));
		assertEquals(new File(projectDir, "src/main/more-plugins").getAbsolutePath(), config.getPluginPaths().get(1));
		assertFalse(config.isEnabled());
		assertEquals("Documents", config.getDatabaseName());
		assertEquals("/some/prefix/", config.getUriPrefix());
	}

	@Test
	public void deployWithCma() {
		Properties p = new Properties();

		CmaConfig cmaConfig = new DefaultAppConfigFactory(new SimplePropertySource(p)).newAppConfig().getCmaConfig();
		assertFalse(cmaConfig.isCombineRequests());
		assertFalse(cmaConfig.isDeployAmps());
		assertFalse(cmaConfig.isDeployDatabases());
		assertFalse(cmaConfig.isDeployForests());
		assertFalse(cmaConfig.isDeployPrivileges());
		assertFalse(cmaConfig.isDeployRoles());
		assertFalse(cmaConfig.isDeployServers());
		assertFalse(cmaConfig.isDeployUsers());

		p.setProperty("mlDeployWithCma", "true");
		cmaConfig = new DefaultAppConfigFactory(new SimplePropertySource(p)).newAppConfig().getCmaConfig();
		assertTrue(cmaConfig.isCombineRequests());
		assertTrue(cmaConfig.isDeployAmps());
		assertTrue(cmaConfig.isDeployDatabases());
		assertTrue(cmaConfig.isDeployForests());
		assertTrue(cmaConfig.isDeployPrivileges());
		assertTrue(cmaConfig.isDeployRoles());
		assertTrue(cmaConfig.isDeployServers());
		assertTrue(cmaConfig.isDeployUsers());
	}

	@Test
	public void mostProperties() {
		Properties p = new Properties();

		p.setProperty("mlMergeResources", "false");
		p.setProperty("mlAddHostNameTokens", "true");
		p.setProperty("mlCatchDeployExceptions", "true");
		p.setProperty("mlCatchUndeployExceptions", "true");

		p.setProperty("mlCombineCmaRequests", "true");
		p.setProperty("mlDeployAmpsWithCma", "true");
		p.setProperty("mlDeployDatabasesWithCma", "true");
		p.setProperty("mlDeployForestsWithCma", "true");
		p.setProperty("mlDeployPrivilegesWithCma", "true");
		p.setProperty("mlDeployRolesWithCma", "true");
		p.setProperty("mlDeployServersWithCma", "true");
		p.setProperty("mlDeployUsersWithCma", "true");

		p.setProperty("mlHost", "prophost");
		p.setProperty("mlAppName", "propname");
		p.setProperty("mlNoRestServer", "true");
		p.setProperty("mlUsername", "propuser1");
		p.setProperty("mlPassword", "proppassword");

		p.setProperty("mlRestConnectionType", DatabaseClient.ConnectionType.DIRECT.name());
		p.setProperty("mlRestPort", "4321");
		p.setProperty("mlTestRestPort", "8765");
		p.setProperty("mlRestAdminUsername", "propuser2");
		p.setProperty("mlRestAdminPassword", "proppassword2");
		p.setProperty("mlRestAuthentication", "certiFicate");
		p.setProperty("mlRestCertFile", "restCertFile");
		p.setProperty("mlRestCertPassword", "restCertPassword");
		p.setProperty("mlRestExternalName", "restExternalName");

		p.setProperty("mlAppServicesConnectionType", DatabaseClient.ConnectionType.GATEWAY.name());
		p.setProperty("mlAppServicesUsername", "appServicesUsername");
		p.setProperty("mlAppServicesPassword", "appServicesPassword");
		p.setProperty("mlAppServicesPort", "8123");
		p.setProperty("mlAppServicesAuthentication", "kerBEROS");
		p.setProperty("mlAppServicesCertFile", "appServicesCertFile");
		p.setProperty("mlAppServicesCertPassword", "appServicesCertPassword");
		p.setProperty("mlAppServicesExternalName", "appServicesExternalName");
		p.setProperty("mlAppServicesSimpleSsl", "true");

		p.setProperty("mlRestServerName", "my-rest-server");
		p.setProperty("mlTestRestServerName", "my-test-rest-server");

		p.setProperty("mlContentForestsPerHost", "17");
		p.setProperty("mlCreateForests", "false");
		p.setProperty("mlForestsPerHost", "some-db,2,other-db,3");
		p.setProperty("mlModulePermissions", "some-perm,read,some-perm,update");
		p.setProperty("mlModulesRegex", "some-pattern");
		p.setProperty("mlAdditionalBinaryExtensions", ".gradle,.properties");
		p.setProperty("mlConfigPaths", "src/test/resources/sample-app/custom-forests,src/test/resources/sample-app/alert-config");
		p.setProperty("mlSimpleSsl", "true");
		p.setProperty("mlContentDatabaseName", "my-content-db");
		p.setProperty("mlTestContentDatabaseName", "my-test-db");
		p.setProperty("mlModulesDatabaseName", "my-modules");
		p.setProperty("mlSchemasDatabaseName", "my-schemas-db");
		p.setProperty("mlTriggersDatabaseName", "my-triggers-db");
		p.setProperty("mlSchemaPaths", "/my/schemas,/my/other/schemas");
		p.setProperty("mlTdeValidationEnabled", "false");
		p.setProperty("mlDeleteForests", "false");
		p.setProperty("mlDeleteReplicas", "false");
		p.setProperty("mlGroupName", "other-group");
		p.setProperty("mlReplaceTokensInModules", "false");
		p.setProperty("mlUseRoxyTokenPrefix", "false");
		p.setProperty("mlModulePaths", "path1,path2,path3");
		p.setProperty("mlModuleTimestampsPath", "custom/timestamps/path.properties");
		p.setProperty("mlModuleTimestampsUseHost", "false");
		p.setProperty("mlDeleteTestModules", "true");
		p.setProperty("mlDeleteTestModulesPattern", "/some/pattern");
		p.setProperty("mlModulesLoaderThreadCount", "3");
		p.setProperty("mlModulesLoaderBatchSize", "79");

		p.setProperty("mlModelsPath", "ml/models");
		p.setProperty("mlModelsDatabase", "my-models-database");
		p.setProperty("mlInstanceConverterPath", "ext/my/path");
		p.setProperty("mlGenerateInstanceConverter", "false");
		p.setProperty("mlGenerateDatabaseProperties", "false");
		p.setProperty("mlGenerateSchema", "false");
		p.setProperty("mlGenerateSearchOptions", "false");
		p.setProperty("mlGenerateExtractionTemplate", "false");

		p.setProperty("mlResourceFilenamesToIgnore", "role1.json,role2.xml");
		p.setProperty("mlResourceFilenamesToExcludeRegex", "dev-.*");
		p.setProperty("mlResourceFilenamesToIncludeRegex", "qa-.*");

		p.setProperty("mlDatabaseNamesAndReplicaCounts", "Documents,1,Security,2");
		p.setProperty("mlDatabasesWithForestsOnOneHost", "Documents,Security");
		p.setProperty("mlDatabaseHosts", "Documents,host1|host2|host3,Security,host1|host2");
		p.setProperty("mlDatabaseGroups", "Documents,group1|group2,Security,group1");

		p.setProperty("mlForestDataDirectory", "/data/path");
		p.setProperty("mlForestFastDataDirectory", "/fast/path");
		p.setProperty("mlForestLargeDataDirectory", "/large/path");

		p.setProperty("mlReplicaForestDataDirectory", "/var/data");
		p.setProperty("mlReplicaForestFastDataDirectory", "/var/fast");
		p.setProperty("mlReplicaForestLargeDataDirectory", "/var/large");

		p.setProperty("mlDatabaseDataDirectories", "Documents,/data/documents,Security,/data/security");
		p.setProperty("mlDatabaseFastDataDirectories", "Documents,/fast/documents,Security,/fast/security");
		p.setProperty("mlDatabaseLargeDataDirectories", "Documents,/large/documents,Security,/large/security");

		p.setProperty("mlDatabaseReplicaDataDirectories", "Documents,/data/replicas,Security,/data/security/replicas");
		p.setProperty("mlDatabaseReplicaFastDataDirectories", "Documents,/fast/replicas,Security,/fast/security/replicas");
		p.setProperty("mlDatabaseReplicaLargeDataDirectories", "Documents,/large/replicas,Security,/large/security/replicas");

		p.setProperty("mlHostGroups", "host1,Default,host2,other-group");

		p.setProperty("mlUpdateMimetypeWhenPropertiesAreEqual", "true");

		sut = new DefaultAppConfigFactory(new SimplePropertySource(p));
		AppConfig config = sut.newAppConfig();

		assertFalse(config.isMergeResources());
		assertTrue(config.isAddHostNameTokens());
		assertTrue(config.isCatchDeployExceptions());
		assertTrue(config.isCatchUndeployExceptions());

		assertTrue(config.getCmaConfig().isCombineRequests());
		assertTrue(config.getCmaConfig().isDeployAmps());
		assertTrue(config.getCmaConfig().isDeployDatabases());
		assertTrue(config.getCmaConfig().isDeployForests());
		assertTrue(config.getCmaConfig().isDeployPrivileges());
		assertTrue(config.getCmaConfig().isDeployRoles());
		assertTrue(config.getCmaConfig().isDeployServers());
		assertTrue(config.getCmaConfig().isDeployUsers());

		assertEquals("prophost", config.getHost());
		assertEquals("propname", config.getName());
		assertTrue(config.isNoRestServer());

		// REST server connection properties
		assertEquals(DatabaseClient.ConnectionType.DIRECT, config.getRestConnectionType());
		assertEquals((Integer) 4321, config.getRestPort());
		assertEquals((Integer) 8765, config.getTestRestPort());
		assertEquals("propuser2", config.getRestAdminUsername());
		assertEquals("proppassword2", config.getRestAdminPassword());
		assertEquals(SecurityContextType.CERTIFICATE, config.getRestSecurityContextType());
		assertEquals("restCertFile", config.getRestCertFile());
		assertEquals("restCertPassword", config.getRestCertPassword());
		assertEquals("restExternalName", config.getRestExternalName());

		// App-Services server connection properties
		assertEquals(DatabaseClient.ConnectionType.GATEWAY, config.getAppServicesConnectionType());
		assertEquals("appServicesUsername", config.getAppServicesUsername());
		assertEquals("appServicesPassword", config.getAppServicesPassword());
		assertEquals((Integer) 8123, config.getAppServicesPort());
		assertEquals(SecurityContextType.KERBEROS, config.getAppServicesSecurityContextType());
		assertEquals("appServicesCertFile", config.getAppServicesCertFile());
		assertEquals("appServicesCertPassword", config.getAppServicesCertPassword());
		assertEquals("appServicesExternalName", config.getAppServicesExternalName());
		assertNotNull(config.getAppServicesSslContext());
		assertEquals(DatabaseClientFactory.SSLHostnameVerifier.ANY, config.getAppServicesSslHostnameVerifier());

		assertEquals("my-rest-server", config.getRestServerName());
		assertEquals("my-test-rest-server", config.getTestRestServerName());

		assertEquals((Integer) 17, config.getContentForestsPerHost());
		assertFalse(config.isCreateForests());
		Map<String, Integer> forestCounts = config.getForestCounts();
		assertEquals(2, (int) forestCounts.get("some-db"));
		assertEquals(3, (int) forestCounts.get("other-db"));
		assertEquals("some-perm,read,some-perm,update", config.getModulePermissions());
		assertEquals("some-pattern", config.getModuleFilenamesIncludePattern().pattern());
		String[] extensions = config.getAdditionalBinaryExtensions();
		assertEquals(".gradle", extensions[0]);
		assertEquals(".properties", extensions[1]);

		List<ConfigDir> configDirs = config.getConfigDirs();
		assertEquals(2, configDirs.size());
		assertTrue(configDirs.get(0).getBaseDir().getAbsolutePath().contains("custom-forests"));
		assertTrue(configDirs.get(1).getBaseDir().getAbsolutePath().contains("alert-config"));

		assertNotNull(config.getRestSslContext());
		assertNotNull(config.getRestSslHostnameVerifier());
		assertEquals("my-content-db", config.getContentDatabaseName());
		assertEquals("my-test-db", config.getTestContentDatabaseName());
		assertEquals("my-modules", config.getModulesDatabaseName());
		assertEquals("my-schemas-db", config.getSchemasDatabaseName());
		assertEquals("my-triggers-db", config.getTriggersDatabaseName());
		assertEquals("/my/schemas", config.getSchemaPaths().get(0));
		assertEquals("/my/other/schemas", config.getSchemaPaths().get(1));
		assertFalse(config.isTdeValidationEnabled());
		assertFalse(config.isDeleteForests());
		assertFalse(config.isDeleteReplicas());
		assertEquals("other-group", config.getGroupName());
		assertFalse(config.isReplaceTokensInModules());
		assertFalse(config.isUseRoxyTokenPrefix());
		assertTrue(config.isDeleteTestModules());
		assertEquals("/some/pattern", config.getDeleteTestModulesPattern());
		assertEquals(3, config.getModulesLoaderThreadCount());
		assertEquals(new Integer(79), config.getModulesLoaderBatchSize());

		assertEquals("ml/models", config.getModelsPath());
		assertEquals("my-models-database", config.getModelsDatabase());
		assertEquals("ext/my/path", config.getInstanceConverterPath());
		assertFalse(config.isGenerateDatabaseProperties());
		assertFalse(config.isGenerateExtractionTemplate());
		assertFalse(config.isGenerateInstanceConverter());
		assertFalse(config.isGenerateSchema());
		assertFalse(config.isGenerateSearchOptions());

		List<String> paths = config.getModulePaths();
		assertEquals("path1", paths.get(0));
		assertEquals("path2", paths.get(1));
		assertEquals("path3", paths.get(2));

		assertEquals("custom/timestamps/path.properties", config.getModuleTimestampsPath());
		assertFalse(config.isModuleTimestampsUseHost());

		assertEquals("role1.json", config.getResourceFilenamesToIgnore()[0]);
		assertEquals("role2.xml", config.getResourceFilenamesToIgnore()[1]);
		assertEquals("dev-.*", config.getResourceFilenamesExcludePattern().pattern());
		assertEquals("qa-.*", config.getResourceFilenamesIncludePattern().pattern());

		assertEquals(new Integer(1), config.getDatabaseNamesAndReplicaCounts().get("Documents"));
		assertEquals(new Integer(2), config.getDatabaseNamesAndReplicaCounts().get("Security"));

		Set<String> set = config.getDatabasesWithForestsOnOneHost();
		assertEquals(2, set.size());
		assertTrue(set.contains("Documents"));
		assertTrue(set.contains("Security"));

		Map<String, List<String>> databaseHosts = config.getDatabaseHosts();
		assertEquals(2, databaseHosts.size());
		assertEquals(3, databaseHosts.get("Documents").size());
		assertTrue(databaseHosts.get("Documents").contains("host1"));
		assertTrue(databaseHosts.get("Documents").contains("host2"));
		assertTrue(databaseHosts.get("Documents").contains("host3"));
		assertEquals(2, databaseHosts.get("Security").size());
		assertTrue(databaseHosts.get("Security").contains("host1"));
		assertTrue(databaseHosts.get("Security").contains("host2"));

		Map<String, List<String>> databaseGroups = config.getDatabaseGroups();
		assertEquals(2, databaseGroups.size());
		assertEquals(2, databaseGroups.get("Documents").size());
		assertTrue(databaseGroups.get("Documents").contains("group1"));
		assertTrue(databaseGroups.get("Documents").contains("group2"));
		assertEquals(1, databaseGroups.get("Security").size());
		assertTrue(databaseGroups.get("Security").contains("group1"));

		assertEquals("/data/path", config.getForestDataDirectory());
		assertEquals("/fast/path", config.getForestFastDataDirectory());
		assertEquals("/large/path", config.getForestLargeDataDirectory());

		assertEquals("/var/data", config.getReplicaForestDataDirectory());
		assertEquals("/var/fast", config.getReplicaForestFastDataDirectory());
		assertEquals("/var/large", config.getReplicaForestLargeDataDirectory());

		Map<String, List<String>> mapOfLists = config.getDatabaseDataDirectories();
		assertEquals("/data/documents", mapOfLists.get("Documents").get(0));
		assertEquals("/data/security", mapOfLists.get("Security").get(0));

		Map<String, String> map = config.getDatabaseFastDataDirectories();
		assertEquals("/fast/documents", map.get("Documents"));
		assertEquals("/fast/security", map.get("Security"));
		map = config.getDatabaseLargeDataDirectories();
		assertEquals("/large/documents", map.get("Documents"));
		assertEquals("/large/security", map.get("Security"));

		map = config.getDatabaseReplicaDataDirectories();
		assertEquals("/data/replicas", map.get("Documents"));
		assertEquals("/data/security/replicas", map.get("Security"));
		map = config.getDatabaseReplicaFastDataDirectories();
		assertEquals("/fast/replicas", map.get("Documents"));
		assertEquals("/fast/security/replicas", map.get("Security"));
		map = config.getDatabaseReplicaLargeDataDirectories();
		assertEquals("/large/replicas", map.get("Documents"));
		assertEquals("/large/security/replicas", map.get("Security"));

		map = config.getHostGroups();
		assertEquals("Default", map.get("host1"));
		assertEquals("other-group", map.get("host2"));

		assertTrue(config.isUpdateMimetypeWhenPropertiesAreEqual());
	}

	/**
	 * Verifies that mlConfigDir is still supported, though mlConfigPath is preferred.
	 */
	@Test
	public void mlConfigDir() {
		Properties p = new Properties();
		p.setProperty("mlConfigDir", "src/test/resources/sample-app/empty-ml-config");

		sut = new DefaultAppConfigFactory(new SimplePropertySource(p));
		AppConfig config = sut.newAppConfig();
		assertTrue(config.getFirstConfigDir().getBaseDir().getAbsolutePath().contains("empty-ml-config"));
	}

	@Test
	public void mlUsernameAndPassword() {
		sut = new DefaultAppConfigFactory(
			new SimplePropertySource("mlUsername", "customuser", "mlPassword", "custompassword"));
		AppConfig config = sut.newAppConfig();

		assertEquals("When mlRestAdminUsername is not set, mlUsername should be used", "customuser",
			config.getRestAdminUsername());
		assertEquals("When mlRestAdminPassword is not set, mlPassword should be used", "custompassword",
			config.getRestAdminPassword());

		assertNull("SSL context should be null by default", config.getRestSslContext());
		assertNull("SSL hostname verifier should be null by default", config.getRestSslHostnameVerifier());
	}

	@Test
	public void schemasAndTriggerDatabaseNamesShouldBeInSetOfDatabasesWithForestsOnOneHost() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlAppName", "example"));
		AppConfig config = sut.newAppConfig();
		assertEquals("example-triggers", config.getTriggersDatabaseName());
		assertEquals("example-schemas", config.getSchemasDatabaseName());

		Set<String> set = config.getDatabasesWithForestsOnOneHost();
		assertTrue(set.contains("example-triggers"));
		assertTrue(set.contains("example-schemas"));
	}

	@Test
	public void dontModifySetOfDatabasesWithForestsOnOneHostIfItsBeenConfigured() {
		sut = new DefaultAppConfigFactory(new SimplePropertySource("mlAppName", "example", "mlDatabasesWithForestsOnOneHost", "db1,db2"));
		AppConfig config = sut.newAppConfig();

		Set<String> set = config.getDatabasesWithForestsOnOneHost();
		assertEquals(2, set.size());
		assertTrue(set.contains("db1"));
		assertTrue(set.contains("db2"));

		final String message = "If the user has configured the set of databases, then the default schema and trigger databases names should not be added automatically";
		assertFalse(message, set.contains("example-triggers"));
		assertFalse(message, set.contains("example-schemas"));

	}
}
