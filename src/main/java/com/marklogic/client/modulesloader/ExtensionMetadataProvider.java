package com.marklogic.client.modulesloader;

import org.springframework.core.io.Resource;

public interface ExtensionMetadataProvider {

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(Resource resourceFile);
}
