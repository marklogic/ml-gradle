package com.marklogic.client.modulesloader;

import java.io.File;

public interface ModulesFinder {

    public Modules findModules(File dir);
}
