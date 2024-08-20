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
package com.marklogic.mgmt.selector;

public interface ResourceSelection {

	String AMPS = "amps";
	String CPF_CONFIGS = "cpfConfigs";
	String DATABASES = "databases";
	String DOMAINS = "domains";
	String GROUPS = "groups";
	String PIPELINES = "pipelines";
	String PRIVILEGES_EXECUTE = "privilegesExecute";
	String PRIVILEGES_URI = "privilegesUri";
	String ROLES = "roles";
	String SERVERS = "servers";
	String TASKS = "tasks";
	String TRIGGERS = "triggers";
	String USERS = "users";

	String[] getCpfConfigNames();

	String[] getDatabaseNames();

	String[] getDomainNames();

	String[] getGroupNames();

	String[] getPipelineNames();

	String[] getPrivilegeExecuteNames();

	String[] getPrivilegeUriNames();

	String[] getRoleNames();

	String[] getServerNames();

	String[] getTaskNames();

	String[] getTriggerNames();

	String[] getUserNames();

	/**
	 * Because an amp cannot be uniquely identified solely by its name, the implementation is expected to return the
	 * full "uriref" that the Manage API defines for an amp, as a uriref uniquely identifies the amp.
	 *
	 * @return
	 */
	String[] getAmpUriRefs();
}
