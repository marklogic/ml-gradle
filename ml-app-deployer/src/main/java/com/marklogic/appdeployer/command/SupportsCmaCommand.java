/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
