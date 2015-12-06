package com.rjrudin.marklogic.gradle.task.cluster

import org.gradle.api.tasks.TaskAction

import com.rjrudin.marklogic.gradle.task.MarkLogicTask
import com.rjrudin.marklogic.mgmt.ManageClient
import com.rjrudin.marklogic.mgmt.admin.ActionRequiringRestart

class RestartClusterTask extends MarkLogicTask {

    @TaskAction
    void restartCluster() {
        final ManageClient client = getManageClient();
        getAdminManager().invokeActionRequiringRestart(new ActionRequiringRestart() {
                    public boolean execute() {
                        client.postJson("/manage/v2", "{\"operation\":\"restart-local-cluster\"}");
                        return true;
                    }
                });
    }
}
