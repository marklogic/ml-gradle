package com.marklogic.client.modulesloader;

import java.io.File;

public interface ExtensionMetadataProvider {

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(File resourceFile);
}
