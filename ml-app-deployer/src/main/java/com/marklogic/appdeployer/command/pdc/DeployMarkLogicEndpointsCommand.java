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

	// OOTB endpoints that PDC GET returns but PUT doesn't manage. These are filtered out when comparing
	// to avoid unnecessary deployments. These names are stable, but if they change, worst case is an
	// unnecessary deployment, not a failure.
	private static final Set<String> OOTB_PDC_ENDPOINT_NAMES = Set.of("Admin", "App-services", "Manage");

	public DeployMarkLogicEndpointsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_PDC_MARKLOGIC_ENDPOINTS);
	}

	@Override
	public void execute(CommandContext context) {
		final List<String> pdcConfigPaths = context.getAppConfig().getPdcConfigPaths();
		if (pdcConfigPaths == null || pdcConfigPaths.isEmpty()) {
			return;
		}

		final Map<String, List<MarkLogicHttpEndpoint>> endpointsByDnsName = readEndpointDefinitionsFromFiles(context, pdcConfigPaths);
		if (!endpointsByDnsName.isEmpty()) {
			if (!StringUtils.hasText(context.getAppConfig().getCloudApiKey())) {
				int totalEndpoints = endpointsByDnsName.values().stream().mapToInt(List::size).sum();
				logger.warn("Found configuration for {} MarkLogic endpoint(s), but not deploying them because no cloud API key has been specified.", totalEndpoints);
			} else {
				deployEndpointsByService(context, endpointsByDnsName);
			}
		}
	}

	private Map<String, List<MarkLogicHttpEndpoint>> readEndpointDefinitionsFromFiles(CommandContext context, List<String> pdcConfigPaths) {
		Map<String, List<MarkLogicHttpEndpoint>> endpointsByDnsName = new HashMap<>();

		for (String pdcConfigPath : pdcConfigPaths) {
			File serviceDir = new File(pdcConfigPath, "service");
			File endpointsDir = new File(serviceDir, "mlendpoints");
			if (!endpointsDir.exists()) {
				logger.info("MarkLogic endpoints directory does not exist: {}", endpointsDir.getAbsolutePath());
				continue;
			}

			logger.info("Reading MarkLogic endpoints from: {}", endpointsDir.getAbsolutePath());

			File[] dnsNameDirs = endpointsDir.listFiles(File::isDirectory);
			if (dnsNameDirs == null || dnsNameDirs.length == 0) {
				logger.warn("No dnsName directories found under: {}. Endpoints should be organized under mlendpoints/<dnsName>/*.json", endpointsDir.getAbsolutePath());
				continue;
			}

			for (File dnsNameDir : dnsNameDirs) {
				String dnsName = dnsNameDir.getName();
				List<MarkLogicHttpEndpoint> endpoints = new ArrayList<>();

				try (Stream<Path> paths = Files.walk(dnsNameDir.toPath())) {
					paths.filter(Files::isRegularFile)
						.filter(path -> path.toString().endsWith(".json"))
						.forEach(path -> endpoints.add(buildEndpointFromFile(context, path.toFile())));
				} catch (IOException e) {
					throw new RuntimeException("Failed to read MarkLogic endpoint configuration files from: " +
						dnsNameDir.getAbsolutePath(), e);
				}

				if (!endpoints.isEmpty()) {
					endpointsByDnsName.put(dnsName, endpoints);
					logger.info("Found {} endpoint(s) for MarkLogic service: {}", endpoints.size(), dnsName);
				}
			}
		}

		return endpointsByDnsName;
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

	private void deployEndpointsByService(CommandContext context, Map<String, List<MarkLogicHttpEndpoint>> endpointsByDnsName) {
		final String host = context.getManageClient().getManageConfig().getHost();
		try (PdcClient pdcClient = new PdcClient(host, context.getAppConfig().getCloudApiKey())) {
			for (Map.Entry<String, List<MarkLogicHttpEndpoint>> entry : endpointsByDnsName.entrySet()) {
				String dnsName = entry.getKey();
				List<MarkLogicHttpEndpoint> endpoints = entry.getValue();
				logger.info("Processing {} endpoint(s) for MarkLogic service: {}", endpoints.size(), dnsName);
				deployEndpoints(pdcClient, dnsName, endpoints);
			}
		}
	}

	private void deployEndpoints(PdcClient pdcClient, String dnsName, List<MarkLogicHttpEndpoint> endpoints) {
		final UUID markLogicServiceId = getMarkLogicServiceIdByDnsName(pdcClient, dnsName);
		final ServiceApi serviceApi = new ServiceApi(pdcClient.getApiClient());
		try {
			MarkLogicEndpointMappingData existingEndpointData = serviceApi.apiServiceMlendpointsIdGet(markLogicServiceId);
			if (allEndpointsMatch(endpoints, existingEndpointData)) {
				logger.info("All {} endpoint(s) for '{}' are up to date; nothing to deploy.", endpoints.size(), dnsName);
			} else {
				logger.info("Deploying all {} endpoint(s) for '{}' due to changes or count mismatch.", endpoints.size(), dnsName);
				serviceApi.apiServiceMlendpointsIdHttpPut(markLogicServiceId, endpoints);
				logger.info("Successfully deployed {} endpoint(s) for '{}'.", endpoints.size(), dnsName);
			}
		} catch (ApiException e) {
			throw new RuntimeException("Unable to create MarkLogic endpoints for '%s' in PDC; cause: %s".formatted(dnsName, e.getMessage()), e);
		}
	}

	/**
	 * Checks if all endpoints match the existing endpoints in PDC. Returns true only if:
	 * 1. The count of endpoints matches, AND
	 * 2. Every endpoint exists with identical properties
	 * <p>
	 * If any endpoint is new, modified, or removed, this returns false and all endpoints
	 * will be deployed. The PDC API requires sending the complete list of endpoints.
	 *
	 * @param endpoints            the list of endpoints to potentially deploy
	 * @param existingEndpointData the existing endpoint data from PDC
	 * @return true if all endpoints are up to date, false if deployment is needed
	 */
	private boolean allEndpointsMatch(
		List<MarkLogicHttpEndpoint> endpoints,
		MarkLogicEndpointMappingData existingEndpointData
	) {
		if (existingEndpointData == null || existingEndpointData.getEndpoints() == null
			|| existingEndpointData.getEndpoints().getHttpEndpoints() == null) {
			return false;
		}

		List<MarkLogicHttpEndpoint> existingEndpoints = existingEndpointData.getEndpoints().getHttpEndpoints();

		// Filter out OOTB endpoints that PDC GET returns but PUT doesn't manage
		List<MarkLogicHttpEndpoint> customExistingEndpoints = existingEndpoints.stream()
			.filter(e -> !OOTB_PDC_ENDPOINT_NAMES.contains(e.getName()))
			.collect(Collectors.toList());

		// If counts don't match, something changed
		if (endpoints.size() != customExistingEndpoints.size()) {
			return false;
		}

		Map<String, MarkLogicHttpEndpoint> existingEndpointsByName = customExistingEndpoints.stream()
			.collect(Collectors.toMap(MarkLogicHttpEndpoint::getName, Function.identity()));

		// Check if all endpoints exist and match - returns true only if none need deployment
		return endpoints.stream()
			.noneMatch(endpoint -> needsDeployment(endpoint, existingEndpointsByName.get(endpoint.getName())));
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

	private UUID getMarkLogicServiceIdByDnsName(PdcClient pdcClient, String dnsName) {
		try {
			final UUID environmentId = pdcClient.getEnvironmentId();
			List<MarkLogicApp> apps = new ServiceApi(pdcClient.getApiClient()).apiServiceAppsGet(environmentId).getMarkLogic();
			if (apps == null || apps.isEmpty()) {
				throw new RuntimeException("No instances of MarkLogic found in PDC tenancy; host: %s".formatted(pdcClient.getHost()));
			}

			return apps.stream()
				.filter(app -> dnsName.equals(app.getDnsName()))
				.findFirst()
				.map(MarkLogicApp::getId)
				.orElseThrow(() -> new RuntimeException(
					"No MarkLogic service found with dnsName '%s'. Available services: %s".formatted(
						dnsName,
						apps.stream().map(MarkLogicApp::getDnsName).collect(Collectors.joining(", "))
					)
				));
		} catch (ApiException e) {
			throw new RuntimeException("Unable to lookup instances of MarkLogic in PDC; cause: %s".formatted(e), e);
		}
	}
}
