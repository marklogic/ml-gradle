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
    	logger.info("uri: " + uri);
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

    public XccTemplate(String host, int port, String username, String password, String contentDatabaseName) {
	    char[] charPassword;
	    if (password != null) {
		    charPassword = password.toCharArray();
	    } else {
		    charPassword = new char[]{};
	    }

	    if (contentDatabaseName != null) {
		    this.contentSource = ContentSourceFactory.newContentSource(host, port, username, charPassword, contentDatabaseName);
	    } else {
		    this.contentSource = ContentSourceFactory.newContentSource(host, port, username, charPassword);
	    }
    }

    public XccTemplate(ContentSource contentSource) {
    	this.contentSource = contentSource;
    }

    public <T> T execute(XccCallback<T> callback) {
		try (Session session = contentSource.newSession()) {
			return callback.execute(session);
		} catch (RequestException re) {
			throw new RuntimeException(re);
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
