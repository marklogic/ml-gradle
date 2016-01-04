package com.marklogic.xcc.template;

import com.marklogic.xcc.AdhocQuery;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * Simple callback for executing an adhoc query. Could be subclassed for a variety of queries, such as getting all the
 * URIs in a collection or retrieving a single document.
 */
public class AdhocQueryCallback implements XccCallback<String> {

    private String xquery;

    public AdhocQueryCallback(String xquery) {
        this.xquery = xquery;
    }

    @Override
    public String execute(Session session) throws RequestException {
        AdhocQuery q = session.newAdhocQuery(xquery);
        return session.submitRequest(q).asString();
    }

}
