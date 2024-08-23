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
package com.marklogic.appdeployer.command.es;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ext.es.CodeGenerationRequest;
import com.marklogic.client.ext.es.EntityServicesManager;
import com.marklogic.client.ext.es.GeneratedCode;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Generates Entity Services model artifacts based on model definitions. This is implemented as a command, though it's
 * not likely to be invoked as part of e.g. an "mlDeploy" call in ml-gradle. But implementing it as a command does
 * allow for that possibility, and it's also easy to invoke by itself in ml-gradle too.
 */
public class GenerateModelArtifactsCommand extends AbstractCommand {

	// Should be very rare to need to override this, but just in case
	private String optionsPath = "options";

	public GenerateModelArtifactsCommand() {
		int order = SortOrderConstants.LOAD_SCHEMAS - 1;
		if (SortOrderConstants.LOAD_SCHEMAS > SortOrderConstants.LOAD_MODULES) {
			order = SortOrderConstants.LOAD_MODULES - 1;
		}
		setExecuteSortOrder(order);
	}

	@Override
	public void execute(CommandContext context) {
		AppConfig appConfig = context.getAppConfig();
		String modelsPath = appConfig.getModelsPath();
		File modelsDir = new File(modelsPath);
		if (modelsDir.exists()) {
			DatabaseClient client = buildDatabaseClient(appConfig);
			try {
				EntityServicesManager mgr = new EntityServicesManager(client);
				for (File f : modelsDir.listFiles()) {
					GeneratedCode code = loadModelDefinition(appConfig, f, mgr);

					File modulesDir = selectModulesDir(appConfig);
					modulesDir.mkdirs();

					generateInstanceConverter(appConfig, code, modulesDir);
					generateSearchOptions(code, modulesDir);
					generateDatabaseProperties(appConfig, code);
					generateSchema(appConfig, code);
					generateExtractionTemplate(appConfig, code);
				}
			} finally {
				client.release();
			}
		}
	}

	protected DatabaseClient buildDatabaseClient(AppConfig appConfig) {
		String db = appConfig.getModelsDatabase();
		if (StringUtils.isEmpty(db)) {
			db = appConfig.getContentDatabaseName();
		}
		if (logger.isInfoEnabled()) {
			logger.info("Will load Entity Services models into database: " + db);
		}
		return appConfig.newAppServicesDatabaseClient(db);
	}

	protected GeneratedCode loadModelDefinition(AppConfig appConfig, File f, EntityServicesManager mgr) {
		return loadModelDefinition(buildCodeGenerationRequest(appConfig),f,mgr);
	}

	protected GeneratedCode loadModelDefinition(CodeGenerationRequest codeGenerationRequest, File f, EntityServicesManager mgr) {
		String name = f.getName();
		String modelDefinition = null;
		try {
			modelDefinition = new String(FileCopyUtils.copyToByteArray(f));
		} catch (IOException e) {
			throw new RuntimeException("Unable to read model definition from file: " + f.getAbsolutePath(), e);
		}
		String modelUri = mgr.loadModel(name, modelDefinition);
		return mgr.generateCode(modelUri, codeGenerationRequest);
	}

	protected CodeGenerationRequest buildCodeGenerationRequest(AppConfig appConfig) {
		CodeGenerationRequest request = new CodeGenerationRequest();
		request.setGenerateDatabaseProperties(appConfig.isGenerateDatabaseProperties());
		request.setGenerateExtractionTemplate(appConfig.isGenerateExtractionTemplate());
		request.setGenerateInstanceConverter(appConfig.isGenerateInstanceConverter());
		request.setGenerateSchema(appConfig.isGenerateSchema());
		request.setGenerateSearchOptions(appConfig.isGenerateSearchOptions());
		return request;
	}

	/**
	 * Selects the last path from appConfig.getModulePaths, the assumption being that any paths before the last one
	 * are for dependencies.
	 *
	 * @param appConfig
	 * @return
	 */
	protected File selectModulesDir(AppConfig appConfig) {
		List<String> modulePaths = appConfig.getModulePaths();
		String lastPath = modulePaths.get(modulePaths.size() - 1);
		return new File(lastPath);
	}

