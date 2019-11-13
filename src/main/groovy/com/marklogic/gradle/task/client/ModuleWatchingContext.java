package com.marklogic.gradle.task.client;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.modulesloader.ModulesLoader;

public class ModuleWatchingContext {

	private ModulesLoader modulesLoader;
	private AppConfig appConfig;
	private DatabaseClient databaseClient;

	public ModuleWatchingContext(ModulesLoader modulesLoader, AppConfig appConfig, DatabaseClient databaseClient) {
		this.modulesLoader = modulesLoader;
		this.appConfig = appConfig;
		this.databaseClient = databaseClient;
	}

	public ModulesLoader getModulesLoader() {
		return modulesLoader;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}
}
