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
package com.marklogic.client.ext.modulesloader;

import com.marklogic.client.DatabaseClient;
import org.springframework.core.io.Resource;

import java.util.Set;

/**
 * Interface for objects that can load a set of modules via the REST API, which is intended to include not just what the
 * REST API calls "assets" (regular modules), but also options, services, transforms, and namespaces.
 *
 * Note that in both of the methods in this interface, the DatabaseClient is used for loading REST extensions. This is
 * to account for the fact that how search options are loaded (i.e. the URI they're written to) differs based on what
 * REST server they're loaded from. But the implementation is not expected to use this DatabaseClient for loading
 * non-REST modules - that is likely done via an instance of AssetFileLoader that uses the App-Services port by
 * default for loading non-REST modules.
 */
public interface ModulesLoader {

	/**
	 * Load modules from the given directory
	 *
	 * @param directory
	 * @param modulesFinder
	 * @param client the DatabaseClient to use for loading REST extensions
	 * @return the set of resources containing all modules written
	 */
	Set<Resource> loadModules(String directory, ModulesFinder modulesFinder, DatabaseClient client);

	/**
	 * Prefer this method when loading modules from multiple paths, as the non-REST modules from all paths should be
	 * loaded in a single batch.
	 *
	 * @param client
	 * @param modulesFinder
	 * @param paths the DatabaseClient to use for loading REST extensions
	 * @return the set of resources containing all modules written
	 */
	Set<Resource> loadModules(DatabaseClient client, ModulesFinder modulesFinder, String... paths);
}
