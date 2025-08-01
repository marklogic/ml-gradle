/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.client.ext.es;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.GenericDocumentManager;
import com.marklogic.client.ext.util.XmlUtil;
import com.marklogic.client.io.BytesHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.jdom2.Element;
import org.jdom2.Namespace;

import java.io.StringReader;
import java.util.Objects;

/**
 * Handles loading a model and then making calls to generate code for it.
 */
public class EntityServicesManager {

	private String modelCollection = "http://marklogic.com/entity-services/models";
	private String baseUri = "/marklogic.com/entity-services/models/";
	private DatabaseClient client;

	public EntityServicesManager(DatabaseClient client) {
		this.client = client;
	}

	/**
	 * @param moduleName      appended to the baseUri to determine the model definition URI
	 * @param modelDefinition JSON or XML
	 * @return the URI of the loaded model
	 */
	public String loadModel(String moduleName, String modelDefinition) {
		GenericDocumentManager mgr = client.newDocumentManager();
		DocumentMetadataHandle dmh = new DocumentMetadataHandle();
		dmh.getCollections().add(modelCollection);
		String modelUri = baseUri + moduleName;
		mgr.write(modelUri, dmh, new BytesHandle(modelDefinition.getBytes()));
		return modelUri;
	}

	public GeneratedCode generateCode(String modelUri, CodeGenerationRequest request) {
		GeneratedCode code = initializeGeneratedCode(modelUri);
		if (request.isGenerateDatabaseProperties()) {
			code.setDatabaseProperties(generateCode(modelUri, "database-properties-generate"));
		}
		if (request.isGenerateExtractionTemplate()) {
			code.setExtractionTemplate(generateCode(modelUri, "extraction-template-generate"));
		}
		if (request.isGenerateInstanceConverter()) {
			code.setInstanceConverter(generateCode(modelUri, "instance-converter-generate"));
		}
		if (request.isGenerateSchema()) {
			code.setSchema(generateCode(modelUri, "schema-generate"));
		}
		if (request.isGenerateSearchOptions()) {
			code.setSearchOptions(generateCode(modelUri, "search-options-generate"));
		}
		return code;
	}

	public String generateVersionTranslator(String oldModelUri, String newModelUri) {
		String xquery = "import module namespace es = \"http://marklogic.com/entity-services\" at \"/MarkLogic/entity-services/entity-services.xqy\"; " +
			"declare variable $oldModelUri external; " +
			"declare variable $newModelUri external; " +
			"es:version-translator-generate(fn:doc($oldModelUri), fn:doc($newModelUri))";
		return client.newServerEval()
			.xquery(xquery)
			.addVariable("oldModelUri", oldModelUri)
			.addVariable("newModelUri", newModelUri)
			.evalAs(String.class);
	}

	protected GeneratedCode initializeGeneratedCode(String modelUri) {
		String xquery = "import module namespace es = \"http://marklogic.com/entity-services\" at \"/MarkLogic/entity-services/entity-services.xqy\"; " +
			"declare variable $URI external; " +
			"es:model-to-xml(es:model-validate(fn:doc($URI)))";
		String output = client.newServerEval().xquery(xquery).addVariable("URI", modelUri).evalAs(String.class);
		Objects.requireNonNull(output);
		Element root;
		try {
			root = XmlUtil.newSAXBuilder().build(new StringReader(output)).getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("Unable to parse model XML: " + e.getMessage(), e);
		}
		Namespace ns = Namespace.getNamespace("es", "http://marklogic.com/entity-services");
		Element info = root.getChild("info", ns);
		Objects.requireNonNull(info);
		String title = info.getChildText("title", ns);
		String version = info.getChildText("version", ns);
		GeneratedCode code = new GeneratedCode();
		code.setTitle(title);
		code.setVersion(version);
		return code;
	}

	protected String generateCode(String modelUri, String functionName) {
		String xquery = "import module namespace es = \"http://marklogic.com/entity-services\" at \"/MarkLogic/entity-services/entity-services.xqy\"; " +
			"declare variable $URI external; " +
			String.format("es:%s(es:model-validate(fn:doc($URI)))", functionName);
		return client.newServerEval().xquery(xquery).addVariable("URI", modelUri).evalAs(String.class);
	}

	public void setModelCollection(String modelCollection) {
		this.modelCollection = modelCollection;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}
}
