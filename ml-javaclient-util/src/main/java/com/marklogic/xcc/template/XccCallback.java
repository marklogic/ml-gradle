/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.xcc.template;

import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;

/**
 * Spring-style callback interface that provides an open Session to an implementation, which then doesn't have to worry
 * about how to open or close the Session.
 */
public interface XccCallback<T> {

    public T execute(Session session) throws RequestException;
}
