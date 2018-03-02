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
import org.springframework.util.FileCopyUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultResourceWriter extends LoggingObject implements ResourceWriter {

	private Map<Class<?>, Function<ConfigDir, File>> functionMap = new HashMap<>();

	/**
	 * Would be nice to handle this in the Resource class itself, but then the mgmt.api package has to depend on the
	 * appdeployer package, which is the current home of ConfigDir. Sigh...
	 */
	public DefaultResourceWriter() {
		addFunction(Cluster.class, configDir -> configDir.getClustersDir());
		addFunction(Database.class, configDir -> configDir.getDatabasesDir());
		addFunction(Forest.class, configDir -> configDir.getForestsDir());
		addFunction(Group.class, configDir -> configDir.getGroupsDir());

		// Security stuff
		addFunction(Amp.class, configDir -> configDir.getAmpsDir());
		addFunction(ExternalSecurity.class, configDir -> configDir.getExternalSecuritiesDir());
		addFunction(Privilege.class, configDir -> configDir.getPrivilegesDir());
		addFunction(ProtectedCollection.class, configDir -> configDir.getProtectedCollectionsDir());
		addFunction(Role.class, configDir -> configDir.getRolesDir());
		addFunction(User.class, configDir -> configDir.getUsersDir());

		addFunction(Server.class, configDir -> configDir.getServersDir());
		addFunction(Task.class, configDir -> configDir.getTasksDir());
	}

	/**
	 * Allow clients to override or add their own functions for writing a resource.
	 *
	 * @param resourceClass
	 * @param function
	 */
	public void addFunction(Class<?> resourceClass, Function<ConfigDir, File> function) {
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
		Function<ConfigDir, File> function = functionMap.get(r.getClass());
		if (function == null) {
			throw new IllegalArgumentException("Unsupported resource class type:" + r.getClass());
		}

		final File dir = function.apply(configDir);
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
