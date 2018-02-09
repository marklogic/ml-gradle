package com.marklogic.gradle.xcc;

import java.net.URI;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.xcc.*;
import com.marklogic.xcc.exceptions.RequestException;

public class XccHelper extends LoggingObject {

    private ContentSource contentSource;

    public XccHelper(String uri) {
        try {
            this.contentSource = ContentSourceFactory.newContentSource(new URI(uri));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public XccHelper(String uri, SecurityOptions securityOptions) {
	    try {
		    this.contentSource = ContentSourceFactory.newContentSource(new URI(uri), securityOptions);
	    } catch (Exception ex) {
		    throw new RuntimeException(ex);
	    }
    }

    public XccHelper(ContentSource contentSource) {
    	this.contentSource = contentSource;
    }

    public String executeXquery(String xquery) {
        Session session = contentSource.newSession();
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Executing XQuery: " + xquery);
            }
            AdhocQuery q = session.newAdhocQuery(xquery);
            return session.submitRequest(q).asString();
        } catch (RequestException re) {
            throw new RuntimeException(re);
        } finally {
            session.close();
        }
    }
}
