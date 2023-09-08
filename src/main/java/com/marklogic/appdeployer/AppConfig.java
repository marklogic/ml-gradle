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

import com.marklogic.appdeployer.command.forests.ForestNamingStrategy;
import com.marklogic.appdeployer.command.forests.ReplicaBuilderStrategy;
import com.marklogic.appdeployer.util.MapPropertiesSource;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.ext.ConfiguredDatabaseClientFactory;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.DefaultConfiguredDatabaseClientFactory;
import com.marklogic.client.ext.SecurityContextType;
import com.marklogic.client.ext.modulesloader.impl.PropertiesModuleManager;
import com.marklogic.client.ext.modulesloader.ssl.SimpleX509TrustManager;
import com.marklogic.client.ext.ssl.SslUtil;
import com.marklogic.client.ext.tokenreplacer.DefaultTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.PropertiesSource;
import com.marklogic.client.ext.tokenreplacer.RoxyTokenReplacer;
import com.marklogic.client.ext.tokenreplacer.TokenReplacer;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Encapsulates common configuration properties for an application deployed to MarkLogic. These properties include not
 * just names of application resources - such as app servers and databases - but also connection information for loading
 * modules into an application as well as paths for modules and configuration files.
 *
 * An instance of this class is passed in as the main argument to the methods in the {@code AppDeployer} interface,
 * meaning that you're free to not just configure this as needed but also subclass it and add anything that you would
 * like.
 *
 * Additionally, the additionalProperties Map can used for storing any additional properties that a client of this class
 * may use, without having to define them as attributes of this class.
 */
public class AppConfig {

	/**
	 * This is set purely for development purposes so that an app can be created without specifying an app name.
	 */
	public static final String DEFAULT_APP_NAME = "my-app";

	/**
	 * This is set purely for development purposes so that an app can be configured without specifying a port. The
	 * v1/rest-apis endpoint will select an open port if none is provided, but some work is then required to figure out
	 * what that port is before modules are loaded.
	 */
	public static final Integer DEFAULT_PORT = 8003;

	/**
	 * The default path from which modules will be loaded into a modules database.
	 */
	public final static String DEFAULT_MODULES_PATH = "src/main/ml-modules";
	public final static String DEFAULT_SCHEMAS_PATH = "src/main/ml-schemas";

	public final static String DEFAULT_HOST = "localhost";
	public final static String DEFAULT_GROUP = "Default";

	private String name = DEFAULT_APP_NAME;

	/**
	 * @deprecated since 4.5.0; will be removed in 5.0.0
	 */
	@Deprecated
	public static final String DEFAULT_USERNAME = "admin";

	/**
	 * @deprecated since 4.5.0; will be removed in 5.0.0
	 */
	@Deprecated
	public static final String DEFAULT_PASSWORD = "admin";

	private String host = DEFAULT_HOST;
	private String cloudApiKey;

	private boolean catchDeployExceptions = false;
	private boolean catchUndeployExceptions = false;

	private CmaConfig cmaConfig;
	private boolean mergeResources = true;

	private boolean addHostNameTokens = false;

	// Used to construct DatabaseClient instances based on inputs defined in this class
	private ConfiguredDatabaseClientFactory configuredDatabaseClientFactory = new DefaultConfiguredDatabaseClientFactory();

	// Connection info for using the client REST API - e.g. to load modules
	private DatabaseClient.ConnectionType restConnectionType;
	private SecurityContextType restSecurityContextType = SecurityContextType.DIGEST;
	private String restAdminUsername;
	private String restAdminPassword;
	private SSLContext restSslContext;
	private SSLHostnameVerifier restSslHostnameVerifier;
	private String restCertFile;
	private String restCertPassword;
	private String restExternalName;
	private String restSamlToken;
	private X509TrustManager restTrustManager;
	private boolean restUseDefaultKeystore;
	private String restSslProtocol;
	private String restTrustManagementAlgorithm;
	private String restBasePath;

	private Integer restPort = DEFAULT_PORT;
	private Integer testRestPort;
	private String testRestBasePath;

	// Connection info for using the App Services client REST API - e.g. to load non-REST API modules
	private DatabaseClient.ConnectionType appServicesConnectionType;
	private SecurityContextType appServicesSecurityContextType = SecurityContextType.DIGEST;
	private String appServicesUsername;
	private String appServicesPassword;
	private Integer appServicesPort = 8000;
	private SSLContext appServicesSslContext;
	private SSLHostnameVerifier appServicesSslHostnameVerifier;
	private String appServicesCertFile;
	private String appServicesCertPassword;
	private String appServicesExternalName;
	private String appServicesSamlToken;
	private X509TrustManager appServicesTrustManager;
	private boolean appServicesUseDefaultKeystore;
	private String appServicesSslProtocol;
	private String appServicesTrustManagementAlgorithm;
	private String appServicesBasePath;

	// These can all be set to override the default names that are generated off of the "name" attribute.
	private String groupName = DEFAULT_GROUP;
	private boolean noRestServer = false;
	private String restServerName;
	private String testRestServerName;
	private String contentDatabaseName;
	private String testContentDatabaseName;
	private String modulesDatabaseName;
	private String triggersDatabaseName;
	private String cpfDatabaseName;
	private String schemasDatabaseName;

	// Since 4.6.0; affects loading of modules, schemas, and data.
	// Disabled by default for backwards compatibility; will likely default to true in 5.0.0 release.
	private boolean cascadeCollections;
	private boolean cascadePermissions;

	private List<String> modulePaths;
	private boolean staticCheckAssets = false;
	private boolean staticCheckLibraryAssets = false;
	private boolean bulkLoadAssets = true;
	private String moduleTimestampsPath;
	private boolean moduleTimestampsUseHost = true;
	private boolean deleteTestModules = false;
	private String deleteTestModulesPattern = "/test/**";
	private int modulesLoaderThreadCount = 1;
	private Integer modulesLoaderBatchSize;
	private String moduleUriPrefix;
	// As defined by the REST API
	private String modulePermissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";

	private boolean incrementalDeploy = false;

