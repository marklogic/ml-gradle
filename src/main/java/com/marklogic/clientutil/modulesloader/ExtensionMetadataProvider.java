package com.marklogic.clientutil.modulesloader;

import java.io.File;

public interface ExtensionMetadataProvider {

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(File resourceFile);
}
