package com.marklogic.client.es;

public class CodeGenerationRequest {

	private boolean generateDatabaseProperties = true;
	private boolean generateExtractionTemplate = true;
	private boolean generateInstanceConverter = true;
	private boolean generateSchema = true;
	private boolean generateSearchOptions = true;
	private boolean generateVersionTranslator = true;

	public boolean isGenerateDatabaseProperties() {
		return generateDatabaseProperties;
	}

	public void setGenerateDatabaseProperties(boolean generateDatabaseProperties) {
		this.generateDatabaseProperties = generateDatabaseProperties;
	}

	public boolean isGenerateExtractionTemplate() {
		return generateExtractionTemplate;
	}

	public void setGenerateExtractionTemplate(boolean generateExtractionTemplate) {
		this.generateExtractionTemplate = generateExtractionTemplate;
	}

	public boolean isGenerateInstanceConverter() {
		return generateInstanceConverter;
	}

	public void setGenerateInstanceConverter(boolean generateInstanceConverter) {
		this.generateInstanceConverter = generateInstanceConverter;
	}

	public boolean isGenerateSchema() {
		return generateSchema;
	}

	public void setGenerateSchema(boolean generateSchema) {
		this.generateSchema = generateSchema;
	}

	public boolean isGenerateSearchOptions() {
		return generateSearchOptions;
	}

	public void setGenerateSearchOptions(boolean generateSearchOptions) {
		this.generateSearchOptions = generateSearchOptions;
	}

	public boolean isGenerateVersionTranslator() {
		return generateVersionTranslator;
	}

	public void setGenerateVersionTranslator(boolean generateVersionTranslator) {
		this.generateVersionTranslator = generateVersionTranslator;
	}
}
