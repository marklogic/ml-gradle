package com.marklogic.rest.mgmt.security.users;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;

public class UserManager extends AbstractManager {

    private ManageClient client;

    public UserManager(ManageClient client) {
        this.client = client;
    }

    public boolean userExists(String name) {
        return client.getXml("/manage/v2/users").elementExists(
                format("/msec:user-default-list/msec:list-items/msec:list-item[msec:nameref = '%s']", name));
    }

    public void createUser(String json) {
        JsonNode node = parseJson(json);
        String username = node.get("user-name").asText();
        if (userExists(username)) {
            logger.info("User already exists, not creating: " + username);
        } else {
            logger.info("Creating user: " + username);
            client.postJson("/manage/v2/users", json);
            logger.info("Created user: " + username);
        }
    }

    public void deleteUser(String username) {
        if (!userExists(username)) {
            logger.info("User doesn't exist, not deleting: " + username);
        } else {
            logger.info("Deleting user: " + username);
            client.delete("/manage/v2/users/" + username);
            logger.info("Deleted user: " + username);
        }
    }
}
