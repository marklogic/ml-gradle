package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.mgmt.api.Resource;

import java.io.File;

/**
 * Interface for objects that can write a Resource object to the appropriate directory within the given ConfigDir.
 */
public interface ResourceWriter {

	File writeResourceAsJson(Resource r, ConfigDir configDir);

	/**
	 * This isn't well-supported yet because most Resource subclasses don't yet have JAXB annotations on them.
	 *
	 * @param r
	 * @param configDir
	 * @return
	 */
	File writeResourceAsXml(Resource r, ConfigDir configDir);

}
