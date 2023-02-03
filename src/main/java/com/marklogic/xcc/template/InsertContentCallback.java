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

import java.io.File;

import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.DocumentFormat;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * Simple callback for inserting a single document.
 */
public class InsertContentCallback implements XccCallback<Void> {

    private String uri;
    private File file;
    private DocumentFormat format;

    public InsertContentCallback(String uri, File file, DocumentFormat format) {
        this.uri = uri;
        this.file = file;
        this.format = format;
    }

    public Void execute(Session session) throws RequestException {
        ContentCreateOptions opts = new ContentCreateOptions();
        opts.setFormat(format);
        Content c = ContentFactory.newContent(uri, file, opts);
        session.insertContent(c);
        return null;
    }
}
