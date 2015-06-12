package com.marklogic.appdeployer.command.security.users;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.appdeployer.command.AbstractCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.UndoableCommand;
import com.marklogic.rest.mgmt.security.users.UserManager;

public class CreateUsersCommand extends AbstractCommand implements UndoableCommand {

    private boolean deleteUsersOnUndo = true;

    @Override
    public Integer getExecuteSortOrder() {
        return 10;
    }

    @Override
    public Integer getUndoSortOrder() {
        return getExecuteSortOrder();
    }

    @Override
    public void execute(CommandContext context) {
        File userDir = getUserDir(context);
        if (userDir.exists()) {
            UserManager mgr = new UserManager(context.getManageClient());
            for (File f : userDir.listFiles()) {
                if (f.getName().endsWith(".json")) {
                    mgr.createUser(copyFileToString(f));
                }
            }
        }
    }

    protected File getUserDir(CommandContext context) {
        return new File(context.getAppConfig().getConfigDir().getSecurityDir(), "users");
    }

    @Override
    public void undo(CommandContext context) {
        if (deleteUsersOnUndo) {
            File userDir = getUserDir(context);
            if (userDir.exists()) {
                UserManager mgr = new UserManager(context.getManageClient());
                ObjectMapper mapper = new ObjectMapper();
                for (File f : userDir.listFiles()) {
                    if (f.getName().endsWith(".json")) {
                        JsonNode node = null;
                        try {
                            node = mapper.readTree(f);
                        } catch (Exception e) {
                            throw new RuntimeException("Unable to read user JSON from file: " + f.getAbsolutePath(), e);
                        }
                        mgr.deleteUser(node.get("user-name").asText());
                    }
                }
            }
        }
    }

    public void setDeleteUsersOnUndo(boolean deleteUserOnUndo) {
        this.deleteUsersOnUndo = deleteUserOnUndo;
    }

}
