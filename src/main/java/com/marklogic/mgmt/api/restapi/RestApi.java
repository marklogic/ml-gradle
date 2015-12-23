package com.marklogic.mgmt.api.restapi;

import org.springframework.http.ResponseEntity;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.ApiObject;
import com.marklogic.mgmt.restapis.RestApiManager;

public class RestApi extends ApiObject {

    private String name;
    private String group;
    private String database;
    private String modulesDatabase;
    private Integer port;
    private Boolean xdbcEnabled;
    private Integer forestsPerHost;
    private String errorFormat;

    private API api;

    public RestApi(API api, String name) {
        this.api = api;
        this.name = name;
    }

    @Override
    public String getJson() {
        return String.format("{\"rest-api\":%s}", super.getJson());
    }

    public String save() {
        ResponseEntity<String> re = new RestApiManager(api.getManageClient()).createRestApi(name, getJson());
        if (re == null) {
            return String.format("REST API with name %s already exists", name);
        } else {
            return String.format("Created REST API with name %s", name);
        }
    }

    public void delete() {
        delete(true, true);
    }

    public String delete(boolean deleteContent, boolean deleteModules) {
        String path = "/v1/rest-apis/" + name + "?";
        if (deleteContent) {
            path += "include=content";
        }
        if (deleteModules) {
            path += "&include=modules";
        }
        api.getManageClient().delete(path);
        return "Deleted REST API at path: " + path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getModulesDatabase() {
        return modulesDatabase;
    }

    public void setModulesDatabase(String modulesDatabase) {
        this.modulesDatabase = modulesDatabase;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getXdbcEnabled() {
        return xdbcEnabled;
    }

    public void setXdbcEnabled(Boolean xdbcEnabled) {
        this.xdbcEnabled = xdbcEnabled;
    }

    public Integer getForestsPerHost() {
        return forestsPerHost;
    }

    public void setForestsPerHost(Integer forestsPerHost) {
        this.forestsPerHost = forestsPerHost;
    }

    public String getErrorFormat() {
        return errorFormat;
    }

    public void setErrorFormat(String errorFormat) {
        this.errorFormat = errorFormat;
    }
}
