package com.marklogic.appdeployer.command.es;

import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.es.CodeGenerationRequest;
import com.marklogic.client.es.EntityServicesManager;
import com.marklogic.client.es.GeneratedCode;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.springframework.util.FileCopyUtils;

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
			DatabaseClient client = appConfig.newDatabaseClient();
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
		}
	}

	protected GeneratedCode loadModelDefinition(AppConfig appConfig, File f, EntityServicesManager mgr) {
		String name = f.getName();
		String modelDefinition = null;
		try {
			modelDefinition = new String(FileCopyUtils.copyToByteArray(f));
		} catch (IOException e) {
			throw new RuntimeException("Unable to read model definition from file: " + f.getAbsolutePath(), e);
		}
		String modelUri = mgr.loadModel(name, modelDefinition);
		return mgr.generateCode(modelUri, buildCodeGenerationRequest(appConfig));
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
			if (out.exists()) {
				out = new File(esDir, code.getTitle() + "-" + code.getVersion() + "-GENERATED.xqy");
			}
			try {
				FileCopyUtils.copy(instanceConverter.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info("Wrote instance converter to: " + out.getAbsolutePath());
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
			if (out.exists()) {
				out = new File(optionsDir, code.getTitle() + "-GENERATED.xml");
			}
			try {
				FileCopyUtils.copy(searchOptions.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info("Wrote search options to: " + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write search options to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	protected void generateDatabaseProperties(AppConfig appConfig, GeneratedCode code) {
		String props = code.getDatabaseProperties();
		if (props != null) {
			File dir = appConfig.getConfigDir().getDatabasesDir();
			dir.mkdirs();
			File out = new File(dir, "content-database.json");
			if (out.exists()) {
				out = new File(dir, "content-database-GENERATED.json");
			}
			try {
				FileCopyUtils.copy(props.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info("Wrote database properties to: " + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write database properties to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	protected void generateSchema(AppConfig appConfig, GeneratedCode code) {
		String schema = code.getSchema();
		if (schema != null) {
			File dir = new File(appConfig.getSchemasPath());
			dir.mkdirs();
			File out = new File(dir, code.getTitle() + "-" + code.getVersion() + ".xsd");
			if (out.exists()) {
				out = new File(dir, code.getTitle() + "-" + code.getVersion() + "-GENERATED.xsd");
			}
			try {
				FileCopyUtils.copy(schema.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info("Wrote schema to: " + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write schema to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	protected void generateExtractionTemplate(AppConfig appConfig, GeneratedCode code) {
		String template = code.getExtractionTemplate();
		if (template != null) {
			File dir = new File(appConfig.getSchemasPath());
			dir.mkdirs();
			File out = new File(dir, code.getTitle() + "-" + code.getVersion() + ".tdex");
			if (out.exists()) {
				out = new File(dir, code.getTitle() + "-" + code.getVersion() + "-GENERATED.tdex");
			}
			try {
				FileCopyUtils.copy(template.getBytes(), out);
				if (logger.isInfoEnabled()) {
					logger.info("Wrote extraction template to: " + out.getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to write extraction template to file: " + out.getAbsolutePath(), e);
			}
		}
	}

	public void setOptionsPath(String optionsPath) {
		this.optionsPath = optionsPath;
	}
}

