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
package com.marklogic.appdeployer.export.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.export.ResourceExporter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Base class that provides some convenience methods for implementing ResourceExporter.
 *
 * TODO Should be able to add TaskExecutor support to allow for parallelizing requests to the Management API to
 * export resources.
 */
public abstract class AbstractResourceExporter extends LoggingObject implements ResourceExporter {

	private ManageClient manageClient;
	private String format = FORMAT_JSON;
	protected PayloadParser payloadParser = new PayloadParser();
	protected ObjectMapper objectMapper;

	protected AbstractResourceExporter(ManageClient manageClient) {
		this.manageClient = manageClient;
		this.objectMapper = ObjectMapperFactory.getObjectMapper();
	}

	protected boolean isFormatXml() {
		return FORMAT_XML.equalsIgnoreCase(getFormat());
	}

	protected String removeJsonKeyFromPayload(String payload, String key) {
		if (payloadParser.isJsonPayload(payload)) {
			ObjectNode node = (ObjectNode)payloadParser.parseJson(payload);
			if (node.has(key)) {
				node.remove(key);
				try {
					return ObjectMapperFactory.getObjectMapper().writeValueAsString(node);
				} catch (JsonProcessingException e) {
					throw new RuntimeException("Unable to write forest JSON out as string: " + e.getMessage(), e);
				}
			}
		}
		return payload;
	}

	/**
	 * Uses SimpleExportInputs with the given resourceName to export the resource identified by resourceName. This works
	 * well for resources that are uniquely identified solely by their resource name, such as a database.
	 *
	 * @param mgr
	 * @param resourceName
	 * @param resourceDir
	 * @return
	 */
	protected File exportToFile(ResourceManager mgr, String resourceName, File resourceDir) {
		return exportToFile(mgr, new SimpleExportInputs(resourceName), resourceDir);
	}

	/**
	 * Use this for exporting resources that can't be identified solely by their resource name. You can provide your own
	 * implementation of ExportInputs to define the inputs for your resource that are needed by this method in order to
	 * export a resource.
	 *
	 * @param mgr
	 * @param exportInputs
	 * @param resourceDir
	 * @return
	 */
	protected File exportToFile(ResourceManager mgr, ExportInputs exportInputs, File resourceDir) {
		File f = null;
		try {
			if (isFormatXml()) {
				f = exportToXml(mgr, exportInputs, resourceDir);
			} else {
				f = exportToJson(mgr, exportInputs, resourceDir);
			}
		} catch (IOException ex) {
			logger.warn(format("Unable to export resource with name %s to resource directory %s, cause: %s",
				exportInputs.getResourceName(), resourceDir.getAbsolutePath(), ex.getMessage()), ex);
		}
		return f;
	}

	protected File exportToXml(ResourceManager mgr, ExportInputs exportInputs, File resourceDir) throws IOException {
		String xml = mgr.getPropertiesAsXmlString(exportInputs.getResourceName(), exportInputs.getResourceUrlParams());
		xml = beforeResourceWrittenToFile(exportInputs, xml);
		File f = new File(resourceDir, exportInputs.buildFilename("xml"));
		logWritingFile(exportInputs, f);
		FileCopyUtils.copy(xml.getBytes(), f);
		return f;
	}

	protected File exportToJson(ResourceManager mgr, ExportInputs exportInputs, File resourceDir) throws IOException {
		String json = mgr.getPropertiesAsJson(exportInputs.getResourceName(), exportInputs.getResourceUrlParams());
		json = beforeResourceWrittenToFile(exportInputs, json);
		json = prettyPrintJson(json);

		File f = new File(resourceDir, exportInputs.buildFilename("json"));
		logWritingFile(exportInputs, f);
		FileCopyUtils.copy(json.getBytes(), f);
		return f;
	}

	protected String prettyPrintJson(String json) throws IOException {
		JsonNode node = objectMapper.readTree(json);
		return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
	}

	protected String beforeResourceWrittenToFile(ExportInputs exportInputs, String payload) {
		return payload;
	}

	protected void logWritingFile(ExportInputs exportInputs, File file) {
		if (logger.isInfoEnabled()) {
			logger.info(format("Exporting resource %s to file %s", exportInputs.getResourceName(), file.getAbsolutePath()));
		}
	}

	public ManageClient getManageClient() {
		return manageClient;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}

