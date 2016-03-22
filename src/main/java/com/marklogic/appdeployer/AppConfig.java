package com.marklogic.appdeployer;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;
import com.marklogic.client.modulesloader.impl.XccAssetLoader;
import com.marklogic.client.modulesloader.ssl.SimpleX509TrustManager;
import com.marklogic.client.modulesloader.xcc.DefaultDocumentFormatGetter;

/**
 * Encapsulates common configuration properties for an application deployed to MarkLogic. These properties include not
 * just names of application resources - such as app servers and databases - but also connection information for loading
 * modules into an application as well as paths for modules and configuration files.
 * 
 * An instance of this class is passed in as the main argument to the methods in the {@code AppDeployer} interface,
 * meaning that you're free to not just configure this as needed but also subclass it and add anything that you would
 * like.
 */
public class AppConfig {

    /**
     * This is set purely for development purposes so that an app can be created without specifying an app name.
     */
    public static final String DEFAULT_APP_NAME = "my-app";

    /**
     * These are assumed as sensible defaults in a development environment, where teams often use admin/admin for the
     * admin login. They are of course expected to change in a real environment.
     */
    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "admin";

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
    private String host = DEFAULT_HOST;
    
    // Username/password combo for using the client REST API - e.g. to load modules
    private String restAdminUsername = DEFAULT_USERNAME;
    private String restAdminPassword = DEFAULT_PASSWORD;
    private SSLContext restSslContext;
    private SSLHostnameVerifier restSslHostnameVerifier;
    private Authentication restAuthentication = Authentication.DIGEST;

    private Integer restPort = DEFAULT_PORT;
    private Integer testRestPort;

    // These can all be set to override the default names that are generated off of the "name" attribute.
    private String groupName = DEFAULT_GROUP;
    private String restServerName;
    private String testRestServerName;
    private String contentDatabaseName;
    private String testContentDatabaseName;
    private String modulesDatabaseName;
    private String triggersDatabaseName;
    private String schemasDatabaseName;

    private List<String> modulePaths;
    private String schemasPath;
    private ConfigDir configDir;

    // Passed into the TokenReplacer that subclasses of AbstractCommand use
    private Map<String, String> customTokens = new HashMap<>();

    // Allows for creating a triggers database without a config file for one
    private boolean createTriggersDatabase = true;

    // As defined by the REST API
    private String modulePermissions = "rest-admin,read,rest-admin,update,rest-extension-user,execute";

    private FileFilter assetFileFilter;

    // Additional module extensions that should be loaded as binaries into the modules database
    private String[] additionalBinaryExtensions;

    // Will override the number of forests that DeployContentDatabasesCommand creates
    private Integer contentForestsPerHost;

    // Comma-delimited string used for configuring forest replicas
    private String databaseNamesAndReplicaCounts;

    
	public AppConfig() {
        this(DEFAULT_MODULES_PATH, DEFAULT_SCHEMAS_PATH);
    }

	public AppConfig(String defaultModulePath) {
	    this(defaultModulePath, DEFAULT_SCHEMAS_PATH);
	}
	
    public AppConfig(String defaultModulePath, String defaultSchemasPath) {
        modulePaths = new ArrayList<String>();
        modulePaths.add(defaultModulePath);
        configDir = new ConfigDir();
        schemasPath = defaultSchemasPath;
    }

    public void setSimpleSslConfig() {
        setRestSslContext(SimpleX509TrustManager.newSSLContext());
        setRestSslHostnameVerifier(DatabaseClientFactory.SSLHostnameVerifier.ANY);
    }

    /**
     * Convenience method for constructing a MarkLogic Java API DatabaseClient based on the host, restPort,
     * restAdminUsername, restAdminPassword, restAuthentication, restSslContext, and restSslHostnameVerifier properties.
     * 
     * @return
     */
    public DatabaseClient newDatabaseClient() {
        return DatabaseClientFactory.newClient(getHost(), getRestPort(), getRestAdminUsername(), getRestAdminPassword(),
                getRestAuthentication(), getRestSslContext(), getRestSslHostnameVerifier());
    }

    /**
     * Just like newDatabaseClient, but uses testRestPort.
     * 
     * @return
     */
    public DatabaseClient newTestDatabaseClient() {
        return DatabaseClientFactory.newClient(getHost(), getTestRestPort(), getRestAdminUsername(),
                getRestAdminPassword(), getRestAuthentication(), getRestSslContext(), getRestSslHostnameVerifier());
    }
    
    /**
     * Like newDatabaseClient, but connects to schemas database.
     * 
     * @return
     */
	public DatabaseClient newSchemasDatabaseClient() {
		return DatabaseClientFactory.newClient(getHost(),  getRestPort(), getSchemasDatabaseName(), 
				getRestAdminUsername(), getRestAdminPassword(), getRestAuthentication(), 
				getRestSslContext(), getRestSslHostnameVerifier());
	}


    /**
     * @return an XccAssetLoader based on the configuration properties in this class
     */
    public XccAssetLoader newXccAssetLoader() {
        XccAssetLoader l = new XccAssetLoader();
        l.setHost(getHost());
        l.setUsername(getRestAdminUsername());
        l.setPassword(getRestAdminPassword());
        l.setDatabaseName(getModulesDatabaseName());

        String permissions = getModulePermissions();
        if (permissions != null) {
            l.setPermissions(permissions);
        }

        String[] extensions = getAdditionalBinaryExtensions();
        if (extensions != null) {
            DefaultDocumentFormatGetter getter = new DefaultDocumentFormatGetter();
            for (String ext : extensions) {
                getter.getBinaryExtensions().add(ext);
            }
            l.setDocumentFormatGetter(getter);
        }

        if (assetFileFilter != null) {
            l.setFileFilter(assetFileFilter);
        }

        return l;

    }

