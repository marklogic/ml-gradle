package com.marklogic.client.modulesloader;

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
