package com.marklogic.mgmt.resource.security;

import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import org.springframework.http.ResponseEntity;

public class SecurityManager extends LoggingObject {

	private ManageClient manageClient;

	public SecurityManager(ManageClient client) {
		this.manageClient = client;
	}

	public ResponseEntity<String> rotateConfigEncryptionKey() {
		return postJson("{\"operation\":\"rotate-config-encryption-key\"}");
	}

	public ResponseEntity<String> rotateDateEncryptionKey() {
		return postJson("{\"operation\":\"rotate-data-encryption-key\"}");
	}

	public ResponseEntity<String> rotateLogsEncryptionKey() {
		return postJson("{\"operation\":\"rotate-logs-encryption-key\"}");
	}

	public ResponseEntity<String> importWallet(String filename, String password) {
		String json = format(
			"{\"operation\":\"import-wallet\", \"filename\":\"%s\", \"password\":\"%s\"}",
			filename, password);
		return postJson(json);
	}

	public ResponseEntity<String> exportWallet(String filename, String password) {
		String json = format(
			"{\"operation\":\"export-wallet\", \"filename\":\"%s\", \"password\":\"%s\"}",
			filename, password);
		return postJson(json);
	}

	private ResponseEntity<String> postJson(String json) {
		return manageClient.postJson("/manage/v2/security", json);
	}
}
