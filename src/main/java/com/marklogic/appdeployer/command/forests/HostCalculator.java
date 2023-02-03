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
package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.mgmt.api.forest.Forest;

import java.util.List;

public interface HostCalculator {

	/**
	 * Calculates which hosts can be used for primary and replica forests. The results are affected by the AppConfig
	 * properties that configure which groups and hosts are allowed to have forests for a particular database.
	 *
	 * In addition, if the database is configured to only have forests on one host, then the list of primary forests
	 * will have a single host. However, the list of replica forests will still have all candidate hosts so that
	 * replicas can still be created.
	 *
	 * @param databaseName
	 * @param context
	 * @param existingPrimaryForests
	 * @return
	 */
	ForestHostNames calculateHostNames(String databaseName, CommandContext context, List<Forest> existingPrimaryForests);

}
