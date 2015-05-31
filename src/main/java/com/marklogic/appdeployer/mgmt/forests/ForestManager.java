package com.marklogic.appdeployer.mgmt.forests;

import com.marklogic.appdeployer.AbstractManager;
import com.marklogic.appdeployer.mgmt.ManageClient;
import com.marklogic.appdeployer.util.Fragment;

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

    public void createForest(String json) {
        client.postJson("/manage/v2/forests", json);
    }

    public boolean forestExists(String name) {
        Fragment f = client.getXml("/manage/v2/forests");
        return f.elementExists(format("/node()/f:list-items/f:list-item[f:nameref = '%s']", name));
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
