package com.marklogic.client.ext.modulesloader;

import org.springframework.core.io.Resource;

public interface ExtensionMetadataProvider {

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(Resource resourceFile);
}