	private List<String> schemaPaths;
	private boolean tdeValidationEnabled = true;

	private List<ConfigDir> configDirs;

	// Passed into the PayloadTokenReplacer that subclasses of AbstractCommand use
	private Map<String, String> customTokens = new HashMap<>();

	// Controls whether forests are created when a database is created
	private boolean createForests = true;

	// Controls whether forests are deleted when a database is deleted
	private boolean deleteForests = true;

	// Controls whether replicas are deleted or not when undeploying a database
	private boolean deleteReplicas = true;

	private boolean sortOtherDatabaseByDependencies = true;

	private FileFilter assetFileFilter;
	private FileFilter schemasFileFilter;

	// Additional module extensions that should be loaded as binaries into the modules database
	private String[] additionalBinaryExtensions;

	// Will override the number of forests that DeployContentDatabasesCommand creates
	private Integer contentForestsPerHost;

	// Comma-delimited string used for configuring forest replicas
	private Map<String, Integer> databaseNamesAndReplicaCounts;

	// Comma-delimited string of database names that should only have forests (most likely just one) created on one host
	private Set<String> databasesWithForestsOnOneHost;

	private Map<String, List<String>> databaseHosts;
	private Map<String, List<String>> databaseGroups;
	private Map<String, String> hostGroups;

	// Data/fast/large directories for default forests
	private String forestDataDirectory;
	private String forestFastDataDirectory;
	private String forestLargeDataDirectory;

	// Comma-delimited string of database names and data directories
	private Map<String, List<String>> databaseDataDirectories;
	private Map<String, String> databaseFastDataDirectories;
	private Map<String, String> databaseLargeDataDirectories;
	private Map<String, List<String>> databaseReplicaDataDirectories;
	private Map<String, String> databaseReplicaFastDataDirectories;
	private Map<String, String> databaseReplicaLargeDataDirectories;

	// Configures the data-directory for replica forests built dynamically
	private String replicaForestDataDirectory;
	private String replicaForestLargeDataDirectory;
	private String replicaForestFastDataDirectory;

	// Allows for customizing how forests are named per database
	private Map<String, ForestNamingStrategy> forestNamingStrategies = new HashMap<>();

	// Allows for customizing how replicas are built for all databases
	private ReplicaBuilderStrategy replicaBuilderStrategy;

	// Path to use for DeployFlexrepCommand
	private String flexrepPath;

	// Whether or not to replace tokens in modules
	private boolean replaceTokensInModules = true;
	// Whether or not to prefix each module token with "@ml."
	private boolean useRoxyTokenPrefix = false;

	private Pattern moduleFilenamesIncludePattern;

	private Map<String, Integer> forestCounts = new HashMap<>();

	// Entity Services properties
	private String modelsPath = "data/entity-services";
	private String instanceConverterPath = "ext/entity-services";

	private boolean generateInstanceConverter = true;
	private boolean generateSchema = true;
	private boolean generateDatabaseProperties = true;
	private boolean generateExtractionTemplate = true;
	private boolean generateSearchOptions = true;
	private String modelsDatabase;

	private String[] resourceFilenamesToIgnore;
	private Pattern resourceFilenamesExcludePattern;
	private Pattern resourceFilenamesIncludePattern;

	// Properties to exclude from resource payloads
	private String[] excludeProperties;
	// Properties to include in resource payloads
	private String[] includeProperties;

	private boolean updateMimetypeWhenPropertiesAreEqual = false;

	private Map<String, Object> additionalProperties = new HashMap<>();

	private File projectDir;

	private DataConfig dataConfig;
	private PluginConfig pluginConfig;

	public AppConfig() {
		this(null);
	}

	public AppConfig(File projectDir) {
		this.projectDir = projectDir;

		dataConfig = new DataConfig(projectDir);
		pluginConfig = new PluginConfig(projectDir);

		// As of 3.15.0, defaulting everything except servers to use CMA. Changes to servers, such as changing group or
		// port number, cause conflicts with CMA.
		cmaConfig = new CmaConfig(true);
		cmaConfig.setDeployServers(false);

		modulePaths = new ArrayList<>();
		String path = projectDir != null ? new File(projectDir, DEFAULT_MODULES_PATH).getAbsolutePath() : DEFAULT_MODULES_PATH;
		modulePaths.add(path);

		String defaultSchemasPath = projectDir != null ? new File(projectDir, DEFAULT_SCHEMAS_PATH).getAbsolutePath() : DEFAULT_SCHEMAS_PATH;
		schemaPaths = new ArrayList<>();
		schemaPaths.add(defaultSchemasPath);

		configDirs = new ArrayList<>();
		configDirs.add(ConfigDir.withProjectDir(projectDir));

		moduleTimestampsPath = projectDir != null ? new File(projectDir, PropertiesModuleManager.DEFAULT_FILE_PATH).getAbsolutePath()
			: PropertiesModuleManager.DEFAULT_FILE_PATH;
	}

	public void populateCustomTokens(PropertiesSource propertiesSource) {
		populateCustomTokens(propertiesSource, "%%", "%%");
	}

	/**
	 * Populate the customTokens map in this class with the properties from the given properties source.
	 * @param propertiesSource
	 * @param prefix optional; if set, then each token key that is added has the prefix prepended to it
	 * @param suffix optional; if set, then each token key that is added has the suffix appended to it
	 */
	public void populateCustomTokens(PropertiesSource propertiesSource, String prefix, String suffix) {
		Properties props = propertiesSource.getProperties();
		if (props != null) {
			if (customTokens == null) {
				customTokens = new HashMap<>();
			}
			for (Object key : props.keySet()) {
				String skey = (String)key;
				String value = props.getProperty(skey);
				if (value != null) {
					String token = skey;
					if (prefix != null) {
						token = prefix + token;
					}
					if (suffix != null) {
						token = token + suffix;
					}
					customTokens.put(token, value);
				}
			}
		}
	}

