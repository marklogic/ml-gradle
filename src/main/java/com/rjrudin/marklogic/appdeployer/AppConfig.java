package com.rjrudin.marklogic.appdeployer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.DatabaseClientFactory.SSLHostnameVerifier;

/**
 * Encapsulates the connection information for an application and where its modules can be found. Should possibly add
 * ConfigDir to this.
 */
public class AppConfig {

    private String name;
    private String host = "localhost";

    // Username/password combo for using the client REST API - e.g. to load modules
    private String restAdminUsername;
    private String restAdminPassword;
    private SSLContext restSslContext;
    private SSLHostnameVerifier restSslHostnameVerifier;
    private Authentication restAuthentication = Authentication.DIGEST;

    private Integer restPort;
    private Integer testRestPort;

    // These can all be set to override the default names
    private String restServerName;
    private String testRestServerName;
    private String contentDatabaseName;
    private String testContentDatabaseName;
    private String modulesDatabaseName;
    private String triggersDatabaseName;
    private String schemasDatabaseName;

    private List<String> modulePaths;
    private ConfigDir configDir;

    private String groupName = "Default";

    // Passed into the TokenReplacer that subclasses of AbstractCommand use
    private Map<String, String> customTokens = new HashMap<>();

    // Allows for creating a triggers database without a config file for one
    private boolean createTriggersDatabase = true;

    public AppConfig() {
        this("src/main/ml-modules");
    }

    public AppConfig(String defaultModulePath) {
        modulePaths = new ArrayList<String>();
        modulePaths.add(defaultModulePath);
        configDir = new ConfigDir();
    }

    public DatabaseClient newDatabaseClient() {
        return DatabaseClientFactory.newClient(getHost(), getRestPort(), getRestAdminUsername(),
                getRestAdminPassword(), getRestAuthentication(), getRestSslContext(), getRestSslHostnameVerifier());
    }

    public boolean isTestPortSet() {
        return testRestPort != null && testRestPort > 0;
    }

    public String getRestServerName() {
        return restServerName != null ? restServerName : name;
    }

    public String getTestRestServerName() {
        return testRestServerName != null ? testRestServerName : name + "-test";
    }

    public String getContentDatabaseName() {
        return contentDatabaseName != null ? contentDatabaseName : name + "-content";
    }

    public String getTestContentDatabaseName() {
        return testContentDatabaseName != null ? testContentDatabaseName : name + "-test-content";
    }

    public String getModulesDatabaseName() {
        return modulesDatabaseName != null ? modulesDatabaseName : name + "-modules";
    }

    public String getTriggersDatabaseName() {
        return triggersDatabaseName != null ? triggersDatabaseName : name + "-triggers";
    }

    public String getSchemasDatabaseName() {
        return schemasDatabaseName != null ? schemasDatabaseName : name + "-schemas";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRestAdminUsername() {
        return restAdminUsername;
    }

    public void setRestAdminUsername(String username) {
        this.restAdminUsername = username;
    }

    public String getRestAdminPassword() {
        return restAdminPassword;
    }

    public void setRestAdminPassword(String password) {
        this.restAdminPassword = password;
    }

    public Integer getRestPort() {
        return restPort;
    }

    public void setRestPort(Integer restPort) {
        this.restPort = restPort;
    }

    public Integer getTestRestPort() {
        return testRestPort;
    }

    public void setTestRestPort(Integer testRestPort) {
        this.testRestPort = testRestPort;
    }

    public List<String> getModulePaths() {
        return modulePaths;
    }

    public void setModulePaths(List<String> modulePaths) {
        this.modulePaths = modulePaths;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Authentication getRestAuthentication() {
        return restAuthentication;
    }

    public void setRestAuthentication(Authentication authentication) {
        this.restAuthentication = authentication;
    }

    public ConfigDir getConfigDir() {
        return configDir;
    }

    public void setConfigDir(ConfigDir configDir) {
        this.configDir = configDir;
    }

    public Map<String, String> getCustomTokens() {
        return customTokens;
    }

    public void setCustomTokens(Map<String, String> customTokens) {
        this.customTokens = customTokens;
    }

    public boolean isCreateTriggersDatabase() {
        return createTriggersDatabase;
    }

    public void setCreateTriggersDatabase(boolean createTriggerDatabase) {
        this.createTriggersDatabase = createTriggerDatabase;
    }

    public SSLContext getRestSslContext() {
        return restSslContext;
    }

    public void setRestSslContext(SSLContext restSslContext) {
        this.restSslContext = restSslContext;
    }

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

}
