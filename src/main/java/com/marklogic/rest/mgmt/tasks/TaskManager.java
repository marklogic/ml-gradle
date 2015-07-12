package com.marklogic.rest.mgmt.tasks;

import java.util.ArrayList;
import java.util.List;

import com.marklogic.rest.mgmt.AbstractResourceManager;
import com.marklogic.rest.mgmt.ManageClient;
import com.marklogic.rest.util.Fragment;

/**
 * A scheduled task doesn't have a name, and the ID is generated by ML, so this class assumes that task-path will be
 * unique and can thus be used as a way to find an existing task.
 * <p>
 * This class also assumes that the group is "Default" by default, which can be overridden. The docs at
 * http://docs.marklogic.com/REST/POST/manage/v2/tasks suggest that the payload can contain "group-name" to specify the
 * group, but as of ML 8.0-3, it doesn't work.
 */
public class TaskManager extends AbstractResourceManager {

    private String groupName = "Default";

    public TaskManager(ManageClient client) {
        super(client);
    }

    @Override
    public String getResourcePath(String resourceNameOrId) {
        return getResourcesPath() + "/" + getTaskIdForTaskPath(resourceNameOrId);
    }

    @Override
    protected String[] getUpdateResourceParams(String payload) {
        List<String> params = new ArrayList<>();
        params.add("group-id");
        params.add(groupName);
        return params.toArray(new String[] {});
    }

    @Override
    protected String getCreateResourcePath(String payload) {
        return getResourcesPath() + "?group-id=" + groupName;
    }

    @Override
    protected String getIdFieldName() {
        return "task-path";
    }

    public String getTaskIdForTaskPath(String taskPath) {
        Fragment f = getAsXml();
        String xpath = "/node()/*[local-name(.) = 'list-items']/node()"
                + "[*[local-name(.) = 'task-path'] = '%s']/*[local-name(.) = 'idref']";
        xpath = String.format(xpath, taskPath);
        String id = f.getElementValue(xpath);
        if (id == null) {
            throw new RuntimeException("Could not find a scheduled task with a task-path of: " + taskPath);
        }
        return id;
    }

    public boolean exists(String resourceNameOrId) {
        Fragment f = getAsXml();
        return f.elementExists(format(
                "/node()/*[local-name(.) = 'list-items']/node()[*[local-name(.) = 'task-path'] = '%s']",
                resourceNameOrId));
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
