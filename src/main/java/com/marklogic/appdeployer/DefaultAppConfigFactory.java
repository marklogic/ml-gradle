package com.marklogic.appdeployer;

import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ext.SecurityContextType;
import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.PropertySourceFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class DefaultAppConfigFactory extends PropertySourceFactory implements AppConfigFactory {

	public DefaultAppConfigFactory() {
		super();
	}

	public DefaultAppConfigFactory(PropertySource propertySource) {
		super(propertySource);
	}

	@Override
	public AppConfig newAppConfig() {
		AppConfig c = new AppConfig();

		String prop = null;

		prop = getProperty("mlCatchDeployExceptions");
		if (prop != null) {
			logger.info("Catch deploy exceptions: " + prop);
			c.setCatchDeployExceptions(Boolean.parseBoolean(prop));
		}

		prop = getProperty("mlCatchUndeployExceptions");
		if (prop != null) {
			logger.info("Catch undeploy exceptions: " + prop);
			c.setCatchUndeployExceptions(Boolean.parseBoolean(prop));
		}

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

		prop = getProperty("mlSchemasDatabaseName");
		if (prop != null) {
			logger.info("Schemas database name: " + prop);
			c.setSchemasDatabaseName(prop);
		}

		prop = getProperty("mlTriggersDatabaseName");
		if (prop != null) {
			logger.info("Triggers database name: " + prop);
			c.setTriggersDatabaseName(prop);
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
		 * Set this to true to prevent creating a REST API server by default.
		 */
		prop = getProperty("mlNoRestServer");
		if (prop != null && Boolean.parseBoolean(prop) == true) {
			logger.info("Not creating REST server if no REST config file is found");
			c.setNoRestServer(true);

		}
		/**
		 * If a REST API server is created, it will use the following port. Modules will also be loaded via this port.
		 */
		prop = getProperty("mlRestPort");
		if (prop != null) {
			logger.info("App REST port: " + prop);
			c.setRestPort(Integer.parseInt(prop));
		}

		prop = getProperty("mlRestAuthentication");
		if (prop != null) {
			logger.info("App REST authentication: " + prop);
			c.setRestAuthentication(DatabaseClientFactory.Authentication.valueOfUncased(prop));
			c.setRestSecurityContextType(SecurityContextType.valueOf(prop.toUpperCase()));
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
		 * Client REST API - namely, loading REST API modules such as options, services, and transforms.
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

		prop = getProperty("mlRestCertFile");
		if (prop != null) {
			logger.info("REST cert file: " + prop);
			c.setRestCertFile(prop);
		}

		prop = getProperty("mlRestCertPassword");
		if (prop != null) {
			logger.info("REST cert password: " + prop);
			c.setRestCertPassword(prop);
		}

		prop = getProperty("mlRestExternalName");
		if (prop != null) {
			logger.info("REST external name: " + prop);
			c.setRestExternalName(prop);
		}

		/**
		 * The username and password for a ML user with the rest-admin role that is used for e.g. loading
		 * non-REST API modules via the App Services client REST API, which is defined by the appServicesPort.
		 *
		 * Note that this will first default to restAdminUsername and restAdminPassword if those have been set, and if
		 * not, then username and password.
		 */
		prop = getProperty("mlAppServicesUsername");
		if (prop != null) {
			logger.info("App Services username: " + prop);
			c.setAppServicesUsername(prop);
		} else if (c.getRestAdminUsername() != null) {
			logger.info("App Services username: " + c.getRestAdminUsername());
			c.setAppServicesUsername(c.getRestAdminUsername());
		} else if (mlUsername != null) {
			logger.info("App Services username: " + mlUsername);
			c.setAppServicesUsername(mlUsername);
		}
		prop = getProperty("mlAppServicesPassword");
		if (prop != null) {
			c.setAppServicesPassword(prop);
		} else if (c.getRestAdminPassword() != null) {
			c.setAppServicesPassword(c.getRestAdminPassword());
		} else if (mlPassword != null) {
			c.setAppServicesPassword(mlPassword);
		}

		prop = getProperty("mlAppServicesAuthentication");
		if (prop != null) {
			logger.info("App Services authentication: " + prop);
			c.setAppServicesSecurityContextType(SecurityContextType.valueOf(prop.toUpperCase()));
			c.setAppServicesAuthentication(DatabaseClientFactory.Authentication.valueOfUncased(prop));
		}

		prop = getProperty("mlAppServicesCertFile");
		if (prop != null) {
			logger.info("App Services cert file: " + prop);
			c.setAppServicesCertFile(prop);
		}

		prop = getProperty("mlAppServicesCertPassword");
		if (prop != null) {
			logger.info("App Services cert password: " + prop);
			c.setAppServicesCertPassword(prop);
		}

		prop = getProperty("mlAppServicesExternalName");
		if (prop != null) {
			logger.info("App Services external name: " + prop);
			c.setAppServicesExternalName(prop);
		}

		if ("true".equals(getProperty("mlAppServicesSimpleSsl"))) {
			logger.info("Using simple SSL context and 'ANY' hostname verifier for authenticating against the App-Services server");
			c.setAppServicesSimpleSslConfig();
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

		prop = getProperty("mlCreateForests");
		if (prop != null) {
			logger.info("Create forests for each deployed database: " + prop);
			c.setCreateForests(Boolean.parseBoolean(prop));
		}

		/**
		 * For any database besides the content database, configure the number of forests per host.
		 */
		prop = getProperty("mlForestsPerHost");
		if (prop != null) {
			logger.info("Forests per host: " + prop);
			String[] tokens = prop.split(",");
			for (int i = 0; i < tokens.length; i += 2) {
				c.getForestCounts().put(tokens[i], Integer.parseInt(tokens[i + 1]));
			}
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

		prop = getProperty("mlForestDataDirectory");
		if (prop != null) {
			logger.info("Default forest data directory for all databases: " + prop);
			c.setForestDataDirectory(prop);
		}

		prop = getProperty("mlForestFastDataDirectory");
		if (prop != null) {
			logger.info("Default forest fast data directory for all databases: " + prop);
			c.setForestFastDataDirectory(prop);
		}

		prop = getProperty("mlForestLargeDataDirectory");
		if (prop != null) {
			logger.info("Default forest large data directory for all databases: " + prop);
			c.setForestLargeDataDirectory(prop);
		}

		prop = getProperty("mlReplicaForestDataDirectory");
		if (prop != null) {
			logger.info("Default replica forest data directory for all databases: " + prop);
			c.setReplicaForestDataDirectory(prop);
		}

		prop = getProperty("mlReplicaForestLargeDataDirectory");
		if (prop != null) {
			logger.info("Default replica forest large data directory for all databases: " + prop);
			c.setReplicaForestLargeDataDirectory(prop);
		}

		prop = getProperty("mlReplicaForestFastDataDirectory");
		if (prop != null) {
			logger.info("Default replica forest fast data directory for all databases: " + prop);
			c.setReplicaForestFastDataDirectory(prop);
		}

		prop = getProperty("mlDatabaseDataDirectories");
		if (prop != null) {
			logger.info("Databases and forest data directories: " + prop);
			c.setDatabaseDataDirectories(buildMapFromCommaDelimitedString(prop));
		}

		prop = getProperty("mlDatabaseFastDataDirectories");
		if (prop != null) {
			logger.info("Databases and forest fast data directories: " + prop);
			c.setDatabaseFastDataDirectories(buildMapFromCommaDelimitedString(prop));
		}

		prop = getProperty("mlDatabaseLargeDataDirectories");
		if (prop != null) {
			logger.info("Databases and forest large data directories: " + prop);
			c.setDatabaseLargeDataDirectories(buildMapFromCommaDelimitedString(prop));
		}

		prop = getProperty("mlDatabaseReplicaDataDirectories");
		if (prop != null) {
			logger.info("Databases and replica forest data directories: " + prop);
			c.setDatabaseReplicaDataDirectories(buildMapFromCommaDelimitedString(prop));
		}

		prop = getProperty("mlDatabaseReplicaFastDataDirectories");
		if (prop != null) {
			logger.info("Databases and replica forest fast data directories: " + prop);
			c.setDatabaseReplicaFastDataDirectories(buildMapFromCommaDelimitedString(prop));
		}

		prop = getProperty("mlDatabaseReplicaLargeDataDirectories");
		if (prop != null) {
			logger.info("Databases and replica forest large data directories: " + prop);
			c.setDatabaseReplicaLargeDataDirectories(buildMapFromCommaDelimitedString(prop));
		}

		/**
		 * When undo is invoked on DeployDatabaseCommand (such as via mlUndeploy in ml-gradle), this controls whether
		 * or not forests are deleted, or just their configuration is deleted. If mlDeleteReplicas is set to true, this
		 * has no impact - currently, the forests and their replicas will be deleted for efficiency reasons (results in
		 * fewer calls to the Management REST API.
		 */
		prop = getProperty("mlDeleteForests");
		if (prop != null) {
			logger.info("Delete forests when a database is deleted: " + prop);
			c.setDeleteForests(Boolean.parseBoolean(prop));
		}

		/**
		 * When undo is invoked on DeployDatabaseCommand (such as via mlUndeploy in ml-gradle), this controls whether
		 * primary forests and their replicas are deleted first. Most of the time, you want this set to true
		 * (the default) as otherwise, the database can't be deleted and the Management REST API will throw an error.
		 */
		prop = getProperty("mlDeleteReplicas");
		if (prop != null) {
			logger.info("Delete replicas when a database is deleted: " + prop);
			c.setDeleteReplicas(Boolean.parseBoolean(prop));
		}

		/**
		 * When a REST API server is created, the content database name will default to mlAppName-content. This property
		 * can be used to override that name.
		 */
		prop = getProperty("mlContentDatabaseName");
		if (prop != null) {
			logger.info("Content database name: " + prop);
			c.setContentDatabaseName(prop);
		}

		/**
		 * When a REST API server is created, the modules database name will default to mlAppName-modules. This property
		 * can be used to override that name.
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
		if ("true".equals(getProperty("mlSimpleSsl"))) {
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

		prop = getProperty("mlModuleTimestampsPath");
		if (prop != null) {
			logger.info("Module timestamps path: " + prop);
			c.setModuleTimestampsPath(prop);
		}

		/**
		 * Whether or not to load asset modules in bulk - i.e. in one transaction. Defaults to true.
		 */
		prop = getProperty("mlBulkLoadAssets");
		if (prop != null) {
			logger.info("Bulk load modules: " + prop);
			c.setBulkLoadAssets(Boolean.parseBoolean(prop));
		}

		/**
		 * Whether or not to statically check asset modules after they're loaded - defaults to false.
		 */
		prop = getProperty("mlStaticCheckAssets");
		if (prop != null) {
			logger.info("Statically check asset modules: " + prop);
			c.setStaticCheckAssets(Boolean.parseBoolean(prop));
		}

		/**
		 * Whether or not to attempt to statically check asset library modules after they're loaded - defaults to false.
		 * If mlStaticCheckAssets is true and this is false, and no errors will be thrown for library modules.
		 * See XccAssetLoader in ml-javaclient-util for information on how this tries to check a library module.
		 */
		prop = getProperty("mlStaticCheckLibraryAssets");
		if (prop != null) {
			logger.info("Statically check asset library modules: " + prop);
			c.setStaticCheckLibraryAssets(Boolean.parseBoolean(prop));
		}

		prop = getProperty("mlDeleteTestModules");
		if (prop != null) {
			logger.info("Delete test modules: " + prop);
			c.setDeleteTestModules(Boolean.parseBoolean(prop));
		}

		prop = getProperty("mlDeleteTestModulesPattern");
		if (prop != null) {
			logger.info("Delete test modules pattern: " + prop);
			c.setDeleteTestModulesPattern(prop);
		}

		prop = getProperty("mlModulesLoaderThreadCount");
		if (prop != null) {
			logger.info("Modules loader thread count: " + prop);
			c.setModulesLoaderThreadCount(Integer.parseInt(prop));
		}

		/**
		 * The following properties are all for generating Entity Services artifacts.
		 */
		prop = getProperty("mlModelsPath");
		if (prop != null) {
			logger.info("Entity Services models path: " + prop);
			c.setModelsPath(prop);
		}
		prop = getProperty("mlInstanceConverterPath");
		if (prop != null) {
			logger.info("Entity Services instance converter path: " + prop);
			c.setInstanceConverterPath(prop);
		}
		prop = getProperty("mlGenerateInstanceConverter");
		if (prop != null) {
			logger.info("Entity Services generate instance converter: " + prop);
			c.setGenerateInstanceConverter(Boolean.parseBoolean(prop));
		}
		prop = getProperty("mlGenerateSchema");
		if (prop != null) {
			logger.info("Entity Services generate schema: " + prop);
			c.setGenerateSchema(Boolean.parseBoolean(prop));
		}
		prop = getProperty("mlGenerateSearchOptions");
		if (prop != null) {
			logger.info("Entity Services generate search options: " + prop);
			c.setGenerateSearchOptions(Boolean.parseBoolean(prop));
		}
		prop = getProperty("mlGenerateDatabaseProperties");
		if (prop != null) {
			logger.info("Entity Services generate database properties: " + prop);
			c.setGenerateDatabaseProperties(Boolean.parseBoolean(prop));
		}
		prop = getProperty("mlGenerateExtractionTemplate");
		if (prop != null) {
			logger.info("Entity Services generate extraction template: " + prop);
			c.setGenerateExtractionTemplate(Boolean.parseBoolean(prop));
		}
		// End Entity Services properties

		/**
		 * Sets resource filenames to ignore on ALL commands. Be careful here, in case you have files for different kinds
		 * of resources, but with the same filename (this should be very rare and easily avoided).
		 *
		 * Also that as of version 2.6.0 of ml-app-deployer, this property is processed by AbstractAppDeployer, NOT by
		 * the Command itself. So in order for this property to be applied, you must execute a Command via a subclass of
		 * AbstractAppDeployer (most commonly SimpleAppDeployer).
		 */
		prop = getProperty("mlResourceFilenamesToIgnore");
		if (prop != null) {
			String[] values = prop.split(",");
			logger.info("Ignoring resource filenames: " + Arrays.asList(values));
			c.setResourceFilenamesToIgnore(values);
		}

		prop = getProperty("mlResourceFilenamesToExcludeRegex");
		if (prop != null) {
			logger.info("Excluding resource filenames matching regex: " + prop);
			c.setResourceFilenamesExcludePattern(Pattern.compile(prop));
		}

		prop = getProperty("mlResourceFilenamesToIncludeRegex");
		if (prop != null) {
			logger.info("Including resource filenames matching regex: " + prop);
			c.setResourceFilenamesIncludePattern(Pattern.compile(prop));
		}

		/**
		 * Version 2.9.0 of ml-app-deployer, by default, sorts role files by reading each file and looking at the role
		 * dependencies. You can disable this behavior by setting this property to false.
		 */
		prop = getProperty("mlSortRolesByDependencies");
		if (prop != null) {
			logger.info("Sort roles by dependencies: " + prop);
			c.setSortRolesByDependencies(Boolean.parseBoolean(prop));
		}

		return c;
	}

	protected Map<String, String> buildMapFromCommaDelimitedString(String str) {
		Map<String, String> map = new HashMap<>();
		String[] tokens = str.split(",");
		for (int i = 0; i < tokens.length; i += 2) {
			map.put(tokens[i], tokens[i + 1]);
		}
		return map;
	}
}
