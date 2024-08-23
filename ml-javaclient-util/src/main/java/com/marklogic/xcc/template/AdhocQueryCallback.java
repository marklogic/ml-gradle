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
