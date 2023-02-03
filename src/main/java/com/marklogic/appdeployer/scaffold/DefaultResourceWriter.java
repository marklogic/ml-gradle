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
package com.marklogic.appdeployer.scaffold;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.cluster.Cluster;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.api.security.*;
import com.marklogic.mgmt.api.server.Server;
import com.marklogic.mgmt.api.task.Task;
import com.marklogic.mgmt.api.trigger.Trigger;
import org.springframework.util.FileCopyUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DefaultResourceWriter extends LoggingObject implements ResourceWriter {

	private Map<Class<?>, BiFunction<ConfigDir, Resource, File>> functionMap = new HashMap<>();

	/**
	 * Would be nice to handle this in the Resource class itself, but then the mgmt.api package has to depend on the
	 * appdeployer package, which is the current home of ConfigDir. Sigh...
	 */
	public DefaultResourceWriter() {
		addFunction(Cluster.class, (configDir, resource) -> configDir.getClustersDir());
		addFunction(Database.class, (configDir, resource) -> configDir.getDatabasesDir());
		addFunction(Forest.class, (configDir, resource) -> configDir.getForestsDir());
		addFunction(Group.class, (configDir, resource) -> configDir.getGroupsDir());

		// Security stuff
		addFunction(Amp.class, (configDir, resource) -> configDir.getAmpsDir());
		addFunction(ExternalSecurity.class, (configDir, resource) -> configDir.getExternalSecuritiesDir());
		addFunction(Privilege.class, (configDir, resource) -> configDir.getPrivilegesDir());
		addFunction(ProtectedCollection.class, (configDir, resource) -> configDir.getProtectedCollectionsDir());
		addFunction(Role.class, (configDir, resource) -> configDir.getRolesDir());
		addFunction(User.class, (configDir, resource) -> configDir.getUsersDir());

		addFunction(Server.class, (configDir, resource) -> configDir.getServersDir());
		addFunction(Task.class, (configDir, resource) -> configDir.getTasksDir());
		addFunction(Trigger.class, (configDir, resource) -> configDir.getTriggersDir(((Trigger) resource).getDatabaseName()));
	}

	/**
	 * Allow clients to override or add their own functions for writing a resource.
	 *
	 * @param resourceClass
	 * @param function
	 */
	public void addFunction(Class<?> resourceClass, BiFunction<ConfigDir, Resource, File> function) {
		functionMap.put(resourceClass, function);
	}

	@Override
	public File writeResourceAsJson(Resource r, ConfigDir configDir) {
		final File file = determineResourceFile(r, configDir, ".json");
		try {
			FileCopyUtils.copy(r.getJson().getBytes(), file);
			return file;
		} catch (IOException ex) {
			throw new RuntimeException("Unable to write to file: " + file.getAbsolutePath() + "; cause: " + ex.getMessage(), ex);
		}
	}

	@Override
	public File writeResourceAsXml(Resource r, ConfigDir configDir) {
		final File file = determineResourceFile(r, configDir, ".xml");
		try {
			JAXBContext context = JAXBContext.newInstance(r.getClass());
			context.createMarshaller().marshal(r, file);
			return file;
		} catch (JAXBException ex) {
			throw new RuntimeException("Unable to write to file: " + file.getAbsolutePath() + "; cause: " + ex.getMessage(), ex);
		}
	}

	protected File determineResourceFile(Resource r, ConfigDir configDir, String extension) {
		BiFunction<ConfigDir, Resource, File> function = functionMap.get(r.getClass());
		if (function == null) {
			throw new IllegalArgumentException("Unsupported resource class type:" + r.getClass());
		}

		final File dir = function.apply(configDir, r);
		dir.mkdirs();
		final File file = new File(dir, buildFilename(r, extension));

		if (logger.isInfoEnabled()) {
			logger.info("Writing file: " + file.getAbsolutePath());
		}

		return file;
	}

	protected String buildFilename(Resource resource, String extension) {
		return resource.getClass().getSimpleName() + "-" + System.currentTimeMillis() + extension;
	}
}
