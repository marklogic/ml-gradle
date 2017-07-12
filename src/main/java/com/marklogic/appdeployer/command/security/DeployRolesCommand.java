package com.marklogic.appdeployer.command.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.marklogic.appdeployer.command.AbstractResourceCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.mgmt.PayloadParser;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.resource.security.RoleManager;
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

		if (context.getAppConfig().isSortRolesByDependencies() && files != null && files.length > 0) {
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
		if (files == null || files.length < 1) {
			return roleFiles;
		}
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

		return sortRoleFiles(roleFiles);
	}

	protected List<RoleFile> sortRoleFiles(List<RoleFile> roleFiles) {
		RoleFileComparator comparator = new RoleFileComparator(roleFiles);
		Collections.sort(roleFiles, comparator);
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

class RoleFile {

	File file;
	Role role;

	public RoleFile(File file) {
		this.file = file;
		this.role = new Role();
		this.role.setRole(new ArrayList<String>());
	}

}

/**
 * This comparator is designed to handle a scenario where two roles are next to each other, but they don't have any
 * dependencies in common, nor does one depend on the other. In this scenario, we need to know which role has a
 * dependency on a role furthest to the end of the list of roles. In order to know that, we first build up a data
 * structure that tracks the highest position of a dependency in the list of roles for each role.
 */
class RoleFileComparator implements Comparator<RoleFile> {

	private Map<String, Integer> highestDependencyPositionMap;

	public RoleFileComparator(List<RoleFile> roleFiles) {
		Map<String, Integer> rolePositions = new HashMap<>();
		for (int i = 0; i < roleFiles.size(); i++) {
			rolePositions.put(roleFiles.get(i).role.getRoleName(), i);
		}

		highestDependencyPositionMap = new HashMap<>();
		for (RoleFile rf : roleFiles) {
			String roleName = rf.role.getRoleName();
			int highest = -1;
			for (String role : rf.role.getRole()) {
				if (rolePositions.containsKey(role)) {
					int pos = rolePositions.get(role);
					if (pos > highest) {
						highest = pos;
					}
				}
			}
			highestDependencyPositionMap.put(roleName, highest);
		}
	}

	@Override
	public int compare(RoleFile o1, RoleFile o2) {
		if (o1 == null && o2 != null) {
			return 1;
		}
		if (o2 == null) {
			return -1;
		}
		if (o1.role.getRole() == null || o1.role.getRole().isEmpty()) {
			return -1;
		}
		if (o2.role.getRole() == null || o2.role.getRole().isEmpty()) {
			return 1;
		}
		if (o2.role.getRole().contains(o1.role.getRoleName())) {
			return -1;
		}
		if (o1.role.getRole().contains(o2.role.getRoleName())) {
			return 1;
		}

		/**
		 * If the roles aren't dependent on each other, then we want to base this on which role has a dependency further
		 * to the right.
		 */
		int o1Pos = highestDependencyPositionMap.get(o1.role.getRoleName());
		int o2Pos = highestDependencyPositionMap.get(o2.role.getRoleName());
		if (o1Pos > o2Pos) {
			return 1;
		}
		if (o2Pos > o1Pos) {
			return -1;
		}

		/**
		 * This would be for two roles that depend on the same other role.
		 */
		return 0;
	}
}
