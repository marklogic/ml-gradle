/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.pdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.progress.pdc.client.PdcClient;
import com.progress.pdc.client.generated.ApiException;
import com.progress.pdc.client.generated.api.ServiceApi;
import com.progress.pdc.client.generated.model.MarkLogicApp;
import com.progress.pdc.client.generated.model.MarkLogicEndpointMappingData;
import com.progress.pdc.client.generated.model.MarkLogicHttpEndpoint;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeployMarkLogicEndpointsCommand extends AbstractCommand {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	public DeployMarkLogicEndpointsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PDC_MARKLOGIC_ENDPOINTS);
	}

	@Override
	public void execute(CommandContext context) {
		final List<String> pdcConfigPaths = context.getAppConfig().getPdcConfigPaths();
		if (pdcConfigPaths == null || pdcConfigPaths.isEmpty()) {
			return;
		}

		final List<MarkLogicHttpEndpoint> endpoints = readEndpointDefinitionsFromFiles(context, pdcConfigPaths);
		if (!endpoints.isEmpty()) {
			if (!StringUtils.hasText(context.getAppConfig().getCloudApiKey())) {
				logger.warn("Found configuration for {} MarkLogic endpoint(s), but not deploying them because no cloud API key has been specified.", endpoints.size());
			} else {
				deployEndpoints(context, endpoints);
			}
		}
	}

	private List<MarkLogicHttpEndpoint> readEndpointDefinitionsFromFiles(CommandContext context, List<String> pdcConfigPaths) {
		List<MarkLogicHttpEndpoint> endpoints = new ArrayList<>();

		for (String pdcConfigPath : pdcConfigPaths) {
			File serviceDir = new File(pdcConfigPath, "service");
			File endpointsDir = new File(serviceDir, "mlendpoints");
			if (!endpointsDir.exists()) {
				logger.info("MarkLogic endpoints directory does not exist: {}", endpointsDir.getAbsolutePath());
				continue;
			}

			logger.info("Reading MarkLogic endpoints from: {}", endpointsDir.getAbsolutePath());

			try (Stream<Path> paths = Files.walk(endpointsDir.toPath())) {
				paths.filter(Files::isRegularFile)
					.filter(path -> path.toString().endsWith(".json"))
					.forEach(path -> endpoints.add(buildEndpointFromFile(context, path.toFile())));
			} catch (IOException e) {
				throw new RuntimeException("Failed to read MarkLogic endpoint configuration files from: " +
					endpointsDir.getAbsolutePath(), e);
			}
		}

		return endpoints;
	}

	private MarkLogicHttpEndpoint buildEndpointFromFile(CommandContext context, File endpointFile) {
		try {
			String content = readResourceFromFile(context, endpointFile);
			MarkLogicHttpEndpoint endpoint = OBJECT_MAPPER.readValue(content, MarkLogicHttpEndpoint.class);
			if (logger.isDebugEnabled()) {
				logger.debug("Built MarkLogic endpoint: name={}, displayName={}, port={}, type={}, path={}",
					endpoint.getName(), endpoint.getDisplayName(), endpoint.getPort(),
					endpoint.getType(), endpoint.getPath());
			}
			return endpoint;
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse MarkLogic endpoint configuration file: " +
				endpointFile.getAbsolutePath(), e);
		}
	}

	private void deployEndpoints(CommandContext context, List<MarkLogicHttpEndpoint> endpoints) {
		final String host = context.getManageClient().getManageConfig().getHost();
		try (PdcClient pdcClient = new PdcClient(host, context.getAppConfig().getCloudApiKey())) {
			final UUID markLogicServiceId = getFirstMarkLogicServiceId(pdcClient);
			final ServiceApi serviceApi = new ServiceApi(pdcClient.getApiClient());
			try {
				MarkLogicEndpointMappingData existingEndpoints = serviceApi.apiServiceMlendpointsIdGet(markLogicServiceId);
				List<MarkLogicHttpEndpoint> endpointsToDeploy = filterOutExistingEndpoints(endpoints, existingEndpoints);
				if (endpointsToDeploy.isEmpty()) {
					logger.info("All {} endpoint(s) are up to date; nothing to deploy.", endpoints.size());
				} else {
					logger.info("Deploying {} new or updated endpoint(s) out of {} total.", endpointsToDeploy.size(), endpoints.size());
					serviceApi.apiServiceMlendpointsIdHttpPut(markLogicServiceId, endpointsToDeploy);
					logger.info("Successfully deployed {} endpoint(s).", endpointsToDeploy.size());
				}
			} catch (ApiException e) {
				throw new RuntimeException("Unable to create MarkLogic endpoints in PDC; cause: %s".formatted(e.getMessage()), e);
			}
		}
	}

	/**
	 * Filters out endpoints that don't need to be deployed. An endpoint needs to be deployed if:
	 * 1. It doesn't exist in PDC (based on name, which is unique), OR
	 * 2. It exists but has different properties (needs to be updated)
	 * <p>
	 * An endpoint can take a surprisingly long time to create, so we only want to deploy ones that
	 * are new or have changed.
	 *
	 * @param endpoints            the list of endpoints to potentially deploy
	 * @param existingEndpointData the existing endpoint data from PDC
	 * @return a list of endpoints that need to be deployed (new or updated)
	 */
	private List<MarkLogicHttpEndpoint> filterOutExistingEndpoints(
		List<MarkLogicHttpEndpoint> endpoints,
		MarkLogicEndpointMappingData existingEndpointData
	) {
		if (existingEndpointData == null || existingEndpointData.getEndpoints() == null
			|| existingEndpointData.getEndpoints().getHttpEndpoints() == null) {
			return endpoints;
		}

		Map<String, MarkLogicHttpEndpoint> existingEndpointsByName = existingEndpointData.getEndpoints()
			.getHttpEndpoints().stream()
			.collect(Collectors.toMap(MarkLogicHttpEndpoint::getName, Function.identity()));

		return endpoints.stream()
			.filter(endpoint -> needsDeployment(endpoint, existingEndpointsByName.get(endpoint.getName())))
			.collect(Collectors.toList());
	}

	/**
	 * Determines if an endpoint needs to be deployed by comparing it to an existing endpoint.
	 *
	 * @param endpoint         the endpoint to potentially deploy
	 * @param existingEndpoint the existing endpoint with the same name, or null if none exists
	 * @return true if the endpoint needs to be deployed (new or has changes)
	 */
	private boolean needsDeployment(MarkLogicHttpEndpoint endpoint, MarkLogicHttpEndpoint existingEndpoint) {
		if (existingEndpoint == null) {
			return true;
		}

		// We don't check "type", as this command so far only supports http endpoints.
		return !Objects.equals(endpoint.getDisplayName(), existingEndpoint.getDisplayName())
			|| !Objects.equals(endpoint.getIcon(), existingEndpoint.getIcon())
			|| !Objects.equals(endpoint.getPath(), existingEndpoint.getPath())
			|| !Objects.equals(endpoint.getPort(), existingEndpoint.getPort())
			|| !Objects.equals(endpoint.getSupportedByCloud(), existingEndpoint.getSupportedByCloud());
	}

	private UUID getFirstMarkLogicServiceId(PdcClient pdcClient) {
		try {
			final UUID environmentId = pdcClient.getEnvironmentId();
			List<MarkLogicApp> apps = new ServiceApi(pdcClient.getApiClient()).apiServiceAppsGet(environmentId).getMarkLogic();
			if (apps == null || apps.isEmpty()) {
				throw new RuntimeException("No instances of MarkLogic found in PDC tenancy; host: %s".formatted(pdcClient.getHost()));
			}
			return apps.get(0).getId();
		} catch (ApiException e) {
			throw new RuntimeException("Unable to lookup instances of MarkLogic in PDC; cause: %s".formatted(e), e);
		}
	}
}
