package com.marklogic.mgmt.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.resource.tasks.TaskManager;

import java.util.List;

public class Task extends Resource {

    private String groupName = Group.DEFAULT_GROUP_NAME;

    private String taskId;
    private Boolean taskEnabled;
    private String taskPath;
    private String taskRoot;
    private String taskType;
    private Integer taskPeriod;
    private Integer taskMonthDay;
    private List<String> taskDay;
    private String taskStartDate;
    private String taskStartTime;
    private Long taskTimestamp;
    private String taskDatabase;
    private String taskModules;
    private String taskUser;
    private String taskHost;
    private String taskPriority;

    public Task() {
        super();
    }

    public Task(API api, String taskId) {
        super(api);
        this.taskId = taskId;
    }

    @Override
    protected ResourceManager getResourceManager() {
        TaskManager mgr = new TaskManager(getClient());
        mgr.setGroupName(groupName);
        return mgr;
    }

    @Override
    protected String getResourceId() {
        return taskId;
    }

    public void disable() {
        Task temp = new Task(getApi(), getTaskId());
        temp.setGroupName(getGroupName());
        temp.setTaskEnabled(false);
        temp.save();
    }

    public void enable() {
        Task temp = new Task(getApi(), getTaskId());
        temp.setGroupName(getGroupName());
        temp.setTaskEnabled(true);
        temp.save();
    }

    @JsonIgnore
    public String getGroupName() {
        return groupName;
    }

    @JsonIgnore
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Boolean getTaskEnabled() {
        return taskEnabled;
    }

    public void setTaskEnabled(Boolean taskEnabled) {
        this.taskEnabled = taskEnabled;
    }

    public String getTaskPath() {
        return taskPath;
    }

    public void setTaskPath(String taskPath) {
        this.taskPath = taskPath;
    }

    public String getTaskRoot() {
        return taskRoot;
    }

    public void setTaskRoot(String taskRoot) {
        this.taskRoot = taskRoot;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getTaskPeriod() {
        return taskPeriod;
    }

    public void setTaskPeriod(Integer taskPeriod) {
        this.taskPeriod = taskPeriod;
    }

    public Integer getTaskMonthDay() {
        return taskMonthDay;
    }

    public void setTaskMonthDay(Integer taskMonthDay) {
        this.taskMonthDay = taskMonthDay;
    }

    public List<String> getTaskDay() {
        return taskDay;
    }

    public void setTaskDay(List<String> taskDay) {
        this.taskDay = taskDay;
    }

    public String getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(String taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Long getTaskTimestamp() {
        return taskTimestamp;
    }

    public void setTaskTimestamp(Long taskTimestamp) {
        this.taskTimestamp = taskTimestamp;
    }

    public String getTaskDatabase() {
        return taskDatabase;
    }

    public void setTaskDatabase(String taskDatabase) {
        this.taskDatabase = taskDatabase;
    }

    public String getTaskModules() {
        return taskModules;
    }

    public void setTaskModules(String taskModules) {
        this.taskModules = taskModules;
    }

    public String getTaskUser() {
        return taskUser;
    }

    public void setTaskUser(String taskUser) {
        this.taskUser = taskUser;
    }

    public String getTaskHost() {
        return taskHost;
    }

    public void setTaskHost(String taskHost) {
        this.taskHost = taskHost;
    }

    public String getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(String taskPriority) {
        this.taskPriority = taskPriority;
    }

}
