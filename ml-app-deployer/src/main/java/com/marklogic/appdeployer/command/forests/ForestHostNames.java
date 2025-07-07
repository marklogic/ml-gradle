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

import java.util.List;

// This is no longer needed now that it doesn't capture replica host names as well.
// Will remove this in favor of a List<String> in the next PR.
public class ForestHostNames {

	private List<String> primaryForestHostNames;

	public ForestHostNames(List<String> primaryForestHostNames) {
		this.primaryForestHostNames = primaryForestHostNames;
	}

	public List<String> getPrimaryForestHostNames() {
		return primaryForestHostNames;
	}
}
