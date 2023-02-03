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
