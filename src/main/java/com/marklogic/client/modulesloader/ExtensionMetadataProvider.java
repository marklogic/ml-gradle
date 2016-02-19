package com.marklogic.client.modulesloader;

import java.io.IOException;
import org.springframework.core.io.Resource;

public interface ExtensionMetadataProvider {

    public ExtensionMetadataAndParams provideExtensionMetadataAndParams(Resource resourceFile) throws IOException;
}
