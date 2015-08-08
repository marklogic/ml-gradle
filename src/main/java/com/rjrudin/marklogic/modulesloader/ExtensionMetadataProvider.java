package com.rjrudin.marklogic.modulesloader;

import java.io.File;

public interface ExtensionMetadataProvider {

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(File resourceFile);
}
