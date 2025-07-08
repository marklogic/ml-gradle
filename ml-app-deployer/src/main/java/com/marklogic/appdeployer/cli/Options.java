/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.appdeployer.cli;

import ch.qos.logback.classic.Level;
import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

import java.util.HashMap;
import java.util.Map;

public class Options {

	@Parameter(names = {"-f"}, description = "Path to properties file")
	private String propertiesFilePath;

	@Parameter(names = {"-l"}, description = "Log level, as defined by Logback")
	private String logLevel = Level.INFO.levelStr;

	@Parameter(names = {"-p"}, description = "Print a list of all supported properties")
	private boolean printProperties;

	@Parameter(names = {"-u"}, description = "Undo the given command (i.e. undeploy instead of deploy)")
	private boolean undo;

	@DynamicParameter(names = "-P", description = "Use this argument to include any property defined by the ml-gradle Property Reference; e.g. -PmlAppName=example")
	private Map<String, String> params = new HashMap<>();

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getPropertiesFilePath() {
		return propertiesFilePath;
	}

	public void setPropertiesFilePath(String propertiesFilePath) {
		this.propertiesFilePath = propertiesFilePath;
	}

	public boolean isUndo() {
		return undo;
	}

	public void setUndo(boolean undo) {
		this.undo = undo;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public boolean isPrintProperties() {
		return printProperties;
	}

	public void setPrintProperties(boolean printProperties) {
		this.printProperties = printProperties;
	}
}
