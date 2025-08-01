/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.command.forests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.resource.forests.ForestManager;

import java.io.File;
import java.util.Iterator;

/**
 * Use this command when you want precise control over the forests that are created for a database. It processes
 * each directory under ml-config/forests (the name of the directory does not matter, but it makes sense to name
 * it after the database that the forests belong to), and each file in a directory can have a single forest object
 * or an array of forest objects.
 * <p>
 * You can also set customForestsPath to specify a directory other than "forest" as the path that contains
 * directories of custom forests. This allows you to easily support different custom forests in different
 * environments.
 */
public class DeployCustomForestsCommand extends AbstractCommand {

	private String customForestsPath = "forests";
	private PayloadParser payloadParser;

	public DeployCustomForestsCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_FORESTS);
	}

	@Override
	public void execute(CommandContext context) {
		for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
			File dir = new File(configDir.getBaseDir(), customForestsPath);
			if (dir != null && dir.exists()) {
				payloadParser = new PayloadParser();
				File[] dirs = dir.listFiles();
				if (dirs != null) {
					for (File f : dirs) {
						if (f.isDirectory()) {
							processDirectory(f, context);
						}
					}
				}
			} else {
				logResourceDirectoryNotFound(dir);
			}
		}
	}

	/**
	 * Supports JSON files with a single payload or an array of payloads, or an XML file with a single payload.
	 *
	 * @param dir
	 * @param context
	 */
	protected void processDirectory(File dir, CommandContext context) {
		if (logger.isInfoEnabled()) {
			logger.info("Processing custom forest files in directory: " + dir.getAbsolutePath());
		}
		ForestManager mgr = new ForestManager(context.getManageClient());

		for (File f : listFilesInDirectory(dir)) {
			if (logger.isInfoEnabled()) {
				logger.info("Processing forests in file: " + f.getAbsolutePath());
			}
			String payload = readResourceFromFile(context, f);

			// As of 4.6.1, create a CMA request per file so that the user has control over how many forests are
			// submitted in a single request, thus avoiding potential timeouts.
			Configuration configuration = context.getAppConfig().getCmaConfig().isDeployForests() ?
				new Configuration() : null;

			if (payloadParser.isJsonPayload(payload)) {
				if (configuration != null) {
					addForestsToCmaConfiguration(context, payload, configuration);
				} else {
					mgr.saveJsonForests(payload);
				}
			} else {
				if (configuration != null) {
					configuration.addForest(convertPayloadToObjectNode(context, payload));
				} else {
					mgr.save(payload);
				}
			}

			if (configuration != null) {
				deployConfiguration(context, configuration);
			}
		}
	}

	protected void addForestsToCmaConfiguration(CommandContext context, String payload, Configuration configuration) {
		JsonNode node = payloadParser.parseJson(payload);
		if (node.isArray()) {
			Iterator<JsonNode> iter = node.iterator();
			while (iter.hasNext()) {
				configuration.addForest((ObjectNode) iter.next());
			}
		} else {
			configuration.addForest((ObjectNode) node);
		}
	}

	public void setCustomForestsPath(String customForestsPath) {
		this.customForestsPath = customForestsPath;
	}
}
