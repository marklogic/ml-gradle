package com.marklogic.client.schemasloader;

import java.io.File;
import java.util.List;


public interface SchemasFinder {

    public List<File> findSchemas(File dir);

}
