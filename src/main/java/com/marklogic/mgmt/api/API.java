package com.marklogic.mgmt.api;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.helper.ClientHelper;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.DefaultManageConfigFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.api.cluster.Cluster;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.api.restapi.RestApi;
import com.marklogic.mgmt.api.security.*;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.api.task.Task;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.appservers.ServerManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.groups.GroupManager;
import com.marklogic.mgmt.resource.security.*;
import com.marklogic.mgmt.resource.tasks.TaskManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import com.marklogic.mgmt.util.SimplePropertySource;
import com.marklogic.mgmt.util.SystemPropertySource;

import java.io.IOException;

/**
 * Big facade-style class for the MarkLogic Management API. Use this to instantiate or access any resource, as it will
 * handle dependencies for those resources.
 */
public class API extends LoggingObject {

    private ManageClient manageClient;
    private AdminManager adminManager;
    private ObjectMapper objectMapper;

    public API(ManageClient client) {
        this.manageClient = client;
        setObjectMapper(buildDefaultObjectMapper());
        initializeAdminManager();
    }

    public API(ManageClient client, AdminManager adminManager) {
        this.manageClient = client;
        this.adminManager = adminManager;
        setObjectMapper(buildDefaultObjectMapper());
    }

    public API(ManageClient client, ObjectMapper mapper) {
        this.manageClient = client;
        this.objectMapper = mapper;
        initializeAdminManager();
    }

    protected void initializeAdminManager() {
    	if (manageClient != null) {
		    ManageConfig mc = manageClient.getManageConfig();
		    if (mc.getAdminUsername() != null && mc.getAdminPassword() != null) {
			    AdminConfig ac = new AdminConfig(mc.getHost(), 8001, mc.getAdminUsername(), mc.getAdminPassword());
			    ac.setConfigureSimpleSsl(mc.isAdminConfigureSimpleSsl());
			    this.adminManager = new AdminManager(ac);
		    }
	    }
    }

    protected ObjectMapper buildDefaultObjectMapper() {
        return ObjectMapperFactory.getObjectMapper();
    }

    /**
     * Connect to a (presumably) different host, using the same username/password combos that were used for the same
     * connection. Useful in a development environment where you may have multiple clusters with the same admin
     * username/password combo, and you want to switch quickly from one to another.
     *
     * @param host
     */
    public void connect(String host) {
        connect(host, this.manageClient.getManageConfig());
    }

	/**
	 * Connect to a (presumably) different MarkLogic Management API.
	 *
	 * @param host
	 * @param mc
	 */
	public void connect(String host, ManageConfig mc) {
	    if (logger.isInfoEnabled()) {
		    logger.info("Connecting to host: " + host);
	    }
	    SimplePropertySource sps = new SimplePropertySource("mlHost", host, "mlManageUsername", mc.getUsername(),
		    "mlManagePassword", mc.getPassword(), "mlAdminUsername", mc.getAdminUsername(), "mlAdminPassword", mc.getAdminPassword(),
		    "mlManageSimpleSsl", mc.isConfigureSimpleSsl() + "", "mlAdminSimpleSsl", mc.isAdminConfigureSimpleSsl() + "",
		    "mlManageScheme", mc.getScheme(), "mlAdminScheme", mc.getAdminScheme(), "mlAdminPort", mc.getAdminPort() + "",
		    "mlManagePort", mc.getPort() + "");
	    this.manageClient = new ManageClient(new DefaultManageConfigFactory(sps).newManageConfig());
	    initializeAdminManager();
	    if (logger.isInfoEnabled()) {
		    logger.info("Connected to host: " + host);
	    }
    }

    /**
     * Connect to a (presumably) different MarkLogic Management API. The username/password are assumed to work for both
     * the Management API and the Admin API on port 8001.
     *
     * @param host
     * @param username
     * @param password
     */
    public void connect(String host, String username, String password) {
        connect(host, username, password, username, password);
    }

    /**
     * Connect to a (presumably) different MarkLogic Management API.
     *
     * @param host
     * @param username
     * @param password
     * @param adminUsername
     * @param adminPassword
     */
    public void connect(String host, String username, String password, String adminUsername, String adminPassword) {
    	ManageConfig mc = new ManageConfig();
    	mc.setHost(host);
    	mc.setUsername(username);
    	mc.setPassword(password);
    	mc.setAdminUsername(adminUsername);
    	mc.setAdminPassword(adminPassword);
    	connect(host, mc);
    }

    /**
     * Constructs a new ClientHelper, using newClient().
     *
     * @return
     */
    public ClientHelper clientHelper() {
        return new ClientHelper(newClient());
    }

    /**
     * Constructs a new DatabaseClient, using system properties to create an AppConfig instance which is used to create
     * a DatabaseClient.
     *
     * @return
     */
    public DatabaseClient newClient() {
        return new DefaultAppConfigFactory(new SystemPropertySource()).newAppConfig().newDatabaseClient();
    }

    /**
     * Convenience method, seems intuitive that this would apply to the local cluster.
     */
    public void restart() {
        getCluster().restart();
    }

    public Cluster getCluster() {
        return new Cluster(this, adminManager);
    }

    public RestApi restApi(String name) {
        return restApi(name, null);
    }

    public RestApi restApi(String name, Integer port) {
        RestApi r = new RestApi(this, name);
        r.setPort(port);
        r.setObjectMapper(getObjectMapper());
        return r;
    }

