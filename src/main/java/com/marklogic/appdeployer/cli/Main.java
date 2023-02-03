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
package com.marklogic.appdeployer.cli;

import ch.qos.logback.classic.Level;
import com.beust.jcommander.JCommander;
import com.marklogic.appdeployer.AppConfig;
import com.marklogic.appdeployer.DefaultAppConfigFactory;
import com.marklogic.appdeployer.command.Command;
import com.marklogic.appdeployer.command.CommandMapBuilder;
import com.marklogic.appdeployer.impl.SimpleAppDeployer;
import com.marklogic.mgmt.DefaultManageConfigFactory;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.mgmt.admin.DefaultAdminConfigFactory;
import com.marklogic.mgmt.util.PropertySource;
import com.marklogic.mgmt.util.SimplePropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Main {

	private final static Logger logger = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		Options options = new Options();
		JCommander.Builder builder = JCommander
			.newBuilder()
			.addObject(options);

		addCommandsToBuilder(builder);

		JCommander commander = builder.build();
		commander.setProgramName("java -jar <name of jar>");
		commander.parse(args);

		String parsedCommand = commander.getParsedCommand();
		if (parsedCommand == null) {
			if (options.isPrintProperties()) {
				printProperties();
			} else {
				commander.usage();
			}
		} else {
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
			root.setLevel(Level.toLevel(options.getLogLevel()));

			JCommander parsedCommander = commander.getCommands().get(parsedCommand);
			CommandArray commandArray = (CommandArray) parsedCommander.getObjects().get(0);
			PropertySource propertySource = buildPropertySource(options);
			runCommand(commandArray, propertySource, options);
		}
	}

	/**
	 * Use CommandMapBuilder to construct the list of all known commands. Each will then be added to JCommander.
	 *
	 * @param builder
	 */
	private static void addCommandsToBuilder(JCommander.Builder builder) {
		CommandMapBuilder commandMapBuilder = new CommandMapBuilder();
		Map<String, List<Command>> commandMap = commandMapBuilder.buildCommandMap();

		// Combine all commands into an ordered map so that usage() lists them in alphabetical order
		Map<String, CommandArray> orderedCommandMap = new TreeMap<>();
		orderedCommandMap.put("mlDeploy", new DeployCommand(commandMap));

		for (String commandGroup : commandMap.keySet()) {
			for (Command command : commandMap.get(commandGroup)) {
				String className = command.getClass().getSimpleName();
				if (className.endsWith("Command")) {
					className = className.substring(0, className.length() - "Command".length());
				}
				orderedCommandMap.put("ml" + className, new CommandWrapper(command));
			}
		}

		for (String commandName : orderedCommandMap.keySet()) {
			builder.addCommand(commandName, orderedCommandMap.get(commandName));
		}
	}

	/**
	 * Properties can be read from a properties file, and/or specified via the -P flag. If a file is specified, any
	 * properties specified via -P will override what's in the properties file.
	 *
	 * @param options
	 * @return
	 * @throws IOException
	 */
	private static PropertySource buildPropertySource(Options options) throws IOException {
		final String propertiesFilePath = options.getPropertiesFilePath();
		if (propertiesFilePath != null) {
			Properties props = new Properties();
			if (logger.isInfoEnabled()) {
				logger.info("Reading properties from file path: " + propertiesFilePath);
			}
			try (FileInputStream fis = new FileInputStream(propertiesFilePath)) {
				props.load(fis);
			}

			// Dynamic params override what's in the properties file
			Map<String, String> params = options.getParams();
			if (params != null) {
				for (String key : params.keySet()) {
					props.setProperty(key, params.get(key));
				}
			}
			return new SimplePropertySource(props);
		} else {
			return (name) -> options.getParams().get(name);
		}
	}

	private static void printProperties() {
		System.out.println("\nManage server connection properties");
		for (String name : new TreeSet<>(new DefaultManageConfigFactory().getPropertyConsumerMap().keySet())) {
			System.out.println(" - " + name);
		}

		System.out.println("\nAdmin server connection properties");
		for (String name : new TreeSet<>(new DefaultAdminConfigFactory().getPropertyConsumerMap().keySet())) {
			System.out.println(" - " + name);
		}

		System.out.println("\nApplication properties");
		for (String name : new TreeSet<>(new DefaultAppConfigFactory().getPropertyConsumerMap().keySet())) {
			System.out.println(" - " + name);
		}
	}

	/**
	 * @param commandArray
	 * @param propertySource
	 * @param options
	 */
	private static void runCommand(CommandArray commandArray, PropertySource propertySource, Options options) {
		AppConfig appConfig = new DefaultAppConfigFactory(propertySource).newAppConfig();
		ManageConfig manageConfig = new DefaultManageConfigFactory(propertySource).newManageConfig();
		ManageClient manageClient = new ManageClient(manageConfig);
		AdminConfig adminConfig = new DefaultAdminConfigFactory(propertySource).newAdminConfig();
		AdminManager adminManager = new AdminManager(adminConfig);

		SimpleAppDeployer deployer = new SimpleAppDeployer(manageClient, adminManager, commandArray.getCommands());
		if (options.isUndo()) {
			deployer.undeploy(appConfig);
		} else {
			deployer.deploy(appConfig);
		}
	}
}
