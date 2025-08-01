/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LoggingObject {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String format(String s, Object... args) {
        return String.format(s, args);
    }

}
