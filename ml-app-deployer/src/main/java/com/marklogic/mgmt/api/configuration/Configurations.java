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
package com.marklogic.mgmt.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.ApiObject;
import com.marklogic.mgmt.cma.ConfigurationManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Does not yet support XML marshalling via JAXB.
 */
public class Configurations extends ApiObject {

	private String name;
	private String desc;
	private String user;
	private String group;
	private String host;
	private String platform;
	private String ts;
	private String version;

	@JsonProperty("config")
	private List<Configuration> configs;

	public Configurations() {
		super();
		this.configs = new ArrayList<>();
		setObjectMapper(ObjectMapperFactory.getObjectMapper());
	}

	public Configurations(Configuration... configs) {
		this();
		for (Configuration c : configs) {
			addConfig(c);
		}
	}

	public Configurations(List<Configuration> configs) {
		this();
		this.configs = configs;
	}

	public void addConfig(Configuration config) {
		if (this.configs == null) {
			this.configs = new ArrayList<>();
		}
		this.configs.add(config);
	}

	public boolean hasResources() {
		if (configs == null || configs.isEmpty()) {
			return false;
		}
		for (Configuration c : configs) {
			if (c.hasResources()) {
				return true;
			}
		}
		return false;
	}

	public void submit(ManageClient manageClient) {
		final Logger logger = LoggerFactory.getLogger(getClass());

		if (!hasResources()) {
			logger.info("No resources are present in this set of CMA configurations, so nothing will be submitted");
			return;
		}

		final String json = getJson();

		if (logger.isInfoEnabled()) {
			if (json.contains("password")) {
				logger.info("Submitting configuration (not logging because it contains the word 'password')");
			} else {
				logger.info("Submitting configuration: " + json);
			}
		}

		new ConfigurationManager(manageClient).submit(json);

		if (logger.isInfoEnabled()) {
			logger.info("Successfully submitted configuration");
		}
	}

	public List<Configuration> getConfigs() {
		return configs;
	}

	public void setConfigs(List<Configuration> configs) {
		this.configs = configs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
