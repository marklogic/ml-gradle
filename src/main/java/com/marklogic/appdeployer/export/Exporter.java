package com.marklogic.appdeployer.export;

import com.marklogic.appdeployer.export.appservers.ServerExporter;
import com.marklogic.appdeployer.export.databases.DatabaseExporter;
import com.marklogic.appdeployer.export.groups.GroupExporter;
import com.marklogic.appdeployer.export.impl.CompositeResourceExporter;
import com.marklogic.appdeployer.export.security.AmpExporter;
import com.marklogic.appdeployer.export.security.PrivilegeExporter;
import com.marklogic.appdeployer.export.security.RoleExporter;
import com.marklogic.appdeployer.export.security.UserExporter;
import com.marklogic.appdeployer.export.tasks.TaskExporter;
import com.marklogic.client.ext.helper.LoggingObject;
import com.marklogic.mgmt.ManageClient;
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
		ResourceSelection selection = selector.selectResources(manageClient);
		amps(selection.getAmpUriRefs());
		databases(selection.getDatabaseNames());
		privilegesExecute(selection.getPrivilegeExecuteNames());
		privilegesUri(selection.getPrivilegeUriNames());
		roles(selection.getRoleNames());
		servers(selection.getServerNames());
		tasks(selection.getTaskNames());
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
		return add(new AmpExporter(manageClient, ampUriRefs));
	}

	public Exporter databases(String... databaseNames) {
		return add(new DatabaseExporter(manageClient, databaseNames));
	}

	public Exporter groups(String... groupNames) {
		return add(new GroupExporter(manageClient, groupNames));
	}

	public Exporter privilegesExecute(String... privilegeNames) {
		return add(new PrivilegeExporter(manageClient, privilegeNames));
	}

	public Exporter privilegesUri(String... privilegeNames) {
		PrivilegeExporter ex = new PrivilegeExporter(manageClient, privilegeNames);
		ex.setUriPrivilegeNames(privilegeNames);
		return add(ex);
	}

	public Exporter roles(String... roleNames) {
		return add(new RoleExporter(manageClient, roleNames));
	}

	public Exporter servers(String... serverNames) {
		return add(buildServerExporter(serverNames));
	}

	public Exporter serversNoDatabases(String... serverNames) {
		ServerExporter se = buildServerExporter(serverNames);
		se.setExportDatabases(false);
		return add(se);
	}

	protected ServerExporter buildServerExporter(String... serverNames) {
		return groupName != null ? new ServerExporter(groupName, manageClient, serverNames) : new ServerExporter(manageClient, serverNames);
	}

	public Exporter tasks(String... taskNames) {
		TaskExporter te;
		if (groupName != null) {
			te = new TaskExporter(groupName, manageClient, taskNames);
		} else {
			te = new TaskExporter(manageClient, taskNames);
		}
		return add(te);
	}

	public Exporter users(String... usernames) {
		return add(new UserExporter(manageClient, usernames));
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
