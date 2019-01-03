package com.marklogic.mgmt.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.api.ApiObject;
import com.marklogic.mgmt.cma.ConfigurationManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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
	}

	public Configurations(Configuration... configs) {
		this(Arrays.asList(configs));
	}

	public Configurations(List<Configuration> configs) {
		super();
		this.configs = configs;
		setObjectMapper(ObjectMapperFactory.getObjectMapper());
	}

	public void submit(ManageClient manageClient) {
		final String json = getJson();
		final Logger logger = LoggerFactory.getLogger(getClass());

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