	/**
	 * Builds a TokenReplacer based on the customTokens map held by this class.
	 *
	 * @return
	 */
	public TokenReplacer buildTokenReplacer() {
		DefaultTokenReplacer r = isUseRoxyTokenPrefix() ? new RoxyTokenReplacer() : new DefaultTokenReplacer();
		final Map<String, String> customTokens = getCustomTokens();
		if (customTokens != null) {
			r.addPropertiesSource(new MapPropertiesSource(customTokens));
		}
		return r;
	}

	public void setSimpleSslConfig() {
		setSimpleSslConfig(null);
	}

	/**
	 * @param protocol the name of the SSL/TLS protocol to use; if null, will use whatever SimpleX509TrustManager defaults to
	 */
	public void setSimpleSslConfig(String protocol) {
		setRestSslContext(protocol != null ? SimpleX509TrustManager.newSSLContext(protocol) : SimpleX509TrustManager.newSSLContext());
		setRestSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY);
		setRestTrustManager(new SimpleX509TrustManager());
	}

	public void setAppServicesSimpleSslConfig() {
		setAppServicesSimpleSslConfig(null);
	}

	/**
	 * @param protocol the name of the SSL/TLS protocol to use; if null, will use whatever SimpleX509TrustManager defaults to
	 */
	public void setAppServicesSimpleSslConfig(String protocol) {
		setAppServicesSslContext(protocol != null ? SimpleX509TrustManager.newSSLContext(protocol) : SimpleX509TrustManager.newSSLContext());
		setAppServicesSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY);
		setAppServicesTrustManager(new SimpleX509TrustManager());
	}

	/**
	 * Convenience method for constructing a MarkLogic Java API DatabaseClient based on the the host and rest*
	 * properties defined on this class.
	 *
	 * @return
	 */
	public DatabaseClient newDatabaseClient() {
		return configuredDatabaseClientFactory.newDatabaseClient(newRestDatabaseClientConfig(getRestPort()));
	}

	/**
	 * Just like newDatabaseClient, but uses testRestPort.
	 *
	 * @return
	 */
	public DatabaseClient newTestDatabaseClient() {
		DatabaseClientConfig config = newRestDatabaseClientConfig(getTestRestPort());
		if (StringUtils.hasText(getTestRestBasePath())) {
			config.setBasePath(getTestRestBasePath());
		}
		return configuredDatabaseClientFactory.newDatabaseClient(config);
	}

	public DatabaseClientConfig newRestDatabaseClientConfig(int port) {
		DatabaseClientConfig config = new DatabaseClientConfig(host, port, restAdminUsername, restAdminPassword);
		config.setCertFile(restCertFile);
		config.setCertPassword(restCertPassword);
		config.setConnectionType(restConnectionType);
		config.setExternalName(restExternalName);
		config.setSamlToken(restSamlToken);
		config.setSecurityContextType(restSecurityContextType);
		config.setCloudApiKey(cloudApiKey);
		config.setBasePath(restBasePath);

		if (restUseDefaultKeystore) {
		    config.setSslProtocol(StringUtils.hasText(restSslProtocol) ? restSslProtocol : SslUtil.DEFAULT_SSL_PROTOCOL);
		    config.setTrustManagementAlgorithm(restTrustManagementAlgorithm);
		    config.setSslHostnameVerifier(restSslHostnameVerifier != null ? restSslHostnameVerifier : SSLHostnameVerifier.ANY);
	    }
	    else {
		    config.setSslContext(restSslContext);
		    config.setTrustManager(restTrustManager);
		    config.setSslHostnameVerifier(restSslHostnameVerifier);
		}

		return config;
	}

	/**
	 * Constructs a DatabaseClient based on host, the appServices* properties, and the modules database name.
	 * @return
	 */
	public DatabaseClient newModulesDatabaseClient() {
		return newAppServicesDatabaseClient(getModulesDatabaseName());
	}

	/**
	 * Like newModulesDatabaseClient, but connects to schemas database.
	 *
	 * @return
	 */
	public DatabaseClient newSchemasDatabaseClient() {
		return newAppServicesDatabaseClient(getSchemasDatabaseName());
	}

	public DatabaseClient newAppServicesDatabaseClient(String databaseName) {
		DatabaseClientConfig config = new DatabaseClientConfig(host, appServicesPort, appServicesUsername, appServicesPassword);
		config.setCertFile(appServicesCertFile);
		config.setCertPassword(appServicesCertPassword);
		config.setConnectionType(appServicesConnectionType);
		config.setDatabase(databaseName);
		config.setExternalName(appServicesExternalName);
		config.setSamlToken(appServicesSamlToken);
		config.setSecurityContextType(appServicesSecurityContextType);
		config.setCloudApiKey(cloudApiKey);
		config.setBasePath(appServicesBasePath);

		if (appServicesUseDefaultKeystore) {
		    config.setSslProtocol(StringUtils.hasText(appServicesSslProtocol) ? appServicesSslProtocol : SslUtil.DEFAULT_SSL_PROTOCOL);
		    config.setTrustManagementAlgorithm(appServicesTrustManagementAlgorithm);
		    config.setSslHostnameVerifier(appServicesSslHostnameVerifier != null ? appServicesSslHostnameVerifier : SSLHostnameVerifier.ANY);
	    }
	    else {
		    config.setSslContext(appServicesSslContext);
		    config.setTrustManager(appServicesTrustManager);
		    config.setSslHostnameVerifier(appServicesSslHostnameVerifier);
		}

		return configuredDatabaseClientFactory.newDatabaseClient(config);
	}

	/**
	 * @return true if {@code testRestPort} is set and greater than zero. This is used as an indicator that an
	 * application wants test resources - most likely a separate app server and content database - created as
	 * part of a deployment.
	 */
	public boolean isTestPortSet() {
		return testRestPort != null && testRestPort > 0;
	}

	/**
	 * @return {@code restServerName} if it is set; {@code name} otherwise
	 */
	public String getRestServerName() {
		return restServerName != null ? restServerName : name;
	}

	public void setRestServerName(String restServerName) {
		this.restServerName = restServerName;
	}

	/**
	 * @return {@code testRestServerName} if it is set; {@code name}-test otherwise
	 */
	public String getTestRestServerName() {
		return testRestServerName != null ? testRestServerName : name + "-test";
	}

	public void setTestRestServerName(String testRestServerName) {
		this.testRestServerName = testRestServerName;
	}

	/**
	 * @return {@code contentDatabaseName} if it is set; {@code name}-content otherwise
	 */
	public String getContentDatabaseName() {
		return contentDatabaseName != null ? contentDatabaseName : name + "-content";
	}

	public void setContentDatabaseName(String contentDatabaseName) {
		this.contentDatabaseName = contentDatabaseName;
	}

	/**
	 * @return {@code testContentDatabaseName} if it is set; {@code name}-test-content otherwise
	 */
	public String getTestContentDatabaseName() {
		return testContentDatabaseName != null ? testContentDatabaseName : name + "-test-content";
	}

	public void setTestContentDatabaseName(String testContentDatabaseName) {
		this.testContentDatabaseName = testContentDatabaseName;
	}

	/**
	 * @return {@code modulesDatabaseName} if it is set; {@code name}-modules otherwise
	 */
	public String getModulesDatabaseName() {
		return modulesDatabaseName != null ? modulesDatabaseName : name + "-modules";
	}

	public void setModulesDatabaseName(String modulesDatabaseName) {
		this.modulesDatabaseName = modulesDatabaseName;
	}

	/**
	 * @return {@code triggersDatabaseName} if it is set; {@code name}-triggers otherwise
	 */
	public String getTriggersDatabaseName() {
		return triggersDatabaseName != null ? triggersDatabaseName : name + "-triggers";
	}

	public void setTriggersDatabaseName(String triggersDatabaseName) {
		this.triggersDatabaseName = triggersDatabaseName;
	}

	/**
	 * @return {@code schemasDatabaseName} if it is set; {@code name}-schemas otherwise
	 */
	public String getSchemasDatabaseName() {
		return schemasDatabaseName != null ? schemasDatabaseName : name + "-schemas";
	}

	public void setSchemasDatabaseName(String schemasDatabaseName) {
		this.schemasDatabaseName = schemasDatabaseName;
	}

	/**
	 * @return the name of the application, which is then used to generate app server and database names unless those
	 * are set via their respective properties
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the host that clients using this class will connect to
	 */
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the name of a MarkLogic user with the rest-admin role who can then load modules via a REST API server
	 */
	public String getRestAdminUsername() {
		return restAdminUsername;
	}

	public void setRestAdminUsername(String username) {
		this.restAdminUsername = username;
	}

	/**
	 * @return the password for the user identified by {@code restAdminUsername}
	 */
	public String getRestAdminPassword() {
		return restAdminPassword;
	}

	public void setRestAdminPassword(String password) {
		this.restAdminPassword = password;
	}

	/**
	 * @return the port of the REST API server used for loading modules
	 */
	public Integer getRestPort() {
		return restPort;
	}

	public void setRestPort(Integer restPort) {
		this.restPort = restPort;
	}

	/**
	 * @return the post of the REST API server used for loading modules that are specific to a test server (currently,
	 * just search options)
	 */
	public Integer getTestRestPort() {
		return testRestPort;
	}

	public void setTestRestPort(Integer testRestPort) {
		this.testRestPort = testRestPort;
	}

	/**
	 * @return a list of all the paths from which modules should be loaded into a REST API server modules database
	 */
	public List<String> getModulePaths() {
		return modulePaths;
	}

	public void setModulePaths(List<String> modulePaths) {
		this.modulePaths = modulePaths;
	}

	/**
	 * @return the name of the group in which the application associated with this configuration should have its app
	 * servers and other group-specific resources
	 */
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * Starting in 3.3.0, use this when you only care about the first ConfigDir in the List of ConfigDirs maintained by
	 * this class.
	 *
	 * @return
	 */
	public ConfigDir getFirstConfigDir() {
		if (configDirs == null || configDirs.isEmpty()) {
			this.configDirs = new ArrayList<>();
			this.configDirs.add(ConfigDir.withProjectDir(this.projectDir));
		}
		return configDirs.get(0);
	}

	/**
	 * As of 3.3.0, this is instead clearing and adding the ConfigDir to the List of ConfigDirs that this class now
	 * maintains.
	 *
	 * @param configDir
	 */
	public void setConfigDir(ConfigDir configDir) {
		this.configDirs = new ArrayList<>();
		this.configDirs.add(configDir);
	}

	/**
	 * @return a map of tokens that are intended to be replaced with their associated values in configuration files.
	 * This map allows for externalized properties to be passed into configuration files - e.g. Gradle
	 * properties can be swapped in for tokens in configuration files at deploy time.
	 */
	public Map<String, String> getCustomTokens() {
		return customTokens;
	}

	public void setCustomTokens(Map<String, String> customTokens) {
		this.customTokens = customTokens;
	}

	/**
	 * @return a Java {@code SSLContext} for making an SSL connection with the REST API server for loading modules; null
	 * if an SSL connection is not required
	 */
	public SSLContext getRestSslContext() {
		return restSslContext;
	}

	public void setRestSslContext(SSLContext restSslContext) {
		this.restSslContext = restSslContext;
	}

	/**
	 * @return a MarkLogic Java Client {@code SSLHostnameVerifier} that is used to make an SSL connection to the REST
	 * API server for loading modules; null if an SSL connection is not required
	 */
	public SSLHostnameVerifier getRestSslHostnameVerifier() {
		return restSslHostnameVerifier;
	}

	public void setRestSslHostnameVerifier(SSLHostnameVerifier restSslHostnameVerifier) {
		this.restSslHostnameVerifier = restSslHostnameVerifier;
	}

	public String[] getAdditionalBinaryExtensions() {
		return additionalBinaryExtensions;
	}

	public void setAdditionalBinaryExtensions(String[] additionalBinaryExtensions) {
		this.additionalBinaryExtensions = additionalBinaryExtensions;
	}

	public Integer getContentForestsPerHost() {
		return contentForestsPerHost;
	}

	public void setContentForestsPerHost(Integer contentForestsPerHost) {
		this.contentForestsPerHost = contentForestsPerHost;
	}

	public String getModulePermissions() {
		return modulePermissions;
	}

	public void setModulePermissions(String assetPermissions) {
		this.modulePermissions = assetPermissions;
	}

	public FileFilter getAssetFileFilter() {
		return assetFileFilter;
	}

	public void setAssetFileFilter(FileFilter assetFileFilter) {
		this.assetFileFilter = assetFileFilter;
	}

	public FileFilter getSchemasFileFilter() {
		return schemasFileFilter;
	}

	public void setSchemasFileFilter(FileFilter schemasFileFilter) {
		this.schemasFileFilter = schemasFileFilter;
	}

	public String getFlexrepPath() {
		return flexrepPath;
	}

	public void setFlexrepPath(String flexrepPath) {
		this.flexrepPath = flexrepPath;
	}

	public Map<String, Integer> getForestCounts() {
		return forestCounts;
	}

	public void setForestCounts(Map<String, Integer> forestCounts) {
		this.forestCounts = forestCounts;
	}

	public Integer getAppServicesPort() {
		return appServicesPort;
	}

	public void setAppServicesPort(Integer appServicesPort) {
		this.appServicesPort = appServicesPort;
	}

	public boolean isReplaceTokensInModules() {
		return replaceTokensInModules;
	}

	public void setReplaceTokensInModules(boolean replaceTokensInModules) {
		this.replaceTokensInModules = replaceTokensInModules;
	}

	public boolean isUseRoxyTokenPrefix() {
		return useRoxyTokenPrefix;
	}

	public void setUseRoxyTokenPrefix(boolean useRoxyTokenPrefix) {
		this.useRoxyTokenPrefix = useRoxyTokenPrefix;
	}

	public boolean isStaticCheckAssets() {
		return staticCheckAssets;
	}

	public void setStaticCheckAssets(boolean staticCheckAssets) {
		this.staticCheckAssets = staticCheckAssets;
	}

	public boolean isStaticCheckLibraryAssets() {
		return staticCheckLibraryAssets;
	}

	public void setStaticCheckLibraryAssets(boolean staticCheckLibraryAssets) {
		this.staticCheckLibraryAssets = staticCheckLibraryAssets;
	}

	public boolean isBulkLoadAssets() {
		return bulkLoadAssets;
	}

	public void setBulkLoadAssets(boolean bulkLoadAssets) {
		this.bulkLoadAssets = bulkLoadAssets;
	}

	public String getModelsPath() {
		return modelsPath;
	}

	public void setModelsPath(String modelsPath) {
		this.modelsPath = modelsPath;
	}

	public String getInstanceConverterPath() {
		return instanceConverterPath;
	}

	public void setInstanceConverterPath(String instanceConverterPath) {
		this.instanceConverterPath = instanceConverterPath;
	}


	public void setGenerateInstanceConverter(boolean generateInstanceConverter) {
		this.generateInstanceConverter = generateInstanceConverter;
	}

	public void setGenerateSchema(boolean generateSchema) {
		this.generateSchema = generateSchema;
	}

	public void setGenerateDatabaseProperties(boolean generateDatabaseProperties) {
		this.generateDatabaseProperties = generateDatabaseProperties;
	}

	public void setGenerateExtractionTemplate(boolean generateExtractionTemplate) {
		this.generateExtractionTemplate = generateExtractionTemplate;
	}

	public void setGenerateSearchOptions(boolean generateSearchOptions) {
		this.generateSearchOptions = generateSearchOptions;
	}

	public boolean isGenerateInstanceConverter() {
		return generateInstanceConverter;
	}

	public boolean isGenerateSchema() {
		return generateSchema;
	}

	public boolean isGenerateDatabaseProperties() {
		return generateDatabaseProperties;
	}

	public boolean isGenerateExtractionTemplate() {
		return generateExtractionTemplate;
	}

	public boolean isGenerateSearchOptions() {
		return generateSearchOptions;
	}

	public String getModuleTimestampsPath() {
		return moduleTimestampsPath;
	}

	public void setModuleTimestampsPath(String moduleTimestampsPath) {
		this.moduleTimestampsPath = moduleTimestampsPath;
	}

	public String[] getResourceFilenamesToIgnore() {
		return resourceFilenamesToIgnore;
	}

	public void setResourceFilenamesToIgnore(String... resourceFilenamesToIgnore) {
		this.resourceFilenamesToIgnore = resourceFilenamesToIgnore;
	}

	public boolean isDeleteReplicas() {
		return deleteReplicas;
	}

	public void setDeleteReplicas(boolean deleteReplicas) {
		this.deleteReplicas = deleteReplicas;
	}

	public boolean isDeleteForests() {
		return deleteForests;
	}

	public void setDeleteForests(boolean deleteForests) {
		this.deleteForests = deleteForests;
	}

	public boolean isCreateForests() {
		return createForests;
	}

	public void setCreateForests(boolean createForests) {
		this.createForests = createForests;
	}

	public boolean isNoRestServer() {
		return noRestServer;
	}

	public void setNoRestServer(boolean noRestServer) {
		this.noRestServer = noRestServer;
	}

	public SSLContext getAppServicesSslContext() {
		return appServicesSslContext;
	}

	public void setAppServicesSslContext(SSLContext appServicesSslContext) {
		this.appServicesSslContext = appServicesSslContext;
	}

	public SSLHostnameVerifier getAppServicesSslHostnameVerifier() {
		return appServicesSslHostnameVerifier;
	}

	public void setAppServicesSslHostnameVerifier(SSLHostnameVerifier appServicesSslHostnameVerifier) {
		this.appServicesSslHostnameVerifier = appServicesSslHostnameVerifier;
	}

	public String getReplicaForestDataDirectory() {
		return replicaForestDataDirectory;
	}

	public void setReplicaForestDataDirectory(String replicaForestDataDirectory) {
		this.replicaForestDataDirectory = replicaForestDataDirectory;
	}

	public String getReplicaForestLargeDataDirectory() {
		return replicaForestLargeDataDirectory;
	}

	public void setReplicaForestLargeDataDirectory(String replicaForestLargeDataDirectory) {
		this.replicaForestLargeDataDirectory = replicaForestLargeDataDirectory;
	}

	public String getReplicaForestFastDataDirectory() {
		return replicaForestFastDataDirectory;
	}

	public void setReplicaForestFastDataDirectory(String replicaForestFastDataDirectory) {
		this.replicaForestFastDataDirectory = replicaForestFastDataDirectory;
	}

	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

	public String getAppServicesUsername() {
		return appServicesUsername;
	}

	public void setAppServicesUsername(String appServicesUsername) {
		this.appServicesUsername = appServicesUsername;
	}

	public String getAppServicesPassword() {
		return appServicesPassword;
	}

	public void setAppServicesPassword(String appServicesPassword) {
		this.appServicesPassword = appServicesPassword;
	}

	public boolean isDeleteTestModules() {
		return deleteTestModules;
	}

	public void setDeleteTestModules(boolean deleteTestModules) {
		this.deleteTestModules = deleteTestModules;
	}

	public String getDeleteTestModulesPattern() {
		return deleteTestModulesPattern;
	}

	public void setDeleteTestModulesPattern(String deleteTestModulesPattern) {
		this.deleteTestModulesPattern = deleteTestModulesPattern;
	}

	public SecurityContextType getRestSecurityContextType() {
		return restSecurityContextType;
	}

	public void setRestSecurityContextType(SecurityContextType restSecurityContextType) {
		this.restSecurityContextType = restSecurityContextType;
	}

	public SecurityContextType getAppServicesSecurityContextType() {
		return appServicesSecurityContextType;
	}

	public void setAppServicesSecurityContextType(SecurityContextType appServicesSecurityContextType) {
		this.appServicesSecurityContextType = appServicesSecurityContextType;
	}

	public String getRestCertFile() {
		return restCertFile;
	}

	public void setRestCertFile(String restCertFile) {
		this.restCertFile = restCertFile;
	}

	public String getRestCertPassword() {
		return restCertPassword;
	}

	public void setRestCertPassword(String restCertPassword) {
		this.restCertPassword = restCertPassword;
	}

	public String getAppServicesCertFile() {
		return appServicesCertFile;
	}

	public void setAppServicesCertFile(String appServicesCertFile) {
		this.appServicesCertFile = appServicesCertFile;
	}

	public String getAppServicesCertPassword() {
		return appServicesCertPassword;
	}

	public void setAppServicesCertPassword(String appServicesCertPassword) {
		this.appServicesCertPassword = appServicesCertPassword;
	}

	public String getRestExternalName() {
		return restExternalName;
	}

	public void setRestExternalName(String restExternalName) {
		this.restExternalName = restExternalName;
	}

	public String getAppServicesExternalName() {
		return appServicesExternalName;
	}

	public void setAppServicesExternalName(String appServicesExternalName) {
		this.appServicesExternalName = appServicesExternalName;
	}

	public ConfiguredDatabaseClientFactory getConfiguredDatabaseClientFactory() {
		return configuredDatabaseClientFactory;
	}

	public void setConfiguredDatabaseClientFactory(ConfiguredDatabaseClientFactory configuredDatabaseClientFactory) {
		this.configuredDatabaseClientFactory = configuredDatabaseClientFactory;
	}

	public Pattern getResourceFilenamesExcludePattern() {
		return resourceFilenamesExcludePattern;
	}

	public void setResourceFilenamesExcludePattern(Pattern resourceFilenamesExcludePattern) {
		this.resourceFilenamesExcludePattern = resourceFilenamesExcludePattern;
	}

	public Pattern getResourceFilenamesIncludePattern() {
		return resourceFilenamesIncludePattern;
	}

	public void setResourceFilenamesIncludePattern(Pattern resourceFilenamesIncludePattern) {
		this.resourceFilenamesIncludePattern = resourceFilenamesIncludePattern;
	}

	public int getModulesLoaderThreadCount() {
		return modulesLoaderThreadCount;
	}

	public void setModulesLoaderThreadCount(int modulesLoaderThreadCount) {
		this.modulesLoaderThreadCount = modulesLoaderThreadCount;
	}

	public Map<String, List<String>> getDatabaseDataDirectories() {
		return databaseDataDirectories;
	}

	public void setDatabaseDataDirectories(Map<String, List<String>> databaseDataDirectories) {
		this.databaseDataDirectories = databaseDataDirectories;
	}

	public Map<String, String> getDatabaseFastDataDirectories() {
		return databaseFastDataDirectories;
	}

	public void setDatabaseFastDataDirectories(Map<String, String> databaseFastDataDirectories) {
		this.databaseFastDataDirectories = databaseFastDataDirectories;
	}

	public Map<String, String> getDatabaseLargeDataDirectories() {
		return databaseLargeDataDirectories;
	}

	public void setDatabaseLargeDataDirectories(Map<String, String> databaseLargeDataDirectories) {
		this.databaseLargeDataDirectories = databaseLargeDataDirectories;
	}

	public String getForestDataDirectory() {
		return forestDataDirectory;
	}

	public void setForestDataDirectory(String forestDataDirectory) {
		this.forestDataDirectory = forestDataDirectory;
	}

	public String getForestFastDataDirectory() {
		return forestFastDataDirectory;
	}

	public void setForestFastDataDirectory(String forestFastDataDirectory) {
		this.forestFastDataDirectory = forestFastDataDirectory;
	}

	public String getForestLargeDataDirectory() {
		return forestLargeDataDirectory;
	}

	public void setForestLargeDataDirectory(String forestLargeDataDirectory) {
		this.forestLargeDataDirectory = forestLargeDataDirectory;
	}

	public Map<String, List<String>> getDatabaseReplicaDataDirectories() {
		return databaseReplicaDataDirectories;
	}

	public void setDatabaseReplicaDataDirectories(Map<String, List<String>> databaseReplicaDataDirectories) {
		this.databaseReplicaDataDirectories = databaseReplicaDataDirectories;
	}

	public Map<String, String> getDatabaseReplicaFastDataDirectories() {
		return databaseReplicaFastDataDirectories;
	}

	public void setDatabaseReplicaFastDataDirectories(Map<String, String> databaseReplicaFastDataDirectories) {
		this.databaseReplicaFastDataDirectories = databaseReplicaFastDataDirectories;
	}

	public Map<String, String> getDatabaseReplicaLargeDataDirectories() {
		return databaseReplicaLargeDataDirectories;
	}

	public void setDatabaseReplicaLargeDataDirectories(Map<String, String> databaseReplicaLargeDataDirectories) {
		this.databaseReplicaLargeDataDirectories = databaseReplicaLargeDataDirectories;
	}

	public boolean isCatchDeployExceptions() {
		return catchDeployExceptions;
	}

	public void setCatchDeployExceptions(boolean catchDeployExceptions) {
		this.catchDeployExceptions = catchDeployExceptions;
	}

	public boolean isCatchUndeployExceptions() {
		return catchUndeployExceptions;
	}

	public void setCatchUndeployExceptions(boolean catchUndeployExceptions) {
		this.catchUndeployExceptions = catchUndeployExceptions;
	}

	public boolean isDatabaseWithForestsOnOneHost(String databaseName) {
		if (databasesWithForestsOnOneHost == null) {
			return false;
		}
		return databasesWithForestsOnOneHost.contains(databaseName);
	}

	public void addDatabaseWithForestsOnOneHost(String databaseName) {
		if (databasesWithForestsOnOneHost == null) {
			databasesWithForestsOnOneHost = new HashSet<>();
		}
		databasesWithForestsOnOneHost.add(databaseName);
	}

	public Set<String> getDatabasesWithForestsOnOneHost() {
		return databasesWithForestsOnOneHost;
	}

	public void setDatabasesWithForestsOnOneHost(Set<String> databasesWithForestsOnOneHost) {
		this.databasesWithForestsOnOneHost = databasesWithForestsOnOneHost;
	}

	public Map<String, List<String>> getDatabaseHosts() {
		return databaseHosts;
	}

	public void setDatabaseHosts(Map<String, List<String>> databaseHosts) {
		this.databaseHosts = databaseHosts;
	}

	public void setExcludeProperties(String... excludeProperties) {
		if (this.includeProperties != null && this.includeProperties.length > 0) {
			throw new IllegalStateException("Setting excludeProperties and includeProperties at the same time is not permitted");
		}
		this.excludeProperties = excludeProperties;
	}

	public String[] getExcludeProperties() {
		return this.excludeProperties;
	}

	public void setIncludeProperties(String... includeProperties) {
		if (this.excludeProperties != null && this.excludeProperties.length > 0) {
			throw new IllegalStateException("Setting excludeProperties and includeProperties at the same time is not permitted");
		}
		this.includeProperties = includeProperties;
	}

	public String[] getIncludeProperties() {
		return this.includeProperties;
	}

	public Map<String, List<String>> getDatabaseGroups() {
		return databaseGroups;
	}

	public void setDatabaseGroups(Map<String, List<String>> databaseGroups) {
		this.databaseGroups = databaseGroups;
	}

	public Map<String, String> getHostGroups() {
		return hostGroups;
	}

	public void setHostGroups(Map<String, String> hostGroups) {
		this.hostGroups = hostGroups;
	}

	public List<ConfigDir> getConfigDirs() {
		return configDirs;
	}

	public void setConfigDirs(List<ConfigDir> configDirs) {
		this.configDirs = configDirs;
	}

	public Pattern getModuleFilenamesIncludePattern() {
		return moduleFilenamesIncludePattern;
	}

	public void setModuleFilenamesIncludePattern(Pattern moduleFilenamesIncludePattern) {
		this.moduleFilenamesIncludePattern = moduleFilenamesIncludePattern;
	}

	public boolean isSortOtherDatabaseByDependencies() {
		return sortOtherDatabaseByDependencies;
	}

	public void setSortOtherDatabaseByDependencies(boolean sortOtherDatabaseByDependencies) {
		this.sortOtherDatabaseByDependencies = sortOtherDatabaseByDependencies;
	}

	public Integer getModulesLoaderBatchSize() {
		return modulesLoaderBatchSize;
	}

	public void setModulesLoaderBatchSize(Integer modulesLoaderBatchSize) {
		this.modulesLoaderBatchSize = modulesLoaderBatchSize;
	}

	public boolean isIncrementalDeploy() {
		return incrementalDeploy;
	}

	public void setIncrementalDeploy(boolean incrementalDeploy) {
		this.incrementalDeploy = incrementalDeploy;
	}

	public String getModelsDatabase() {
		return modelsDatabase;
	}

	public void setModelsDatabase(String modelsDatabase) {
		this.modelsDatabase = modelsDatabase;
	}

	public String getCpfDatabaseName() {
		return cpfDatabaseName != null ? cpfDatabaseName : getTriggersDatabaseName();
	}

	public void setCpfDatabaseName(String cpfDatabaseName) {
		this.cpfDatabaseName = cpfDatabaseName;
	}

	public Map<String, Integer> getDatabaseNamesAndReplicaCounts() {
		return databaseNamesAndReplicaCounts;
	}

	public void setDatabaseNamesAndReplicaCounts(Map<String, Integer> databaseNamesAndReplicaCounts) {
		this.databaseNamesAndReplicaCounts = databaseNamesAndReplicaCounts;
	}

	public Map<String, ForestNamingStrategy> getForestNamingStrategies() {
		return forestNamingStrategies;
	}

	public void setForestNamingStrategies(Map<String, ForestNamingStrategy> forestNamingStrategies) {
		this.forestNamingStrategies = forestNamingStrategies;
	}

	public boolean isUpdateMimetypeWhenPropertiesAreEqual() {
		return updateMimetypeWhenPropertiesAreEqual;
	}

	public void setUpdateMimetypeWhenPropertiesAreEqual(boolean updateMimetypeWhenPropertiesAreEqual) {
		this.updateMimetypeWhenPropertiesAreEqual = updateMimetypeWhenPropertiesAreEqual;
	}

	public X509TrustManager getRestTrustManager() {
		return restTrustManager;
	}

	public void setRestTrustManager(X509TrustManager restTrustManager) {
		this.restTrustManager = restTrustManager;
	}

	public X509TrustManager getAppServicesTrustManager() {
		return appServicesTrustManager;
	}

	public void setAppServicesTrustManager(X509TrustManager appServicesTrustManager) {
		this.appServicesTrustManager = appServicesTrustManager;
	}

	public ReplicaBuilderStrategy getReplicaBuilderStrategy() {
		return replicaBuilderStrategy;
	}

	public void setReplicaBuilderStrategy(ReplicaBuilderStrategy replicaBuilderStrategy) {
		this.replicaBuilderStrategy = replicaBuilderStrategy;
	}

	public boolean isTdeValidationEnabled() {
		return tdeValidationEnabled;
	}

	public void setTdeValidationEnabled(boolean tdeValidationEnabled) {
		this.tdeValidationEnabled = tdeValidationEnabled;
	}

	public boolean isAddHostNameTokens() {
		return addHostNameTokens;
	}

	public void setAddHostNameTokens(boolean addHostNameTokens) {
		this.addHostNameTokens = addHostNameTokens;
	}

	public DataConfig getDataConfig() {
		return dataConfig;
	}

	public void setDataConfig(DataConfig dataConfig) {
		this.dataConfig = dataConfig;
	}

	public PluginConfig getPluginConfig() {
		return pluginConfig;
	}

	public void setPluginConfig(PluginConfig pluginConfig) {
		this.pluginConfig = pluginConfig;
	}

	public List<String> getSchemaPaths() {
		return schemaPaths;
	}

	public void setSchemaPaths(List<String> schemaPaths) {
		this.schemaPaths = schemaPaths;
	}

	public boolean isMergeResources() {
		return mergeResources;
	}

	public void setMergeResources(boolean mergeResources) {
		this.mergeResources = mergeResources;
	}

	public boolean isModuleTimestampsUseHost() {
		return moduleTimestampsUseHost;
	}

	public void setModuleTimestampsUseHost(boolean moduleTimestampsUseHost) {
		this.moduleTimestampsUseHost = moduleTimestampsUseHost;
	}

	public DatabaseClient.ConnectionType getRestConnectionType() {
		return restConnectionType;
	}

	public void setRestConnectionType(DatabaseClient.ConnectionType restConnectionType) {
		this.restConnectionType = restConnectionType;
	}

	public DatabaseClient.ConnectionType getAppServicesConnectionType() {
		return appServicesConnectionType;
	}

	public void setAppServicesConnectionType(DatabaseClient.ConnectionType appServicesConnectionType) {
		this.appServicesConnectionType = appServicesConnectionType;
	}

	public CmaConfig getCmaConfig() {
		return cmaConfig;
	}

	public void setCmaConfig(CmaConfig cmaConfig) {
		this.cmaConfig = cmaConfig;
	}

	public boolean isRestUseDefaultKeystore() {
		return restUseDefaultKeystore;
	}

	public void setRestUseDefaultKeystore(boolean restUseDefaultKeystore) {
		this.restUseDefaultKeystore = restUseDefaultKeystore;
	}

	public String getRestSslProtocol() {
		return restSslProtocol;
	}

	public void setRestSslProtocol(String restSslProtocol) {
		this.restSslProtocol = restSslProtocol;
	}

	public String getRestTrustManagementAlgorithm() {
		return restTrustManagementAlgorithm;
	}

	public void setRestTrustManagementAlgorithm(String restTrustManagementAlgorithm) {
		this.restTrustManagementAlgorithm = restTrustManagementAlgorithm;
	}

	public boolean isAppServicesUseDefaultKeystore() {
		return appServicesUseDefaultKeystore;
	}

	public void setAppServicesUseDefaultKeystore(boolean appServicesUseDefaultKeystore) {
		this.appServicesUseDefaultKeystore = appServicesUseDefaultKeystore;
	}

	public String getAppServicesSslProtocol() {
		return appServicesSslProtocol;
	}

	public void setAppServicesSslProtocol(String appServicesSslProtocol) {
		this.appServicesSslProtocol = appServicesSslProtocol;
	}

	public String getAppServicesTrustManagementAlgorithm() {
		return appServicesTrustManagementAlgorithm;
	}

	public void setAppServicesTrustManagementAlgorithm(String appServicesTrustManagementAlgorithm) {
		this.appServicesTrustManagementAlgorithm = appServicesTrustManagementAlgorithm;
	}

	public String getModuleUriPrefix() {
		return moduleUriPrefix;
	}

	public void setModuleUriPrefix(String moduleUriPrefix) {
		this.moduleUriPrefix = moduleUriPrefix;
	}

	public String getCloudApiKey() {
		return cloudApiKey;
	}

	public void setCloudApiKey(String cloudApiKey) {
		this.cloudApiKey = cloudApiKey;
	}

	public String getRestBasePath() {
		return restBasePath;
	}

	public void setRestBasePath(String restBasePath) {
		this.restBasePath = restBasePath;
	}

	public String getAppServicesBasePath() {
		return appServicesBasePath;
	}

	public void setAppServicesBasePath(String appServicesBasePath) {
		this.appServicesBasePath = appServicesBasePath;
	}

	public String getTestRestBasePath() {
		return testRestBasePath;
	}

	public void setTestRestBasePath(String testRestBasePath) {
		this.testRestBasePath = testRestBasePath;
	}

	public String getRestSamlToken() {
		return restSamlToken;
	}

	public void setRestSamlToken(String restSamlToken) {
		this.restSamlToken = restSamlToken;
	}

	public String getAppServicesSamlToken() {
		return appServicesSamlToken;
	}

	public void setAppServicesSamlToken(String appServicesSamlToken) {
		this.appServicesSamlToken = appServicesSamlToken;
	}

	public boolean isCascadeCollections() {
		return cascadeCollections;
	}

	public void setCascadeCollections(boolean cascadeCollections) {
		this.cascadeCollections = cascadeCollections;
	}

	public boolean isCascadePermissions() {
		return cascadePermissions;
	}

	public void setCascadePermissions(boolean cascadePermissions) {
		this.cascadePermissions = cascadePermissions;
	}
}
