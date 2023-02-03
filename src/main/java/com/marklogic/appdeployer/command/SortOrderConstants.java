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
package com.marklogic.appdeployer.command;

public abstract class SortOrderConstants {

	public static Integer DEPLOY_PRIVILEGES = 5;
	public static Integer DEPLOY_ROLES = 10;
	public static Integer DEPLOY_PROTECTED_PATHS = 12; // depends on roles
	public static Integer DEPLOY_QUERY_ROLESETS = 13; // depends on roles
	public static Integer DEPLOY_USERS = 15; // depends on roles

	// After users are deployed so they're not included in combined CMA requests
	public static Integer DEPLOY_PRIVILEGE_ROLES = 18;

	public static Integer DEPLOY_CERTIFICATE_AUTHORITIES = 20;
	public static Integer DEPLOY_CERTIFICATE_TEMPLATES = 24;
	public static Integer GENERATE_TEMPORARY_CERTIFICATE = 25;
	public static Integer INSERT_HOST_CERTIFICATES = 28;

	public static Integer DEPLOY_EXTERNAL_SECURITY = 35;
	public static Integer DEPLOY_SECURE_CREDENTIALS = 36;
	public static Integer DEPLOY_PROTECTED_COLLECTIONS = 40;
	public static Integer DEPLOY_MIMETYPES = 45;

	public static Integer DEPLOY_GROUPS = 90;

	// Hosts need to be assigned to their group before databases are created.
	// This is so that when forests are created based on the mlDatabaseGroups
	// then the forests will be created on the correct hosts.
	public static Integer ASSIGN_HOSTS_TO_GROUPS = 95;

	public static Integer DEPLOY_OTHER_DATABASES = 120;
	public static Integer DEPLOY_FORESTS = 150;
	public static Integer DEPLOY_PARTITIONS = 170;
	public static Integer DEPLOY_PARTITION_QUERIES = 175;

	public static Integer DEPLOY_REST_API_SERVERS = 200;
	public static Integer UPDATE_REST_API_SERVERS = 250;
	public static Integer DEPLOY_OTHER_SERVERS = 300;

	public static Integer MODIFY_LOCAL_CLUSTER = 350;

	// Module code may depend on schemas, but not vice-versa.
	public static Integer LOAD_SCHEMAS = 350;

	// The modules database must exist before we deploy amps, but in the event that loading modules depends
	// on an amp (will be true in DHF 5.5), amps should be deployed before modules are loaded.
	public static Integer DEPLOY_AMPS = 390;

	// Modules have to be loaded after the REST API server has been updated, for if the deployer is expecting to load
	// modules via SSL, then the REST API server must already be configured with a certificate template
	public static Integer LOAD_MODULES = 400;
	public static Integer DELETE_TEST_MODULES = 410;

	public static Integer DEPLOY_TRIGGERS = 700;

	public static Integer DEPLOY_TEMPORAL_AXIS = 750;
	public static Integer DEPLOY_TEMPORAL_COLLECTIONS = 751;
	public static Integer DEPLOY_TEMPORAL_COLLECTIONS_LSQT = 752;

	public static Integer DEPLOY_SCHEDULED_TASKS = 800;
	public static Integer UPDATE_TASK_SERVER = 810;

	public static Integer DEPLOY_DEFAULT_PIPELINES = 900;
	public static Integer DEPLOY_PIPELINES = 905;
	public static Integer DEPLOY_DOMAINS = 910;
	public static Integer DEPLOY_CPF_CONFIGS = 920;

	public static Integer DEPLOY_ALERT_CONFIGS = 950;
	public static Integer DEPLOY_ALERT_ACTIONS = 960;
	public static Integer DEPLOY_ALERT_RULES = 970;

	public static Integer DEPLOY_FLEXREP_CONFIGS = 1000;
	public static Integer DEPLOY_FLEXREP_TARGETS = 1010;
	public static Integer DEPLOY_FLEXREP_PULLS = 1020;

	public static Integer DEPLOY_SQL_VIEWS = 1100;

	public static Integer DEPLOY_FOREST_REPLICAS = 1200;

	public static Integer LOAD_DATA = 1300;

	public static Integer INSTALL_PLUGINS = 1400;

	public static Integer DELETE_MIMETYPES = 8500;

	public static Integer UNASSIGN_HOSTS_FROM_GROUPS = 8590;
	public static Integer DELETE_GROUPS = 8600;

	public static Integer DELETE_USERS = 9000;
	public static Integer DELETE_CERTIFICATE_TEMPLATES = 9010;
	public static Integer DELETE_EXTERNAL_SECURITY = 9030;
	public static Integer DELETE_PROTECTED_COLLECTIONS = 9040;
	public static Integer DELETE_QUERY_ROLESETS = 9050;

	// Roles can reference privileges, so must delete roles first
	public static Integer DELETE_ROLES = 9060;
	public static Integer DELETE_PRIVILEGES = 9070;
	// Protected paths reference roles
	public static Integer DELETE_PROTECTED_PATHS = 9080;


	/*
	 * This executes before databases are deleted, as deleting databases normally deletes the primary forests, so we
	 * need to make sure the replicas are deleted first.
	 */
	public static Integer DELETE_FOREST_REPLICAS = 8000;

	public static Integer DELETE_OTHER_DATABASES = 8120;

	public static Integer DELETE_REST_API_SERVERS = 7000;

	// Amps can reference roles and databases, so must delete amps before both (and before deleting REST API servers
	// too, which may delete databases)
	public static Integer DELETE_AMPS = 2000;

	public static Integer DELETE_SCHEDULED_TASKS = 1000;

	public static Integer DELETE_OTHER_SERVERS = 750;

	public static Integer UNINSTALL_PLUGINS = 500;
}