	protected void generateInstanceConverter(AppConfig appConfig, GeneratedCode code, File modulesDir) {
		String instanceConverter = code.getInstanceConverter();
		if (instanceConverter != null) {
			File esDir = new File(modulesDir, appConfig.getInstanceConverterPath());
			esDir.mkdirs();
			File out = new File(esDir, code.getTitle() + "-" + code.getVersion() + ".xqy");
			String logMessage = "Wrote instance converter to: ";
			if (out.exists()) {
				if (!fileHasDifferentContent(out, instanceConverter)) {
					if (logger.isInfoEnabled()) {
						logger.info("Instance converter matches file, so not modifying: " + out.getAbsolutePath());
					}
					return;
				}
				out = new File(esDir, code.getTitle() + "-" + code.getVersion() + ".xqy.GENERATED");
				logMessage = "Instance converter does not match existing file, so writing to: ";
			}
			try {
				FileCopyUtils.copy(instanceConverter.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info(logMessage + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write instance converter to: " + out.getAbsolutePath(), e);
			}
		}
	}

	protected void generateSearchOptions(GeneratedCode code, File modulesDir) {
		String searchOptions = code.getSearchOptions();
		if (searchOptions != null) {
			File optionsDir = new File(modulesDir, optionsPath);
			optionsDir.mkdirs();
			File out = new File(optionsDir, code.getTitle() + ".xml");
			String logMessage = "Wrote search options to: ";
			if (out.exists()) {
				if (!fileHasDifferentContent(out, searchOptions)) {
					if (logger.isInfoEnabled()) {
						logger.info("Search options matches file, so not modifying: " + out.getAbsolutePath());
					}
					return;
				}
				out = new File(optionsDir, code.getTitle() + "xml.GENERATED");
				logMessage = "Search options does not match existing file, so writing to: ";
			}
			try {
				FileCopyUtils.copy(searchOptions.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info(logMessage + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write search options to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * The content-database.json file generated by Entity Services has a reference to a schemas database. So if a config
	 * file for a schemas database doesn't exist, one is generated so that the next deployment succeeds.
	 *
	 * @param appConfig
	 * @param code
	 */
	protected void generateDatabaseProperties(AppConfig appConfig, GeneratedCode code) {
		String props = code.getDatabaseProperties();
		if (props != null) {
			File dbDir = appConfig.getFirstConfigDir().getDatabasesDir();
			dbDir.mkdirs();
			File out = new File(dbDir, "content-database.json");
			String logMessage = "Wrote database properties to: ";
			if (out.exists()) {
				if (!fileHasDifferentContent(out, props)) {
					if (logger.isInfoEnabled()) {
						logger.info("Database properties matches file, so not modifying: " + out.getAbsolutePath());
					}
					return;
				}
				out = new File(dbDir, "content-database.json.GENERATED");
				logMessage = "Database properties does not match existing file, so writing to: ";
			}
			try {
				FileCopyUtils.copy(props.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info(logMessage + out.getAbsolutePath());
				}

				// Makes some assumptions about the schemas file
				File schemasFile = new File(dbDir, "schemas-database.json");
				if (!schemasFile.exists()) {
					String payload = "{\"database-name\": \"%%SCHEMAS_DATABASE%%\"}";
					try {
						FileCopyUtils.copy(payload.getBytes(), schemasFile);
					} catch (IOException ex) {
						logger.warn("Unable to write schemas database payload to file: " + schemasFile.getAbsolutePath(), ex);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write database properties to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	protected void generateSchema(AppConfig appConfig, GeneratedCode code) {
		String schema = code.getSchema();
		if (schema != null) {
			File dir = new File(appConfig.getSchemaPaths().get(0));
			dir.mkdirs();
			File out = new File(dir, code.getTitle() + "-" + code.getVersion() + ".xsd");
			String logMessage = "Wrote schema to: ";
			if (out.exists()) {
				if (!fileHasDifferentContent(out, schema)) {
					if (logger.isInfoEnabled()) {
						logger.info("Schema matches file, so not modifying: " + out.getAbsolutePath());
					}
					return;
				}
				out = new File(dir, code.getTitle() + "-" + code.getVersion() + "xsd.GENERATED");
				logMessage = "Schema does not match existing file, so writing to: ";
			}
			try {
				FileCopyUtils.copy(schema.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info(logMessage + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write schema to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	protected void generateExtractionTemplate(AppConfig appConfig, GeneratedCode code) {
		String template = code.getExtractionTemplate();
		if (template != null) {
			File dir = new File(appConfig.getSchemaPaths().get(0));
			dir.mkdirs();
			dir = new File(dir, "tde");
			dir.mkdirs();
			File out = new File(dir, code.getTitle() + "-" + code.getVersion() + ".tdex");
			String logMessage = "Wrote extraction template to: ";
			if (out.exists()) {
				if (!fileHasDifferentContent(out, template)) {
					if (logger.isInfoEnabled()) {
						logger.info("Extraction template matches file, so not modifying: " + out.getAbsolutePath());
					}
					return;
				}
				out = new File(dir, code.getTitle() + "-" + code.getVersion() + "tdex.GENERATED");
				logMessage = "Extraction template does not match existing file, so writing to: ";
			}
			try {
				FileCopyUtils.copy(template.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info(logMessage + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write extraction template to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	/**
	 * Determines if a file matches generated code, in which case we don't need to do anything further with the
	 * generated code.
	 *
	 * @param existingFile
	 * @param content
	 * @return
	 */
	protected boolean fileHasDifferentContent(File existingFile, String content) {
		try {
			String fileContent = copyFileToString(existingFile);
			fileContent = removeGeneratedAtTimestamp(fileContent);
			content = removeGeneratedAtTimestamp(content);
			return !fileContent.equals(content);
		} catch (RuntimeException e) {
			// Shouldn't occur, but if it does, treat it as the file having different content
			return true;
		}
	}

	/**
	 * The instance converter module has a timestamp in it that has to be removed in order to tell if its contents match
	 * that of an existing instance converter; the timestamp will of course nearly always be different.
	 *
	 * @param content
	 * @return
	 */
	protected String removeGeneratedAtTimestamp(String content) {
		int pos = content.indexOf("Generated at timestamp");
		if (pos > -1) {
			int end = content.indexOf(":)", pos);
			return content.substring(0, pos) + content.substring(end + 2);
		}
		return content;
	}

	public void setOptionsPath(String optionsPath) {
		this.optionsPath = optionsPath;
	}
}

