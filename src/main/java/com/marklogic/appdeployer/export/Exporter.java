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
package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.export.appservers.ServerExporter;
import com.marklogic.appdeployer.export.cpf.CpfConfigExporter;
import com.marklogic.appdeployer.export.cpf.DomainExporter;
import com.marklogic.appdeployer.export.cpf.PipelineExporter;
import com.marklogic.appdeployer.export.databases.DatabaseExporter;
import com.marklogic.appdeployer.export.groups.GroupExporter;
import com.marklogic.appdeployer.export.impl.CompositeResourceExporter;
import com.marklogic.appdeployer.export.security.AmpExporter;
import com.marklogic.appdeployer.export.security.PrivilegeExporter;
import com.marklogic.appdeployer.export.security.RoleExporter;
import com.marklogic.appdeployer.export.security.UserExporter;
import com.marklogic.appdeployer.export.tasks.TaskExporter;
import com.marklogic.appdeployer.export.triggers.TriggerExporter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.selector.AbstractNameMatchingResourceSelector;
import com.marklogic.mgmt.selector.ResourceSelection;
import com.marklogic.mgmt.selector.ResourceSelector;

import java.io.File;

/**
 * Intent is to provide a fluent-ish way of composing comands for exporting resources.
 */
public class Exporter extends LoggingObject {

	private CompositeResourceExporter compositeExporter;
	private ManageClient manageClient;
	private String groupName;
	private String triggersDatabase;

	public static Exporter client(ManageClient manageClient) {
		return new Exporter(manageClient);
	}

	public Exporter(ManageClient manageClient) {
		this(manageClient, null);
	}

	/**
	 * If set, the groupName will be used when exporting servers and tasks.
	 *
	 * @param manageClient
	 * @param groupName
	 */
	public Exporter(ManageClient manageClient, String groupName) {
		this.manageClient = manageClient;
		compositeExporter = new CompositeResourceExporter();
		this.groupName = groupName;
	}

	public Exporter select(ResourceSelector selector) {
		// TODO A bit hacky here... may want an interface of e.g. TriggersDatabaseAware
		if (selector instanceof AbstractNameMatchingResourceSelector) {
			((AbstractNameMatchingResourceSelector)selector).setTriggersDatabase(triggersDatabase);
		}

		ResourceSelection selection = selector.selectResources(manageClient);
		amps(selection.getAmpUriRefs());
		cpfConfigs(triggersDatabase, selection.getCpfConfigNames());
		databases(selection.getDatabaseNames());
		domains(triggersDatabase, selection.getDomainNames());
		groups(selection.getGroupNames());
		pipelines(triggersDatabase, selection.getPipelineNames());
		privilegesExecute(selection.getPrivilegeExecuteNames());
		privilegesUri(selection.getPrivilegeUriNames());
		roles(selection.getRoleNames());
		servers(selection.getServerNames());
		tasks(selection.getTaskNames());
		triggers(triggersDatabase, selection.getTriggerNames());
		users(selection.getUserNames());
		return this;
	}

	public Exporter format(String xmlOrJson) {
		compositeExporter.setFormat(xmlOrJson);
		return this;
	}

	public ExportedResources export(File baseDir) {
		return compositeExporter.exportResources(baseDir);
	}

	public Exporter add(ResourceExporter resourceExporter) {
		compositeExporter.add(resourceExporter);
		return this;
	}

	public Exporter amps(String... ampUriRefs) {
		return (ampUriRefs != null && ampUriRefs.length > 0) ? add(new AmpExporter(manageClient, ampUriRefs)) : this;
	}

	/**
	 * A CPF config is identified by its domain name.
	 *
	 * @param databaseIdOrName
	 * @param domainNames
	 * @return
	 */
	public Exporter cpfConfigs(String databaseIdOrName, String... domainNames) {
		return (domainNames != null && domainNames.length > 0) ? add(new CpfConfigExporter(manageClient, databaseIdOrName, domainNames)) : null;
	}

	public Exporter databases(String... databaseNames) {
		return (databaseNames != null && databaseNames.length > 0) ? add(new DatabaseExporter(manageClient, databaseNames)) : null;
	}

	public Exporter domains(String databaseIdOrName, String... domainNames) {
		return (domainNames != null && domainNames.length > 0) ? add(new DomainExporter(manageClient, databaseIdOrName, domainNames)) : null;
	}

	public Exporter groups(String... groupNames) {
		return (groupNames != null && groupNames.length > 0) ? add(new GroupExporter(manageClient, groupNames)) : null;
	}

	public Exporter pipelines(String databaseIdOrName, String... pipelineNames) {
		return (pipelineNames != null && pipelineNames.length > 0) ? add(new PipelineExporter(manageClient, databaseIdOrName, pipelineNames)) : null;
	}

	public Exporter privilegesExecute(String... privilegeNames) {
		return (privilegeNames != null && privilegeNames.length > 0) ? add(new PrivilegeExporter(manageClient, privilegeNames)) : null;
	}

	public Exporter privilegesUri(String... privilegeNames) {
		if (privilegeNames != null && privilegeNames.length > 0) {
			PrivilegeExporter ex = new PrivilegeExporter(manageClient, privilegeNames);
			ex.setUriPrivilegeNames(privilegeNames);
			return add(ex);
		}
		return this;
	}

	public Exporter roles(String... roleNames) {
		return (roleNames != null && roleNames.length > 0) ? add(new RoleExporter(manageClient, roleNames)) : null;
	}

	public Exporter servers(String... serverNames) {
		return (serverNames != null && serverNames.length > 0) ? add(buildServerExporter(serverNames)) : null;
	}

	public Exporter serversNoDatabases(String... serverNames) {
		if (serverNames != null && serverNames.length > 0) {
			ServerExporter se = buildServerExporter(serverNames);
			se.setExportDatabases(false);
			return add(se);
		}
		return this;
	}

	protected ServerExporter buildServerExporter(String... serverNames) {
		return groupName != null ? new ServerExporter(groupName, manageClient, serverNames) : new ServerExporter(manageClient, serverNames);
	}

	public Exporter tasks(String... taskNames) {
		if (taskNames != null && taskNames.length > 0) {
			TaskExporter te;
			if (groupName != null) {
				te = new TaskExporter(groupName, manageClient, taskNames);
			} else {
				te = new TaskExporter(manageClient, taskNames);
			}
			return add(te);
		}
		return this;
	}

	public Exporter triggers(String databaseIdOrName, String... triggerNames) {
		return (triggerNames != null && triggerNames.length > 0) ? add(new TriggerExporter(manageClient, databaseIdOrName, triggerNames)) : null;
	}

	public Exporter users(String... usernames) {
		return (usernames != null && usernames.length > 0) ? add(new UserExporter(manageClient, usernames)) : null;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Exporter withTriggersDatabase(String triggersDatabase) {
		setTriggersDatabase(triggersDatabase);
		return this;
	}

	public void setTriggersDatabase(String triggersDatabase) {
		this.triggersDatabase = triggersDatabase;
	}
}
