package com.rjrudin.marklogic.mgmt.api;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rjrudin.marklogic.client.LoggingObject;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.ManageConfig;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.admin.AdminConfig;
import com.rjrudin.marklogic.mgmt.admin.AdminManager;
import com.rjrudin.marklogic.mgmt.api.cluster.Cluster;
import com.rjrudin.marklogic.mgmt.api.database.Database;
import com.rjrudin.marklogic.mgmt.api.forest.Forest;
import com.rjrudin.marklogic.mgmt.api.group.Group;
import com.rjrudin.marklogic.mgmt.api.restapi.RestApi;
import com.rjrudin.marklogic.mgmt.api.security.Role;
import com.rjrudin.marklogic.mgmt.api.security.User;
import com.rjrudin.marklogic.mgmt.api.server.Server;
import com.rjrudin.marklogic.mgmt.appservers.ServerManager;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;
import com.rjrudin.marklogic.mgmt.groups.GroupManager;
import com.rjrudin.marklogic.mgmt.security.RoleManager;
import com.rjrudin.marklogic.mgmt.security.UserManager;

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
        ManageConfig mc = manageClient.getManageConfig();
        if (mc.getAdminUsername() != null && mc.getAdminPassword() != null) {
            AdminConfig ac = new AdminConfig(mc.getHost(), 8001, mc.getAdminUsername(), mc.getAdminPassword());
            this.adminManager = new AdminManager(ac);
        }
    }

    protected ObjectMapper buildDefaultObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.setPropertyNamingStrategy(new LowerCaseWithHyphensStrategy());
        m.setSerializationInclusion(Include.NON_NULL);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // This is needed at least for localname on Element instances
        m.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return m;
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

    protected <T extends Resource> T getResource(String name, ResourceManager mgr, Class<T> resourceClass) {
        if (mgr.exists(name)) {
            return buildFromJson(mgr.getAsJson(name), resourceClass);
        }
        throw new RuntimeException("Could not find resource with name: " + name);
    }

    protected <T extends Resource> T buildFromJson(String json, Class<T> clazz) {
        try {
            T resource = getObjectMapper().reader(clazz).readValue(json);
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
