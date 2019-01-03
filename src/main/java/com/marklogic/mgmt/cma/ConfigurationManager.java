package com.marklogic.mgmt.cma;

import com.marklogic.mgmt.AbstractManager;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.rest.util.MgmtResponseErrorHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

/**
 * This doesn't extend AbstractResourceManager because a configuration isn't really a resource, it's a collection of
 * resources.
 *
 * Currently only supports JSON and XML configuration payloads. Not clear yet from the docs on what the format of a
 * zip should be. The docs also mention a bunch of request parameters, but the examples don't show what the purpose
 * of those are, so those aren't supported yet either.
 */
public class ConfigurationManager extends AbstractManager {

	public final static String PATH = "/manage/v3";

	private ManageClient manageClient;

	public ConfigurationManager(ManageClient manageClient) {
		this.manageClient = manageClient;
	}

	@Override
	protected boolean useSecurityUser() {
		return true;
	}

	/**
	 * Returns true if the CMA endpoint exists. This temporarily disables logging in MgmtResponseErrorHandler so that
	 * a client doesn't see the 404 error being logged, which could be mistakenly perceived as a real error.
	 * @return
	 */
	public boolean endpointExists() {
		try {
			MgmtResponseErrorHandler.errorLoggingEnabled = false;
			if (logger.isInfoEnabled()) {
				logger.info("Checking to see if Configuration Management API is available at: " + PATH);
			}
			final String emptyPayload = "{}";
			submit(emptyPayload);
			return true;
		} catch (HttpClientErrorException ex) {
			return false;
		} finally {
			MgmtResponseErrorHandler.errorLoggingEnabled = true;
		}
	}

	/**
	 * Submits the configuration, with some logging before and after.
	 *
	 * @param payload
	 * @return
	 */
	public SaveReceipt save(String payload) {
		String configurationName = payloadParser.getPayloadFieldValue(payload, "name", false);
		if (configurationName == null) {
			configurationName = "with unknown name";
		}

		if (logger.isInfoEnabled()) {
			logger.info("Applying configuration " + configurationName);
		}

		SaveReceipt receipt = submit(payload);

		if (logger.isInfoEnabled()) {
			logger.info("Applied configuration " + configurationName);
		}

		return receipt;
	}

	public SaveReceipt submit(String payload) {
		ResponseEntity<String> response = postPayload(manageClient, PATH, payload);
		return new SaveReceipt(null, payload, PATH, response);
	}

}
