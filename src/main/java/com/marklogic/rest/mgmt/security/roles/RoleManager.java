package com.marklogic.rest.mgmt.security.roles;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;

public class RoleManager extends AbstractManager {

    private ManageClient client;

    public RoleManager(ManageClient client) {
        this.client = client;
    }

    public boolean roleExists(String name) {
        return client.getXml("/manage/v2/roles").elementExists(
                format("/msec:role-default-list/msec:list-items/msec:list-item[msec:nameref = '%s']", name));
    }

    public void createRole(String json) {
        JsonNode node = parseJson(json);
        String rolename = node.get("role-name").asText();
        if (roleExists(rolename)) {
            logger.info("Role already exists, not creating: " + rolename);
        } else {
            logger.info("Creating role: " + rolename);
            client.postJson("/manage/v2/roles", json);
            logger.info("Created role: " + rolename);
        }
    }

    public void deleteRole(String name) {
        if (!roleExists(name)) {
            logger.info("Role doesn't exist, not deleting: " + name);
        } else {
            logger.info("Deleting role: " + name);
            client.delete("/manage/v2/roles/" + name);
            logger.info("Deleted role: " + name);
        }
    }

}
