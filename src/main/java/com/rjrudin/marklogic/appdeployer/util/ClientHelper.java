package com.rjrudin.marklogic.appdeployer.util;

import com.marklogic.client.DatabaseClient;

/**
 * TODO Should be able to move this all into the ClientHelper parent class.
 */
public class ClientHelper extends com.rjrudin.marklogic.client.ClientHelper {

    public ClientHelper(DatabaseClient client) {
        super(client);
    }

    public String eval(String expr) {
        return getClient().newServerEval().xquery(expr).evalAs(String.class);
    }

    public void release() {
        getClient().release();
    }
}
