package com.rjrudin.marklogic.mgmt.api;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rjrudin.marklogic.client.LoggingObject;
import com.rjrudin.marklogic.mgmt.ManageClient;
import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.database.Database;
import com.rjrudin.marklogic.mgmt.api.forest.Forest;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;
import com.rjrudin.marklogic.mgmt.forests.ForestManager;

/**
 * Big facade-style class for the MarkLogic Management API. Use this to instantiate or access any resource, as it will
 * handle dependencies for those resources.
 */
public class API extends LoggingObject {

    private ManageClient manageClient;
    private ObjectMapper objectMapper;

    public API(ManageClient client) {
        this.manageClient = client;
        setObjectMapper(buildDefaultObjectMapper());
    }

    public API(ManageClient client, ObjectMapper mapper) {
        this.manageClient = client;
        this.objectMapper = mapper;
    }

    protected ObjectMapper buildDefaultObjectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.setPropertyNamingStrategy(new LowerCaseWithHyphensStrategy());
        m.setSerializationInclusion(Include.NON_NULL);
        m.enable(SerializationFeature.INDENT_OUTPUT);
        m.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        /**
         * This is needed at least for localname on Element instances
         */
        m.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return m;
    }

    public Database getDatabase(String name) {
        return getResource(name, new DatabaseManager(getManageClient()), Database.class);
    }

    public Database newDatabase(String name) {
        return new Database(this, name);
    }

    public Forest getForest(String name) {
        return getResource(name, new ForestManager(getManageClient()), Forest.class);
    }

    public Forest newForest(String name) {
        return new Forest(this, name);
    }

    /**
     * Reusable method for getting an existing resource and populating its dependencies.
     * 
     * @param name
     * @param mgr
     * @param resourceClass
     * @return
     */
    protected <T extends Resource> T getResource(String name, ResourceManager mgr, Class<T> resourceClass) {
        if (mgr.exists(name)) {
            return buildFromJson(mgr.getAsJson(name), resourceClass);
        }
        throw new RuntimeException("Could not find resource with name: " + name);
    }

    /**
     * Useful method for instantiating a Resource from JSON and populating its dependencies.
     * 
     * @param json
     * @param clazz
     * @return
     */
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
}
