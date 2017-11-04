package com.marklogic.xcc.template;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * Spring-style Template class that handles instantiating an XCC Session and then closing it, allowing the Callback
 * implementation to focus on what to do with the Session.
 */
public class XccTemplate {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ContentSource contentSource;

    public XccTemplate(String uri) {
        try {
            contentSource = ContentSourceFactory.newContentSource(new URI(uri));
            if (logger.isInfoEnabled()) {
                String[] tokens = uri.split("@");
                if (tokens.length > 1) {
                    String hostAndPort = tokens[tokens.length - 1];
                    logger.info("Will submit requests to XDBC server at " + hostAndPort);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public XccTemplate(ContentSource contentSource) {
    	this.contentSource = contentSource;
    }
    
    public <T> T execute(XccCallback<T> callback) {
        Session session = contentSource.newSession();
        try {
            return callback.execute(session);
        } catch (RequestException re) {
            throw new RuntimeException(re);
        } finally {
            session.close();
        }
    }

    /**
     * Convenience method for executing any adhoc query.
     *
     * @param xquery the XQuery statement to execute
     * @return the response from MarkLogic as a String
     */
    public String executeAdhocQuery(String xquery) {
        return execute(new AdhocQueryCallback(xquery));
    }
}
