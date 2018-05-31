package com.marklogic.mgmt.cma;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;
import org.springframework.http.ResponseEntity;

/**
 * This doesn't extend AbstractResourceManager because a configuration isn't really a resource, it's a collection of
 * resources.
 *
 * Currently only supports JSON and XML configuration payloads. Not clear yet from the docs on what the format of a
 * zip should be. The docs also mention a bunch of request parameters, but the examples don't show what the purpose
 * of those are, so those aren't supported yet either.
 */
public class ConfigurationManager extends AbstractManager {

	private ManageClient manageClient;

	public ConfigurationManager(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	public SaveReceipt save(String payload) {
		String configurationName = payloadParser.getPayloadFieldValue(payload, "name", false);
		if (configurationName == null) {
			configurationName = "with unknown name";
		}

		if (logger.isInfoEnabled()) {
			logger.info("Applying configuration " + configurationName);
		}

		final String path = "/manage/v3";
		ResponseEntity<String> response = postPayload(manageClient, path, payload);

		if (logger.isInfoEnabled()) {
			logger.info("Applied configuration " + configurationName);
		}

		return new SaveReceipt(null, payload, path, response);
	}
}
