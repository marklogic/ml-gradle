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
package com.marklogic.mgmt.api.forest;

import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.database.ForestDatabaseReplication;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.forests.ForestManager;
import com.marklogic.mgmt.resource.hosts.HostManager;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "forest-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Forest extends Resource {

	@XmlElement(name = "forest-name")
	private String forestName;

    private Boolean enabled;
    private String host;
    private String database;

	@XmlElement(name = "data-directory")
	private String dataDirectory;

	@XmlElement(name = "large-data-directory")
	private String largeDataDirectory;

	@XmlElement(name = "fast-data-directory")
	private String fastDataDirectory;

	@XmlElement(name = "updates-allowed")
	private String updatesAllowed;

    private String availability;

	@XmlElement(name = "rebalancer-enable")
	private Boolean rebalancerEnable;

	@XmlElementWrapper(name = "ranges")
	private List<Range> range;

	@XmlElement(name = "failover-enable")
	private Boolean failoverEnable;

	@XmlElementWrapper(name = "failover-hosts")
	@XmlElement(name = "failover-host")
	private List<String> failoverHost;

	@XmlElementWrapper(name = "forest-backups")
	@XmlElement(name = "forest-backup")
	private List<ForestBackup> forestBackup;

	@XmlElement(name = "database-replication")
	private ForestDatabaseReplication databaseReplication;

	@XmlElementWrapper(name = "forest-replicas")
	@XmlElement(name = "forest-replica")
	private List<ForestReplica> forestReplica;

    public Forest() {
    }

    public Forest(API api, String forestName) {
        super(api);
        setForestName(forestName);
    }

    public Forest(String host, String forestName) {
    	setHost(host);
    	setForestName(forestName);
	}

    @Override
    protected String getResourceLabel() {
        return getForestName();
    }

    @Override
    protected ResourceManager getResourceManager() {
        return new ForestManager(getClient());
    }

    @Override
    protected String getResourceId() {
        return forestName;
    }

    /**
     * save is tricky for forests, because many of the properties are read-only, and thus ForestManager does not yet
     * support updates.
     *
     * Another tricky part is that "localhost" won't work as a hostname - it has to be the real hostname. So if it's not
     * set, we have to fetch it from the cluster.
     */
    @Override
    public String save() {
        if (host == null) {
            String host = new HostManager(getClient()).getHostNames().get(0);
            if (getLogger().isInfoEnabled()) {
                getLogger().info(format("Setting forest host to %s", host));
            }
            this.host = host;
        }
        return super.save();
    }

    public String getForestName() {
        return forestName;
    }

    public void setForestName(String forestName) {
        this.forestName = forestName;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getLargeDataDirectory() {
        return largeDataDirectory;
    }

    public void setLargeDataDirectory(String largeDataDirectory) {
        this.largeDataDirectory = largeDataDirectory;
    }

    public String getFastDataDirectory() {
        return fastDataDirectory;
    }

    public void setFastDataDirectory(String fastDataDirectory) {
        this.fastDataDirectory = fastDataDirectory;
    }

    public String getUpdatesAllowed() {
        return updatesAllowed;
    }

    public void setUpdatesAllowed(String updatesAllowed) {
        this.updatesAllowed = updatesAllowed;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public Boolean isRebalancerEnable() {
        return rebalancerEnable;
    }

    public void setRebalancerEnable(Boolean rebalancerEnable) {
        this.rebalancerEnable = rebalancerEnable;
    }

    public List<Range> getRange() {
        return range;
    }

    public void setRange(List<Range> range) {
        this.range = range;
    }

    public Boolean isFailoverEnable() {
        return failoverEnable;
    }

    public void setFailoverEnable(Boolean failoverEnable) {
        this.failoverEnable = failoverEnable;
    }

    public List<String> getFailoverHost() {
        return failoverHost;
    }

    public void setFailoverHost(List<String> failoverHost) {
        this.failoverHost = failoverHost;
    }

    public List<ForestBackup> getForestBackup() {
        return forestBackup;
    }

    public void setForestBackup(List<ForestBackup> forestBackup) {
        this.forestBackup = forestBackup;
    }

    public ForestDatabaseReplication getDatabaseReplication() {
        return databaseReplication;
    }

    public void setDatabaseReplication(ForestDatabaseReplication databaseReplication) {
        this.databaseReplication = databaseReplication;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Boolean getRebalancerEnable() {
        return rebalancerEnable;
    }

    public Boolean getFailoverEnable() {
        return failoverEnable;
    }

	public List<ForestReplica> getForestReplica() {
		return forestReplica;
	}

	public void setForestReplica(List<ForestReplica> forestReplica) {
		this.forestReplica = forestReplica;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
