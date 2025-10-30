/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.pdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.progress.pdc.client.PdcClient;
import com.progress.pdc.client.generated.ApiClient;
import com.progress.pdc.client.generated.ApiException;
import com.progress.pdc.client.generated.api.ServiceApi;
import com.progress.pdc.client.generated.api.ServiceGroupApi;
import com.progress.pdc.client.generated.model.MarkLogicApp;
import com.progress.pdc.client.generated.model.MarkLogicHttpEndpoint;
import com.progress.pdc.client.generated.model.ServiceGroupViewModel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Deploys Progress Data Cloud (PDC) integration endpoints by reading JSON configuration files
 * from the src/main/pdc-config/service/endpoints directory.
 */
public class DeployPdcEndpointsCommand extends AbstractCommand {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public DeployPdcEndpointsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PDC_MARKLOGIC_ENDPOINTS);
	}

	@Override
	public void execute(CommandContext context) {
		final List<String> pdcConfigPaths = context.getAppConfig().getPdcConfigPaths();
		if (pdcConfigPaths == null || pdcConfigPaths.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("No PDC config paths configured");
			}
			return;
		}

		final List<MarkLogicHttpEndpoint> endpoints = readEndpointDefinitionsFromFiles(pdcConfigPaths);
		if (!endpoints.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("Deploying {} PDC endpoint(s)", endpoints.size());
			}
			deployEndpoints(context, endpoints);
		}
	}

	private List<MarkLogicHttpEndpoint> readEndpointDefinitionsFromFiles(List<String> pdcConfigPaths) {
		List<MarkLogicHttpEndpoint> endpoints = new ArrayList<>();

		for (String pdcConfigPath : pdcConfigPaths) {
			File serviceDir = new File(pdcConfigPath, "service");
			File endpointsDir = new File(serviceDir, "endpoints");
			if (!endpointsDir.exists()) {
				if (logger.isDebugEnabled()) {
					logger.debug("PDC endpoints directory does not exist: {}", endpointsDir.getAbsolutePath());
				}
				continue;
			}

			if (logger.isInfoEnabled()) {
				logger.info("Reading PDC integration endpoints from: {}", endpointsDir.getAbsolutePath());
			}

			try (Stream<Path> paths = Files.walk(endpointsDir.toPath())) {
				paths.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".json"))
					.forEach(path -> endpoints.add(buildEndpointFromFile(path.toFile())));
			} catch (IOException e) {
				throw new RuntimeException("Failed to read PDC endpoint configuration files from: " +
					endpointsDir.getAbsolutePath(), e);
			}
		}

		return endpoints;
	}

	private MarkLogicHttpEndpoint buildEndpointFromFile(File endpointFile) {
		try {
			MarkLogicHttpEndpoint endpoint = OBJECT_MAPPER.readValue(endpointFile, MarkLogicHttpEndpoint.class);
			if (logger.isInfoEnabled()) {
				logger.info("Built PDC endpoint: name={}, displayName={}, port={}, type={}, path={}",
					endpoint.getName(), endpoint.getDisplayName(), endpoint.getPort(),
					endpoint.getType(), endpoint.getPath());
			}
			return endpoint;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse PDC endpoint configuration file: " +
				endpointFile.getAbsolutePath(), e);
		}
	}

	private void deployEndpoints(CommandContext context, List<MarkLogicHttpEndpoint> endpoints) {
		final String host = context.getManageClient().getManageConfig().getHost();
		try (PdcClient pdcClient = new PdcClient(host, context.getAppConfig().getCloudApiKey())) {
			final UUID environmentId = getEnvironmentId(pdcClient.getApiClient());
			final UUID markLogicServiceId = getFirstMarkLogicServiceId(pdcClient.getApiClient(), environmentId, host);
			try {
				new ServiceApi(pdcClient.getApiClient()).apiServiceMlendpointsIdHttpPut(markLogicServiceId, endpoints);
			} catch (ApiException e) {
				throw new RuntimeException("Unable to create MarkLogic HTTP endpoints in PDC; cause: %s".formatted(e.getMessage()), e);
			}
		}
	}

	private UUID getEnvironmentId(ApiClient apiClient) {
		try {
			ServiceGroupViewModel viewModel = new ServiceGroupApi(apiClient).apiServicegroupGet(null).get(0);
			return viewModel.getId();
		} catch (ApiException e) {
			throw new RuntimeException("Unable to get service groups from PDC; cause: %s".formatted(e.getMessage()), e);
		}
	}

	private UUID getFirstMarkLogicServiceId(ApiClient apiClient, UUID environmentId, String host) {
		try {
			List<MarkLogicApp> apps = new ServiceApi(apiClient).apiServiceAppsGet(environmentId).getMarkLogic();
			if (apps == null || apps.isEmpty()) {
				throw new RuntimeException("No instances of MarkLogic found in PDC tenancy; host: %s".formatted(host));
			}
			return apps.get(0).getId();
		} catch (ApiException e) {
			throw new RuntimeException("Unable to lookup instances of MarkLogic in PDC; cause: %s".formatted(e), e);
		}
	}
}
