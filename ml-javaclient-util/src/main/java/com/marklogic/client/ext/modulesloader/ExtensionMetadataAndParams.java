/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.modulesloader;

import java.util.List;

import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.ResourceExtensionsManager.MethodParameters;

public class ExtensionMetadataAndParams {

    public ExtensionMetadata metadata;
    public List<MethodParameters> methods;

    public ExtensionMetadataAndParams(ExtensionMetadata metadata, List<MethodParameters> params) {
        super();
        this.metadata = metadata;
        this.methods = params;
    }

}
