/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.util;

public class SystemPropertySource implements PropertySource {

    @Override
    public String getProperty(String name) {
        return System.getProperty(name);
    }

}
