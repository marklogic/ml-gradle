package com.marklogic.client.ext.modulesloader;

import com.marklogic.client.DatabaseClient;
import org.springframework.core.io.Resource;

import java.util.Set;

/**
 * Interface for objects that can load a set of modules via the REST API, which is intended to include not just what the
 * REST API calls "assets" (regular modules), but also options, services, transforms, and namespaces.
 */
public interface ModulesLoader {

	/**
	 * Use the given DatabaseClient to load modules found in the given directory. Return a set containing any files that
	 * were loaded.
	 *
	 * @param directory
	 * @param modulesFinder
	 * @param client
	 * @return
	 */
	Set<Resource> loadModules(String directory, ModulesFinder modulesFinder, DatabaseClient client);

	/**
	 * Prefer this method when loading modules from multiple paths, as the non-REST modules from all paths should be
	 * loaded in a single batch.
	 *
	 * @param client
	 * @param modulesFinder
	 * @param paths
	 * @return
	 */
	Set<Resource> loadModules(DatabaseClient client, ModulesFinder modulesFinder, String... paths);
}
