package com.marklogic.appdeployer;

import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAppConfigFactory extends PropertySourceFactory implements AppConfigFactory {

	public DefaultAppConfigFactory() {
		super();
	}

	;

	public DefaultAppConfigFactory(PropertySource propertySource) {
		super(propertySource);
	}

	@Override
	public AppConfig newAppConfig() {
		AppConfig c = new AppConfig();

		String prop = null;

		/**
		 * mlUsername and mlPassword are used as the default username/password for the admin, rest-admin, and manage-admin
		 * roles when there isn't a specific username/password combo for those roles.
		 */
		String mlUsername = getProperty("mlUsername");
		String mlPassword = getProperty("mlPassword");

		/**
		 * The application name is used as a prefix for default names for a variety of resources, such as REST API servers
		 * and databases.
		 */
		prop = getProperty("mlAppName");
		if (prop != null) {
			logger.info("App name: " + prop);
			c.setName(prop);
		}

		/**
		 * The path to the directory containing all the resource configuration files. Defaults to src/main/ml-config.
		 * mlConfigPath is the preferred one, as its name is consistent with other properties that refer to a path.
		 * mlConfigDir is deprecated but still supported.
		 */
		prop = getProperty("mlConfigDir");
		if (prop != null) {
			logger.info("mlConfigDir is deprecated; please use mlConfigPath; Config dir: " + prop);
			c.setConfigDir(new ConfigDir(new File(prop)));
		}
		prop = getProperty("mlConfigPath");
		if (prop != null) {
			logger.info("Config path: " + prop);
			c.setConfigDir(new ConfigDir(new File(prop)));
		}

		/**
		 * Defines the path to files that should be loaded into a schemas database. Defaults to src/main/ml-schemas.
		 */
		prop = getProperty("mlSchemasPath");
		if (prop != null) {
			logger.info("Schemas path: " + prop);
			c.setSchemasPath(prop);
		}

		/**
		 * Defines the MarkLogic host that requests should be sent to. Defaults to localhost.
		 */
		prop = getProperty("mlHost");
		if (prop != null) {
			logger.info("App host: " + prop);
			c.setHost(prop);
		}

		/**
		 * If a REST API server is created, it will use the following port. Modules will also be loaded via this port.
		 */
		prop = getProperty("mlRestPort");
		if (prop != null) {
			logger.info("App REST port: " + prop);
			c.setRestPort(Integer.parseInt(prop));
		}

		/**
		 * If a test REST API server is created, it will use the following port.
		 */
		prop = getProperty("mlTestRestPort");
		if (prop != null) {
			logger.info("App test REST port: " + prop);
			c.setTestRestPort(Integer.parseInt(prop));
		}

		/**
		 * Defaults to port 8000. In rare cases, the ML App-Services app server will have been changed to listen on a
		 * different port, in which case you can set this to that port.
		 */
		prop = getProperty("mlAppServicesPort");
		if (prop != null) {
			logger.info("App services port: " + prop);
			c.setAppServicesPort(Integer.parseInt(prop));
		}

		/**
		 * The username and password for a ML user with the rest-admin role. This user is used for operations against the
		 * Client REST API - namely, loading modules.
		 */
		prop = getProperty("mlRestAdminUsername");
		if (prop != null) {
			logger.info("REST admin username: " + prop);
			c.setRestAdminUsername(prop);
		} else if (mlUsername != null) {
			logger.info("REST admin username: " + mlUsername);
			c.setRestAdminUsername(mlUsername);
		}
		prop = getProperty("mlRestAdminPassword");
		if (prop != null) {
			c.setRestAdminPassword(prop);
		} else if (mlPassword != null) {
			c.setRestAdminPassword(mlPassword);
		}

		/**
		 * When a content database is created, this property can be used to control the number of forests per host for
		 * that database.
		 */
		prop = getProperty("mlContentForestsPerHost");
		if (prop != null) {
			logger.info("Content forests per host: " + prop);
			c.setContentForestsPerHost(Integer.parseInt(prop));
		}

		/**
		 * This property can specify a comma-delimited list of database names and replica counts as a simple way of
		 * setting up forest replicas - e.g. Documents,1,Security,2.
		 */
		prop = getProperty("mlDatabaseNamesAndReplicaCounts");
		if (prop != null) {
			logger.info("Database names and replica counts: " + prop);
			c.setDatabaseNamesAndReplicaCounts(prop);
		}

		/**
		 * When a REST API server is created, the name will default to mlAppName-modules. This property can be used to
		 * override that name.
		 */
		prop = getProperty("mlModulesDatabaseName");
		if (prop != null) {
			logger.info("Modules database name: " + prop);
			c.setModulesDatabaseName(prop);
		}

		/**
		 * When modules are loaded via the Client REST API, if the app server requires an SSL connection, then
		 * setting this property will force the simplest SSL connection to be created.
		 */
		if (getProperty("mlSimpleSsl") != null) {
			logger.info(
				"Using simple SSL context and 'ANY' hostname verifier for authenticating against client REST API server");
			c.setSimpleSslConfig();
		}

		/**
		 * Specifies the path for flexrep configuration files; used by DeployFlexrepCommand.
		 */
		prop = getProperty("mlFlexrepPath");
		if (prop != null) {
			logger.info("Flexrep path: " + prop);
			c.setFlexrepPath(prop);
		}

		/**
		 * "Default" is the assumed group for group-specific resources, such as app servers and scheduled tasks. This
		 * property can be set to override that.
		 */
		prop = getProperty("mlGroupName");
		if (prop != null) {
			logger.info("Group name: " + prop);
			c.setGroupName(prop);
		}

		/**
		 * When modules are loaded via the Client REST API, this property can specify a comma-delimited set of role/capability
		 * permissions - e.g. rest-reader,read,rest-writer,update.
		 */
		prop = getProperty("mlModulePermissions");
		if (prop != null) {
			logger.info("Module permissions: " + prop);
			c.setModulePermissions(prop);
		}

		/**
		 * When modules are loaded via the Client REST API, this property can specify a comma-delimited set of extensions
		 * for files that should be loaded as binaries.
		 */
		prop = getProperty("mlAdditionalBinaryExtensions");
		if (prop != null) {
			String[] values = prop.split(",");
			logger.info("Additional binary extensions for loading modules: " + Arrays.asList(values));
			c.setAdditionalBinaryExtensions(values);
		}

		/**
		 * By default, tokens in module files will be replaced. This property can be used to enable/disable that behavior.
		 */
		prop = getProperty("mlReplaceTokensInModules");
		if (prop != null) {
			logger.info("Replace tokens in modules: " + prop);
			c.setReplaceTokensInModules(Boolean.parseBoolean(prop));
		}

		/**
		 * To mimic Roxy behavior, tokens in modules are expected to start with "@ml.". If you do not want this behavior,
		 * you can set this property to false to disable it.
		 */
		prop = getProperty("mlUseRoxyTokenPrefix");
		if (prop != null) {
			logger.info("Use Roxy token prefix of '@ml.': " + prop);
			c.setUseRoxyTokenPrefix(Boolean.parseBoolean(prop));
		}

		/**
		 * Comma-separated list of paths for loading modules. Defaults to src/main/ml-modules.
		 */
		prop = getProperty("mlModulePaths");
		if (prop != null) {
			logger.info("Module paths: " + prop);
			String[] paths = prop.split(",");
			// Ensure we have a modifiable list
			List<String> list = new ArrayList<>();
			for (String s : paths) {
				list.add(s);
			}
			c.setModulePaths(list);
		}

		return c;
	}

}
