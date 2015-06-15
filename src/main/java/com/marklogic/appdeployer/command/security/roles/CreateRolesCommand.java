package com.marklogic.appdeployer.command.security.roles;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.rest.mgmt.security.roles.RoleManager;

public class CreateRolesCommand extends AbstractCommand implements UndoableCommand {

    private boolean deleteRolesOnUndo = true;

    @Override
    public Integer getExecuteSortOrder() {
        return SortOrderConstants.CREATE_ROLES;
    }

    @Override
    public Integer getUndoSortOrder() {
        return getExecuteSortOrder();
    }

    @Override
    public void execute(CommandContext context) {
        File roleDir = getRoleDir(context);
        if (roleDir.exists()) {
            RoleManager mgr = new RoleManager(context.getManageClient());
            for (File f : roleDir.listFiles()) {
                if (f.getName().endsWith(".json")) {
                    mgr.createRole(copyFileToString(f));
                }
            }
        }
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteRolesOnUndo) {
            File roleDir = getRoleDir(context);
            if (roleDir.exists()) {
                RoleManager mgr = new RoleManager(context.getManageClient());
                ObjectMapper mapper = new ObjectMapper();
                for (File f : roleDir.listFiles()) {
                    if (f.getName().endsWith(".json")) {
                        JsonNode node = null;
                        try {
                            node = mapper.readTree(f);
                        } catch (Exception e) {
                            throw new RuntimeException("Unable to read role JSON from file: " + f.getAbsolutePath(), e);
                        }
                        mgr.deleteRole(node.get("role-name").asText());
                    }
                }
            }
        }
    }

    protected File getRoleDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "roles");
    }

    public void setDeleteRolesOnUndo(boolean deleteRoleOnUndo) {
        this.deleteRolesOnUndo = deleteRoleOnUndo;
    }

}
