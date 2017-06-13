package com.marklogic.appdeployer.export;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.PayloadParser;

/**
 * Base class that provides a few conveniences for implementing ResourceExporter.
 *
 * TODO Should be able to add TaskExecutor support to allow for parallelizing requests to the Management API to
 * export resources.
 */
public abstract class AbstractResourceExporter extends LoggingObject implements ResourceExporter {

	private ManageClient manageClient;
	private String format = FORMAT_JSON;
	protected PayloadParser payloadParser = new PayloadParser();

	protected AbstractResourceExporter(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	protected boolean isFormatXml() {
		return FORMAT_XML.equalsIgnoreCase(format);
	}

	protected String removeJsonKeyFromPayload(String payload, String key) {
		if (payloadParser.isJsonPayload(payload)) {
			ObjectNode node = (ObjectNode)payloadParser.parseJson(payload);
			if (node.has(key)) {
				node.remove(key);
				try {
					return payloadParser.getObjectMapper().writeValueAsString(node);
				} catch (JsonProcessingException e) {
					throw new RuntimeException("Unable to write forest JSON out as string: " + e.getMessage(), e);
				}
			}
		}
		return payload;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public ManageClient getManageClient() {
		return manageClient;
	}
}
