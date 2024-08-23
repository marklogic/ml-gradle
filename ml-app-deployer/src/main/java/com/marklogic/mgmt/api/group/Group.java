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
package com.marklogic.mgmt.api.group;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.server.UsingNamespace;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.groups.GroupManager;
import com.marklogic.mgmt.resource.tasks.TaskManager;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Group extends Resource {

	public final static String DEFAULT_GROUP_NAME = "Default";

	@XmlElement(name = "group-name")
	private String groupName;

	@XmlElement(name = "list-cache-size")
	private Integer listCacheSize;

	@XmlElement(name = "list-cache-partitions")
	private Integer listCachePartitions;

	@XmlElement(name = "compressed-tree-cache-size")
	private Integer compressedTreeCacheSize;

	@XmlElement(name = "compressed-tree-cache-partitions")
	private Integer compressedTreeCachePartitions;

	@XmlElement(name = "compressed-tree-read-size")
	private Integer compressedTreeReadSize;

	@XmlElement(name = "expanded-tree-cache-size")
	private Integer expandedTreeCacheSize;

	@XmlElement(name = "expanded-tree-cache-partitions")
	private Integer expandedTreeCachePartitions;

	@XmlElement(name = "triple-cache-size")
	private Integer tripleCacheSize;

	@XmlElement(name = "triple-cache-partitions")
	private Integer tripleCachePartitions;

	@XmlElement(name = "triple-cache-timeout")
	private Integer tripleCacheTimeout;

	@XmlElement(name = "triple-value-cache-size")
	private Integer tripleValueCacheSize;

	@XmlElement(name = "triple-value-cache-partitions")
	private Integer tripleValueCachePartitions;

	@XmlElement(name = "triple-value-cache-timeout")
	private Integer tripleValueCacheTimeout;

	@XmlElement(name = "smtp-relay")
	private String smtpRelay;

	@XmlElement(name = "smtp-timeout")
	private Integer smtpTimeout;

	@XmlElement(name = "http-user-agent")
	private String httpUserAgent;

	@XmlElement(name = "http-timeout")
	private Integer httpTimeout;

	@XmlElement(name = "xdqp-timeout")
	private Integer xdqpTimeout;

	@XmlElement(name = "host-timeout")
	private Integer hostTimeout;

	@XmlElement(name = "host-initial-timeout")
	private Integer hostInitialTimeout;

	@XmlElement(name = "retry-timeout")
	private Integer retryTimeout;

	@XmlElement(name = "module-cache-timeout")
	private Integer moduleCacheTimeout;

	@XmlElement(name = "system-log-level")
	private String systemLogLevel;

	@XmlElement(name = "file-log-level")
	private String fileLogLevel;

	@XmlElement(name = "rotate-log-files")
	private String rotateLogFiles;

	@XmlElement(name = "keep-log-files")
	private String keepLogFiles;

	@XmlElement(name = "failover-enable")
	private Boolean failoverEnable;

	@XmlElement(name = "xdqp-ssl-enabled")
	private Boolean xdqpSslEnabled;

	@XmlElement(name = "xdqp-ssl-allow-ssl-v3")
	private Boolean xdqpSslAllowSslv3;

	@XmlElement(name = "xdqp-ssl-allow-tls")
	private Boolean xdqpSslAllowTls;

	@XmlElement(name = "xdqp-ssl-ciphers")
	private String xdqpSslCiphers;

	@XmlElementWrapper(name = "schemas")
	@XmlElement(name = "schema")
	private List<Schema> schema;

	@XmlElementWrapper(name = "namespaces")
	@XmlElement(name = "namespace")
	private List<Namespace> namespace;

	@XmlElementWrapper(name = "using-namespaces")
	@XmlElement(name = "using-namespace")
	private List<UsingNamespace> usingNamespace;

	@XmlElementWrapper(name = "module-locations")
	@XmlElement(name = "module-location")
	private List<ModuleLocation> moduleLocation;

	@XmlElement(name = "events-activated")
	private Boolean eventsActivated;

	@XmlElementWrapper(name = "events")
	@XmlElement(name = "event")
	private List<String> event;

	private Audit audit;

	@XmlElement(name = "background-io-limit")
	private Long backgroundIoLimit;

	@XmlElement(name = "metering-enabled")
	private Boolean meteringEnabled;

	@XmlElement(name = "performance-metering-enabled")
	private Boolean performanceMeteringEnabled;

	@XmlElement(name = "meters-database")
	private String metersDatabase;

	@XmlElement(name = "performance-metering-period")
	private Integer performanceMeteringPeriod;

	@XmlElement(name = "performance-metering-raw")
	private Integer performanceMeteringRetainRaw;

	@XmlElement(name = "performance-metering-hourly")
	private Integer performanceMeteringRetainHourly;

	@XmlElement(name = "performance-metering-daily")
	private Integer performanceMeteringRetainDaily;

	@XmlElement(name = "s3-domain")
	private String s3Domain;

	@XmlElement(name = "s3-protocol")
	private String s3Protocol;

	@XmlElement(name = "s3-encryption")
	private String s3ServerSideEncryption;

	@XmlElement(name = "security-database")
	private String securityDatabase;

	public Group() {
		super();
	}

	public Group(API api) {
		this(api, DEFAULT_GROUP_NAME);
	}

	public Group(API api, String groupName) {
		super(api);
		this.groupName = groupName;
	}

	@Override
	protected ResourceManager getResourceManager() {
		return new GroupManager(getClient());
	}

	/**
	 * Convenience method that adds the given events and then updates the group, ensuring that trace events are
	 * activated.
	 *
	 * @param events
	 */
	public void trace(String... events) {
		addEvents(events);
		eventsActivated = true;
		saveEvents();
	}

	/**
	 * Convenience method for removing some trace events.
	 *
	 * @param events
	 */
	public void untrace(String... events) {
		removeEvents(events);
		saveEvents();
	}

	/**
	 * Convenience method for sending a partial JSON message with just the event list and eventsActivated.
	 */
	public void saveEvents() {
		// Send a partial JSON message
		Group temp = new Group(getApi(), groupName);
		temp.setEvent(getEvent());
		temp.setEventsActivated(true);
		temp.save();
	}

	public void addEvents(String... events) {
		if (event == null) {
			event = new ArrayList<>();
		}
		event.addAll(Arrays.asList(events));
	}

	public void removeEvents(String... events) {
		if (event == null) {
			event = new ArrayList<>();
		} else {
			event.removeAll(Arrays.asList(events));
		}
	}

	public void disableTasks() {
		newTaskManager().disableAllTasks();
	}

	public void enableTasks() {
		newTaskManager().enableAllTasks();
	}

	public void deleteTasks() {
		newTaskManager().deleteAllScheduledTasks();
	}

	public TaskManager newTaskManager() {
		return new TaskManager(getClient(), getGroupName());
	}

	@Override
	protected String getResourceId() {
		return groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getListCacheSize() {
		return listCacheSize;
	}

	public void setListCacheSize(Integer listCacheSize) {
		this.listCacheSize = listCacheSize;
	}

	public Integer getListCachePartitions() {
		return listCachePartitions;
	}

	public void setListCachePartitions(Integer listCachePartitions) {
		this.listCachePartitions = listCachePartitions;
	}

	public Integer getCompressedTreeCacheSize() {
		return compressedTreeCacheSize;
	}

	public void setCompressedTreeCacheSize(Integer compressedTreeCacheSize) {
		this.compressedTreeCacheSize = compressedTreeCacheSize;
	}

	public Integer getCompressedTreeCachePartitions() {
		return compressedTreeCachePartitions;
	}

	public void setCompressedTreeCachePartitions(Integer compressedTreeCachePartitions) {
		this.compressedTreeCachePartitions = compressedTreeCachePartitions;
	}

	public Integer getCompressedTreeReadSize() {
		return compressedTreeReadSize;
	}

	public void setCompressedTreeReadSize(Integer compressedTreeReadSize) {
		this.compressedTreeReadSize = compressedTreeReadSize;
	}

	public Integer getExpandedTreeCacheSize() {
		return expandedTreeCacheSize;
	}

	public void setExpandedTreeCacheSize(Integer expandedTreeCacheSize) {
		this.expandedTreeCacheSize = expandedTreeCacheSize;
	}

	public Integer getExpandedTreeCachePartitions() {
		return expandedTreeCachePartitions;
	}

	public void setExpandedTreeCachePartitions(Integer expandedTreeCachePartitions) {
		this.expandedTreeCachePartitions = expandedTreeCachePartitions;
	}

	public Integer getTripleCacheSize() {
		return tripleCacheSize;
	}

	public void setTripleCacheSize(Integer tripleCacheSize) {
		this.tripleCacheSize = tripleCacheSize;
	}

	public Integer getTripleCachePartitions() {
		return tripleCachePartitions;
	}

	public void setTripleCachePartitions(Integer tripleCachePartitions) {
		this.tripleCachePartitions = tripleCachePartitions;
	}

	public Integer getTripleCacheTimeout() {
		return tripleCacheTimeout;
	}

	public void setTripleCacheTimeout(Integer tripleCacheTimeout) {
		this.tripleCacheTimeout = tripleCacheTimeout;
	}

	public Integer getTripleValueCacheSize() {
		return tripleValueCacheSize;
	}

	public void setTripleValueCacheSize(Integer tripleValueCacheSize) {
		this.tripleValueCacheSize = tripleValueCacheSize;
	}

	public Integer getTripleValueCachePartitions() {
		return tripleValueCachePartitions;
	}

	public void setTripleValueCachePartitions(Integer tripleValueCachePartitions) {
		this.tripleValueCachePartitions = tripleValueCachePartitions;
	}

	public Integer getTripleValueCacheTimeout() {
		return tripleValueCacheTimeout;
	}

	public void setTripleValueCacheTimeout(Integer tripleValueCacheTimeout) {
		this.tripleValueCacheTimeout = tripleValueCacheTimeout;
	}

	public String getSmtpRelay() {
		return smtpRelay;
	}

	public void setSmtpRelay(String smtpRelay) {
		this.smtpRelay = smtpRelay;
	}

	public Integer getSmtpTimeout() {
		return smtpTimeout;
	}

	public void setSmtpTimeout(Integer smtpTimeout) {
		this.smtpTimeout = smtpTimeout;
	}

	public String getHttpUserAgent() {
		return httpUserAgent;
	}

	public void setHttpUserAgent(String httpUserAgent) {
		this.httpUserAgent = httpUserAgent;
	}

	public Integer getHttpTimeout() {
		return httpTimeout;
	}

	public void setHttpTimeout(Integer httpTimeout) {
		this.httpTimeout = httpTimeout;
	}

	public Integer getXdqpTimeout() {
		return xdqpTimeout;
	}

	public void setXdqpTimeout(Integer xdqpTimeout) {
		this.xdqpTimeout = xdqpTimeout;
	}

	public Integer getHostTimeout() {
		return hostTimeout;
	}

	public void setHostTimeout(Integer hostTimeout) {
		this.hostTimeout = hostTimeout;
	}

	public Integer getHostInitialTimeout() {
		return hostInitialTimeout;
	}

	public void setHostInitialTimeout(Integer hostInitialTimeout) {
		this.hostInitialTimeout = hostInitialTimeout;
	}

	public Integer getRetryTimeout() {
		return retryTimeout;
	}

	public void setRetryTimeout(Integer retryTimeout) {
		this.retryTimeout = retryTimeout;
	}

	public Integer getModuleCacheTimeout() {
		return moduleCacheTimeout;
	}

	public void setModuleCacheTimeout(Integer moduleCacheTimeout) {
		this.moduleCacheTimeout = moduleCacheTimeout;
	}

	public String getSystemLogLevel() {
		return systemLogLevel;
	}

	public void setSystemLogLevel(String systemLogLevel) {
		this.systemLogLevel = systemLogLevel;
	}

	public String getFileLogLevel() {
		return fileLogLevel;
	}

	public void setFileLogLevel(String fileLogLevel) {
		this.fileLogLevel = fileLogLevel;
	}

	public String getRotateLogFiles() {
		return rotateLogFiles;
	}

	public void setRotateLogFiles(String rotateLogFiles) {
		this.rotateLogFiles = rotateLogFiles;
	}

	public String getKeepLogFiles() {
		return keepLogFiles;
	}

	public void setKeepLogFiles(String keepLogFiles) {
		this.keepLogFiles = keepLogFiles;
	}

	public Boolean getFailoverEnable() {
		return failoverEnable;
	}

	public void setFailoverEnable(Boolean failoverEnable) {
		this.failoverEnable = failoverEnable;
	}

	public Boolean getXdqpSslEnabled() {
		return xdqpSslEnabled;
	}

	public void setXdqpSslEnabled(Boolean xdqpSslEnabled) {
		this.xdqpSslEnabled = xdqpSslEnabled;
	}

	public Boolean getXdqpSslAllowSslv3() {
		return xdqpSslAllowSslv3;
	}

	public void setXdqpSslAllowSslv3(Boolean xdqpSslAllowSslv3) {
		this.xdqpSslAllowSslv3 = xdqpSslAllowSslv3;
	}

	public Boolean getXdqpSslAllowTls() {
		return xdqpSslAllowTls;
	}

	public void setXdqpSslAllowTls(Boolean xdqpSslAllowTls) {
		this.xdqpSslAllowTls = xdqpSslAllowTls;
	}

	public String getXdqpSslCiphers() {
		return xdqpSslCiphers;
	}

	public void setXdqpSslCiphers(String xdqpSslCiphers) {
		this.xdqpSslCiphers = xdqpSslCiphers;
	}

	public List<Schema> getSchema() {
		return schema;
	}

	public void setSchema(List<Schema> schema) {
		this.schema = schema;
	}

	public List<Namespace> getNamespace() {
		return namespace;
	}

	public void setNamespace(List<Namespace> namespace) {
		this.namespace = namespace;
	}

	public List<UsingNamespace> getUsingNamespace() {
		return usingNamespace;
	}

	public void setUsingNamespace(List<UsingNamespace> usingNamespace) {
		this.usingNamespace = usingNamespace;
	}

	public List<ModuleLocation> getModuleLocation() {
		return moduleLocation;
	}

	public void setModuleLocation(List<ModuleLocation> moduleLocation) {
		this.moduleLocation = moduleLocation;
	}

	public Boolean getEventsActivated() {
		return eventsActivated;
	}

	public void setEventsActivated(Boolean eventsActivated) {
		this.eventsActivated = eventsActivated;
	}

	public List<String> getEvent() {
		return event;
	}

	public void setEvent(List<String> event) {
		this.event = event;
	}

	public Audit getAudit() {
		return audit;
	}

	public void setAudit(Audit audit) {
		this.audit = audit;
	}

	public Long getBackgroundIoLimit() {
		return backgroundIoLimit;
	}

	public void setBackgroundIoLimit(Long backgroundIoLimit) {
		this.backgroundIoLimit = backgroundIoLimit;
	}

	public Boolean getMeteringEnabled() {
		return meteringEnabled;
	}

	public void setMeteringEnabled(Boolean meteringEnabled) {
		this.meteringEnabled = meteringEnabled;
	}

	public Boolean getPerformanceMeteringEnabled() {
		return performanceMeteringEnabled;
	}

	public void setPerformanceMeteringEnabled(Boolean performanceMeteringEnabled) {
		this.performanceMeteringEnabled = performanceMeteringEnabled;
	}

	public String getMetersDatabase() {
		return metersDatabase;
	}

	public void setMetersDatabase(String metersDatabase) {
		this.metersDatabase = metersDatabase;
	}

	public Integer getPerformanceMeteringPeriod() {
		return performanceMeteringPeriod;
	}

	public void setPerformanceMeteringPeriod(Integer performanceMeteringPeriod) {
		this.performanceMeteringPeriod = performanceMeteringPeriod;
	}

	public Integer getPerformanceMeteringRetainRaw() {
		return performanceMeteringRetainRaw;
	}

	public void setPerformanceMeteringRetainRaw(Integer performanceMeteringRetainRaw) {
		this.performanceMeteringRetainRaw = performanceMeteringRetainRaw;
	}

	public Integer getPerformanceMeteringRetainHourly() {
		return performanceMeteringRetainHourly;
	}

	public void setPerformanceMeteringRetainHourly(Integer performanceMeteringRetainHourly) {
		this.performanceMeteringRetainHourly = performanceMeteringRetainHourly;
	}

	public Integer getPerformanceMeteringRetainDaily() {
		return performanceMeteringRetainDaily;
	}

	public void setPerformanceMeteringRetainDaily(Integer performanceMeteringRetainDaily) {
		this.performanceMeteringRetainDaily = performanceMeteringRetainDaily;
	}

	public String getS3Domain() {
		return s3Domain;
	}

	public void setS3Domain(String s3Domain) {
		this.s3Domain = s3Domain;
	}

	public String getS3Protocol() {
		return s3Protocol;
	}

	public void setS3Protocol(String s3Protocol) {
		this.s3Protocol = s3Protocol;
	}

	public String getS3ServerSideEncryption() {
		return s3ServerSideEncryption;
	}

	public void setS3ServerSideEncryption(String s3ServerSideEncryption) {
		this.s3ServerSideEncryption = s3ServerSideEncryption;
	}

	public String getSecurityDatabase() {
		return securityDatabase;
	}

	public void setSecurityDatabase(String securityDatabase) {
		this.securityDatabase = securityDatabase;
	}
}
