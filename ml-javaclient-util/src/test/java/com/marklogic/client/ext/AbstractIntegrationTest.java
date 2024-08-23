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
package com.marklogic.client.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DocumentMetadataHandle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class AbstractIntegrationTest {

	protected final static String CONTENT_DATABASE = "ml-javaclient-util-test-content";
	protected final static String MODULES_DATABASE = "ml-javaclient-util-test-modules";

	@Autowired
	protected DatabaseClientConfig clientConfig;
	protected DatabaseClient client;

	protected ConfiguredDatabaseClientFactory configuredDatabaseClientFactory = new DefaultConfiguredDatabaseClientFactory();

	@AfterEach
	public void releaseClientOnTearDown() {
		if (client != null) {
			try {
				client.release();
			} catch (Exception ex) {
				// That's fine, the test probably released it already
			}
		}
	}

	protected final DatabaseClient newContentClient() {
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(CONTENT_DATABASE);
		DatabaseClient client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);
		return client;
	}

	protected DatabaseClient newClient(String database) {
		String currentDatabase = clientConfig.getDatabase();
		clientConfig.setDatabase(database);
		client = configuredDatabaseClientFactory.newDatabaseClient(clientConfig);
		clientConfig.setDatabase(currentDatabase);
		return client;
	}

	protected final void verifyMetadata(String uri, Consumer<DocumentMetadataHandle> verifier) {
		verifier.accept(client.newJSONDocumentManager().readMetadata(uri, new DocumentMetadataHandle()));
	}

	protected final void verifyCollections(String uri, String... collections) {
		verifyMetadata(uri, metadata -> {
			assertEquals(collections.length, metadata.getCollections().size());
			for (String collection : collections) {
				assertTrue(metadata.getCollections().contains(collection), "Did not find expected collection: " +
					collection + "; actual collections: " + metadata.getCollections());
			}
		});
	}

	protected final void verifyPermissions(String uri, String... permissionsRolesAndCapabilities) {
		verifyMetadata(uri, metadata -> {
			// TODO This will likely need to be modified once we shift the tests to not use an admin user, and thus
			// the user will have to specify at least one update permission.
			if (permissionsRolesAndCapabilities.length == 0) {
				assertEquals(0, metadata.getPermissions().size());
			}
			for (int i = 0; i < permissionsRolesAndCapabilities.length; i += 2) {
				String role = permissionsRolesAndCapabilities[i];
				assertTrue(metadata.getPermissions().containsKey(role), "Did not find permissions with role: " +
					role + "; actual permissions: " + metadata.getPermissions());

				DocumentMetadataHandle.Capability capability =
					DocumentMetadataHandle.Capability.valueOf(permissionsRolesAndCapabilities[i + 1].toUpperCase());
				Set<DocumentMetadataHandle.Capability> capabilities = metadata.getPermissions().get(role);
				assertTrue(capabilities.contains(capability), "Did not find permission for role: " + role +
					" with capability: " + capability + "; actual capabilities: " + capabilities);
			}
		});
	}
}

@Configuration
@PropertySource(value = {"file:gradle.properties", "file:gradle-local.properties"}, ignoreResourceNotFound = true)
class TestConfig extends DatabaseClientConfig {

	@Autowired
	public TestConfig(
		@Value("${mlHost}") String host,
		@Value("${mlRestPort}") int port,
		@Value("${mlUsername}") String username,
		@Value("${mlPassword}") String password) {
		super(host, port, username, password);
	}

	/**
	 * Ensures that placeholders are replaced with property values
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
