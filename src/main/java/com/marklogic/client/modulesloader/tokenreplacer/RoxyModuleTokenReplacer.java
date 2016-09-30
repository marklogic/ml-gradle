package com.marklogic.client.modulesloader.tokenreplacer;

import java.io.File;

/**
 * Loads properties from typical Roxy locations of "deploy/default.properties", "deploy/build.properties", and
 * "deploy/local.properties", if any of those exist.
 */
public class RoxyModuleTokenReplacer extends DefaultModuleTokenReplacer {

    public RoxyModuleTokenReplacer() {
        super();
        setPropertyPrefix("@ml.");
        addPropertiesSource(new FilePropertiesSource(new File("deploy/default.properties")));
        addPropertiesSource(new FilePropertiesSource(new File("deploy/build.properties")));
        addPropertiesSource(new FilePropertiesSource(new File("deploy/local.properties")));
    }

}
