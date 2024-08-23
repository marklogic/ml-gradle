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
package com.marklogic.mgmt.api.restapi;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.ApiObject;
import com.marklogic.mgmt.resource.restapis.RestApiManager;
import org.springframework.http.ResponseEntity;

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
        ResponseEntity<String> re = new RestApiManager(api.getManageClient(), this.group).createRestApi(name, getJson());
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
