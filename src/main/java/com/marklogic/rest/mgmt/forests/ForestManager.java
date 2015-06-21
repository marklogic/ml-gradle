package com.marklogic.rest.mgmt.forests;

import com.marklogic.rest.mgmt.AbstractManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

public class ForestManager extends AbstractManager {

    private ManageClient client;

    public ForestManager(ManageClient client) {
        this.client = client;
    }

    public void createForestWithName(String name, String host) {
        if (forestExists(name)) {
            logger.info(format("Forest already exists with name: %s", name));
        } else {
            logger.info(format("Creating forest %s on host %s", name, host));
            createForest(format("{\"forest-name\":\"%s\", \"host\":\"%s\"}", name, host));
            logger.info(format("Created forest %s on host %s", name, host));
        }
    }

    public void delete(String nameOrId) {
        delete(nameOrId, "full");
    }

    public void delete(String nameOrId, String level) {
        if (!forestExists(nameOrId)) {
            logger.info(format("Could not find forest with name or ID: %s, so not deleting", nameOrId));
        } else {
            logger.info(format("Deleting forest %s", nameOrId));
            client.delete(format("/manage/v2/forests/%s?level=%s", nameOrId, level));
            logger.info(format("Deleted forest %s", nameOrId));
        }
    }

    public void createForest(String json) {
        client.postJson("/manage/v2/forests", json);
    }

    public boolean forestExists(String nameOrId) {
        Fragment f = client.getXml("/manage/v2/forests");
        return f.elementExists(format("/node()/f:list-items/f:list-item[f:nameref = '%s' or f:idref = '%s']", nameOrId,
                nameOrId));
    }

    public void attachForest(String forestIdOrName, String databaseIdOrName) {
        if (isForestAttached(forestIdOrName)) {
            logger.info(format("Forest %s is already attached to a database, not attaching", forestIdOrName));
            return;
        }
        logger.info(format("Attaching forest %s to database %s", forestIdOrName, databaseIdOrName));
        String path = format("/manage/v2/forests/%s", forestIdOrName);
        client.postForm(path, "state", "attach", "database", databaseIdOrName);
        logger.info(format("Attached forest %s to database %s", forestIdOrName, databaseIdOrName));
    }

    public boolean isForestAttached(String forestIdOrName) {
        Fragment f = client.getXml(format("/manage/v2/forests/%s", forestIdOrName));
        return f.elementExists("/node()/f:relations/f:relation-group[f:typeref = 'databases']");
    }
}
