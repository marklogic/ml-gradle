package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.ResourceManager;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.security.RoleManager;
import com.marklogic.rest.util.Fragment;

import java.io.File;
import java.util.*;

public class DeployRolesCommand extends AbstractResourceCommand {

	public DeployRolesCommand() {
		setExecuteSortOrder(SortOrderConstants.DEPLOY_ROLES);
		setUndoSortOrder(SortOrderConstants.DELETE_ROLES);
	}

	/**
	 * Overriding this so we can list the files based on role dependencies.
	 *
	 * @param dir
	 * @return
	 */
	@Override
	protected File[] listFilesInDirectory(File dir, CommandContext context) {
		File[] files = super.listFilesInDirectory(dir);

		if (context.getAppConfig().isSortRolesByDependencies()) {
			if (logger.isInfoEnabled()) {
				logger.info("Sorting role files by role dependencies");
			}
			List<RoleFile> roleFiles = sortFilesBasedOnRoleDependencies(files, context);
			files = new File[files.length];
			for (int i = 0; i < roleFiles.size(); i++) {
				files[i] = roleFiles.get(i).file;
			}
		}

		return files;
	}

	protected List<RoleFile> sortFilesBasedOnRoleDependencies(File[] files, CommandContext context) {
		List<RoleFile> roleFiles = new ArrayList<>();
		PayloadParser parser = new PayloadParser();
		for (File f : files) {
			RoleFile rf = new RoleFile(f);
			String payload = copyFileToString(f, context);
			if (parser.isJsonPayload(payload)) {
				JsonNode json = parser.parseJson(payload);
				rf.role.setRoleName(json.get("role-name").asText());
				if (json.has("role")) {
					ArrayNode roles = (ArrayNode) json.get("role");
					Iterator<JsonNode> iter = roles.elements();
					while (iter.hasNext()) {
						rf.role.getRole().add(iter.next().asText());
					}
				}
			} else {
				Fragment frag = new Fragment(payload);
				rf.role.setRoleName(frag.getElementValue("/node()/m:role-name"));
				rf.role.setRole(frag.getElementValues("/node()/m:roles/m:role"));
			}
			roleFiles.add(rf);
		}

		Collections.sort(roleFiles);
		return roleFiles;
	}

	protected File[] getResourceDirs(CommandContext context) {
		return new File[]{context.getAppConfig().getConfigDir().getRolesDir()};
	}

	@Override
	protected ResourceManager getResourceManager(CommandContext context) {
		return new RoleManager(context.getManageClient());
	}

}

class RoleFile implements Comparable<RoleFile> {

	File file;
	Role role;

	public RoleFile(File file) {
		this.file = file;
		this.role = new Role();
		this.role.setRole(new ArrayList<String>());
	}

	@Override
	public int compareTo(RoleFile o) {
		return this.role.compareTo(o.role);
	}
}