    public String createRestApi(String name) {
        return restApi(name).save();
    }

    public String createRestApi(String name, Integer port) {
        return restApi(name, port).save();
    }

    /**
     * Convenience method since the "Default" group is usually the group that's wanted (and often the only one that
     * exists).
     *
     * @return
     */
    public Group group() {
        return group(Group.DEFAULT_GROUP_NAME);
    }

    public Group group(String name) {
        Group g = new Group(this, name);
        return name != null && g.exists() ? getResource(name, new GroupManager(getManageClient()), Group.class) : g;
    }

    public Group getGroup() {
        return group(null);
    }

    /**
     * The vast majority of the time, configuring trace events will be on the default group, so a convenience method is
     * exposed for this use case.
     *
     * @param events
     */
    public void trace(String... events) {
        group().trace(events);
    }

    /**
     * The vast majority of the time, configuring trace events will be on the default group, so a convenience method is
     * exposed for this use case.
     *
     * @param events
     */
    public void untrace(String... events) {
        group().untrace(events);
    }

    public Database db(String name) {
        Database db = new Database(this, name);
        return name != null && db.exists() ? getResource(name, new DatabaseManager(getManageClient()), Database.class)
                : db;
    }

    public Database getDb() {
        return db(null);
    }

    public Forest forest(String name) {
        Forest f = new Forest(this, name);
        return name != null && f.exists() ? getResource(name, new ForestManager(getManageClient()), Forest.class) : f;
    }

    public Forest getForest() {
        return forest(null);
    }

    public Server server(String name) {
        return server(name, null);
    }

    public Server server(String name, Integer port) {
        Server s = new Server(this, name);
        s.setPort(port);
        return name != null && s.exists() ? getResource(name, new ServerManager(getManageClient()), Server.class) : s;
    }

    public Server getServer() {
        return server(null);
    }

    /**
     * In order to get an amp, we need up to 4 things to uniquely identify based on
     * http://docs.marklogic.com/REST/GET/manage/v2/amps/[id-or-name] . TODO Could make this nicer, where a call is made
     * to the amps endpoint to find an amp with the given local name, and if only one exists, just use that.
     *
     * @param localName
     * @return
     */
    public Amp amp(String localName, String namespace, String documentUri, String modulesDatabase) {
        Amp amp = new Amp(this, localName);
        amp.setNamespace(namespace);
        amp.setDocumentUri(documentUri);
        amp.setModulesDatabase(modulesDatabase);
        return localName != null && amp.exists() ? getResource(localName, new AmpManager(getManageClient()), Amp.class,
                amp.getResourceUrlParams()) : amp;
    }

    public Amp getAmp() {
        return amp(null, null, null, null);
    }

    public ExternalSecurity externalSecurity(String name) {
        ExternalSecurity es = new ExternalSecurity(this, name);
        return name != null && es.exists() ? getResource(name, new ExternalSecurityManager(getManageClient()),
                ExternalSecurity.class) : es;
    }

    public ExternalSecurity externalSecurity() {
        return externalSecurity(null);
    }

    public Privilege privilege(String name) {
        Privilege p = new Privilege(this, name);
        return name != null && p.exists() ? getResource(name, new PrivilegeManager(getManageClient()), Privilege.class)
                : p;
    }

    public Privilege getPrivilege() {
        return privilege(null);
    }

    public ProtectedCollection protectedCollection(String name) {
        ProtectedCollection pc = new ProtectedCollection(this, name);
        return name != null && pc.exists() ? getResource(name, new ProtectedCollectionsManager(getManageClient()),
                ProtectedCollection.class) : pc;
    }

    public ProtectedCollection getProtectedCollection() {
        return protectedCollection(null);
    }

    public User user(String name) {
        User u = new User(this, name);
        return name != null && u.exists() ? getResource(name, new UserManager(getManageClient()), User.class) : u;
    }

    public User getUser() {
        return user(null);
    }

    public Role role(String name) {
        Role r = new Role(this, name);
        return name != null && r.exists() ? getResource(name, new RoleManager(getManageClient()), Role.class) : r;
    }

    public Role getRole() {
        return role(null);
    }

    public Task task(String taskId) {
        Task t = new Task(this, taskId);
        return taskId != null && t.exists() ? getResource(taskId, new TaskManager(getManageClient()), Task.class) : t;
    }

    public Task getTask() {
        return task(null);
    }

    protected <T extends Resource> T getResource(String resourceNameOrId, ResourceManager mgr, Class<T> resourceClass,
            String... resourceUrlParams) {
        if (mgr.exists(resourceNameOrId)) {
            return buildFromJson(mgr.getAsJson(resourceNameOrId, resourceUrlParams), resourceClass);
        }
        throw new RuntimeException("Could not find resource with name or ID: " + resourceNameOrId);
    }

    protected <T extends Resource> T buildFromJson(String json, Class<T> clazz) {
        try {
            T resource = getObjectMapper().readerFor(clazz).readValue(json);
            resource.setApi(this);
            resource.setObjectMapper(getObjectMapper());
            return resource;
        } catch (IOException ex) {
            throw new RuntimeException("Unable to build object from json, cause: " + ex.getMessage(), ex);
        }
    }

    public ManageClient getManageClient() {
        return manageClient;
    }

    public void setManageClient(ManageClient client) {
        this.manageClient = client;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AdminManager getAdminManager() {
        return adminManager;
    }

    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
}
