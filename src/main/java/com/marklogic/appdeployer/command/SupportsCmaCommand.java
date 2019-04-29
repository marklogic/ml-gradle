package com.marklogic.appdeployer.command;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.mgmt.api.configuration.Configuration;

/**
 * Commands that wish to optimize the deployment of resources in a single resource directory should implement this
 * interface. It is used by AbstractResourceCommand so that each valid file found in a resource directory can be
 * add as a resource to a CMA configuration.
 */
public interface SupportsCmaCommand {

	boolean cmaShouldBeUsed(CommandContext context);

	/**
	 * @param resource      the contents of the resource file, with all tokens replaced
	 * @param configuration the CMA Configuration object that the payload should be added to
	 */
	void addResourceToConfiguration(ObjectNode resource, Configuration configuration);


}
