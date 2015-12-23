package com.marklogic.mgmt.api.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.groups.GroupManager;
import com.marklogic.mgmt.tasks.TaskManager;

public class Group extends Resource {

    public final static String DEFAULT_GROUP_NAME = "Default";

    private String groupName;
    private Integer listCacheSize;
    private Integer listCachePartitions;
    private Integer compressedTreeCacheSize;
    private Integer compressedTreeCachePartitions;
    private Integer compressedTreeReadSize;
    private Integer expandedTreeCacheSize;
    private Integer expandedTreeCachePartitions;
    private Integer tripleCacheSize;
    private Integer tripleCachePartitions;
    private Integer tripleCacheTimeout;
    private Integer tripleValueCacheSize;
    private Integer tripleValueCachePartitions;
    private Integer tripleValueCacheTimeout;
    private String smtpRelay;
    private Integer smtpTimeout;
    private String httpUserAgent;
    private Integer httpTimeout;
    private Integer xdqpTimeout;
    private Integer hostTimeout;
    private Integer hostInitialTimeout;
    private Integer retryTimeout;
    private Integer moduleCacheTimeout;
    private String systemLogLevel;
    private String fileLogLevel;
    private String rotateLogFiles;
    private String keepLogFiles;
    private Boolean failoverEnable;
    private Boolean xdqpSslEnabled;
    private Boolean xdqpSslAllowSslv3;
    private Boolean xdqpSslAllowTls;
    private String xdqpSslCiphers;
    private List<Schema> schema;
    private List<Namespace> namespace;
    private List<UsingNamespace> usingNamespace;
    private List<ModuleLocation> moduleLocation;
    private Boolean eventsActivated;
    private List<String> event;
    private Audit audit;
    private Long backgroundIoLimit;
    private Boolean meteringEnabled;
    private Boolean performanceMeteringEnabled;
    private String metersDatabase;
    private Integer performanceMeteringPeriod;
    private Integer performanceMeteringRetainRaw;
    private Integer performanceMeteringRetainHourly;
    private Integer performanceMeteringRetainDaily;
    private String s3Domain;
    private String s3Protocol;
    private String s3ServerSideEncyrption;
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
        for (String e : events) {
            event.add(e);
        }
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

    public String getS3ServerSideEncyrption() {
        return s3ServerSideEncyrption;
    }

    public void setS3ServerSideEncyrption(String s3ServerSideEncyrption) {
        this.s3ServerSideEncyrption = s3ServerSideEncyrption;
    }

    public String getSecurityDatabase() {
        return securityDatabase;
    }

    public void setSecurityDatabase(String securityDatabase) {
        this.securityDatabase = securityDatabase;
    }
}
