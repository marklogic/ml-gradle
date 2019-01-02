package com.marklogic.appdeployer.command;

import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.mapper.ResourceMapper;

/**
 * Commands that wish to optimize the deployment of resources in a single resource directory should implement this
 * interface. It is used by AbstractResourceCommand so that each valid file found in a resource directory can be
 * add as a resource to a CMA configuration.
 */
public interface SupportsCmaCommand {

	boolean cmaShouldBeUsed(CommandContext context);

	/**
	 * @param payload        the contents of the resource file, with all tokens replaced
	 * @param resourceMapper the implementer is likely to use this to convert the payload into a subclass of Resource
	 *                       so that it doesn't matter if the payload is JSON or XML
	 * @param configuration  the CMA Configuration object that the payload should be added to
	 */
	void addResourceToConfiguration(String payload, ResourceMapper resourceMapper, Configuration configuration);

}
