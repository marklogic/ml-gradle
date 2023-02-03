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
package com.marklogic.mgmt.api.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.resource.tasks.TaskManager;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "task-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Task extends Resource {

	@XmlElement(name = "group-name")
    private String groupName = Group.DEFAULT_GROUP_NAME;

	@XmlElement(name = "task-id")
    private String taskId;

	@XmlElement(name = "task-enabled")
    private Boolean taskEnabled;

	@XmlElement(name = "task-path")
    private String taskPath;

	@XmlElement(name = "task-root")
    private String taskRoot;

	@XmlElement(name = "task-type")
    private String taskType;

	@XmlElement(name = "task-period")
    private Integer taskPeriod;

	@XmlElement(name = "task-month-day")
    private Integer taskMonthDay;

	@XmlElementWrapper(name = "task-days")
	@XmlElement(name = "task-day")
    private List<String> taskDay;

	@XmlElement(name = "task-start-date")
    private String taskStartDate;

	@XmlElement(name = "task-start-time")
    private String taskStartTime;

	@XmlElement(name = "task-timestamp")
    private String taskTimestamp;

	@XmlElement(name = "task-database")
    private String taskDatabase;

	@XmlElement(name = "task-modules")
    private String taskModules;

	@XmlElement(name = "task-user")
    private String taskUser;

	@XmlElement(name = "task-host")
    private String taskHost;

	@XmlElement(name = "task-priority")
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

    public String getTaskTimestamp() {
        return taskTimestamp;
    }

    public void setTaskTimestamp(String taskTimestamp) {
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