    /**
     * @return true if {@code testRestPort} is set and greater than zero. This is used as an indicator that an
     *         application wants test resources - most likely a separate app server and content database - created as
     *         part of a deployment.
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

    /**
     * @return {@code testRestServerName} if it is set; {@code name}-test otherwise
     */
    public String getTestRestServerName() {
        return testRestServerName != null ? testRestServerName : name + "-test";
    }

    /**
     * @return {@code contentDatabaseName} if it is set; {@code name}-content otherwise
     */
    public String getContentDatabaseName() {
        return contentDatabaseName != null ? contentDatabaseName : name + "-content";
    }

    /**
     * @return {@code testContentDatabaseName} if it is set; {@code name}-test-content otherwise
     */
    public String getTestContentDatabaseName() {
        return testContentDatabaseName != null ? testContentDatabaseName : name + "-test-content";
    }

    /**
     * @return {@code modulesDatabaseName} if it is set; {@code name}-modules otherwise
     */
    public String getModulesDatabaseName() {
        return modulesDatabaseName != null ? modulesDatabaseName : name + "-modules";
    }

    /**
     * @return {@code triggersDatabaseName} if it is set; {@code name}-triggers otherwise
     */
    public String getTriggersDatabaseName() {
        return triggersDatabaseName != null ? triggersDatabaseName : name + "-triggers";
    }

    /**
     * @return {@code schemasDatabaseName} if it is set; {@code name}-schemas otherwise
     */
    public String getSchemasDatabaseName() {
        return schemasDatabaseName != null ? schemasDatabaseName : name + "-schemas";
    }
    
    /**
     * @return the name of the application, which is then used to generate app server and database names unless those
     *         are set via their respective properties
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
     *         just search options)
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
    public String getSchemasPath() {
        return schemasPath;
    }

    public void setSchemasPath(String schemasPath) {
        this.schemasPath = schemasPath;
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
     *         servers and other group-specific resources
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the MarkLogic Java Client {@code Authentication} object that is used for authenticating with a REST API
     *         server for loading modules
     */
    public Authentication getRestAuthentication() {
        return restAuthentication;
    }

    public void setRestAuthentication(Authentication authentication) {
        this.restAuthentication = authentication;
    }

    /**
     * @return a {@code ConfigDir} instance that defines the location of the configuration directory (where files are
     *         stored that are then loaded via MarkLogic Management API endpoints) as well as paths to specific
     *         resources within that directory
     */
    public ConfigDir getConfigDir() {
        return configDir;
    }

    public void setConfigDir(ConfigDir configDir) {
        this.configDir = configDir;
    }

    /**
     * @return a map of tokens that are intended to be replaced with their associated values in configuration files.
     *         This map allows for externalized properties to be passed into configuration files - e.g. Gradle
     *         properties can be swapped in for tokens in configuration files at deploy time.
     */
    public Map<String, String> getCustomTokens() {
        return customTokens;
    }

    public void setCustomTokens(Map<String, String> customTokens) {
        this.customTokens = customTokens;
    }

    /**
     * @return whether a triggers database should be created by default; defaults to true, as it's very common to need a
     *         triggers database, such as for CPF, Alerting, custom triggers, etc.
     */
    public boolean isCreateTriggersDatabase() {
        return createTriggersDatabase;
    }

    public void setCreateTriggersDatabase(boolean createTriggerDatabase) {
        this.createTriggersDatabase = createTriggerDatabase;
    }

    /**
     * @return a Java {@code SSLContext} for making an SSL connection with the REST API server for loading modules; null
     *         if an SSL connection is not required
     */
    public SSLContext getRestSslContext() {
        return restSslContext;
    }

    public void setRestSslContext(SSLContext restSslContext) {
        this.restSslContext = restSslContext;
    }

    /**
     * @return a MarkLogic Java Client {@code SSLHostnameVerifier} that is used to make an SSL connection to the REST
     *         API server for loading modules; null if an SSL connection is not required
     */
    public SSLHostnameVerifier getRestSslHostnameVerifier() {
        return restSslHostnameVerifier;
    }

    public void setRestSslHostnameVerifier(SSLHostnameVerifier restSslHostnameVerifier) {
        this.restSslHostnameVerifier = restSslHostnameVerifier;
    }

    public void setRestServerName(String restServerName) {
        this.restServerName = restServerName;
    }

    public void setTestRestServerName(String testRestServerName) {
        this.testRestServerName = testRestServerName;
    }

    public void setContentDatabaseName(String contentDatabaseName) {
        this.contentDatabaseName = contentDatabaseName;
    }

    public void setTestContentDatabaseName(String testContentDatabaseName) {
        this.testContentDatabaseName = testContentDatabaseName;
    }

    public void setModulesDatabaseName(String modulesDatabaseName) {
        this.modulesDatabaseName = modulesDatabaseName;
    }

    public void setTriggersDatabaseName(String triggersDatabaseName) {
        this.triggersDatabaseName = triggersDatabaseName;
    }

    public void setSchemasDatabaseName(String schemasDatabaseName) {
        this.schemasDatabaseName = schemasDatabaseName;
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

    public String getDatabaseNamesAndReplicaCounts() {
        return databaseNamesAndReplicaCounts;
    }

    public void setDatabaseNamesAndReplicaCounts(String databaseNamesAndReplicaCounts) {
        this.databaseNamesAndReplicaCounts = databaseNamesAndReplicaCounts;
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
}
