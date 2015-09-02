package com.rjrudin.marklogic.appdeployer.command;

public abstract class SortOrderConstants {

    public static Integer DEPLOY_GROUPS = 5;

    public static Integer DEPLOY_PRIVILEGES = 10;
    public static Integer DEPLOY_ROLES = 20;
    public static Integer DEPLOY_USERS = 30;
    public static Integer DEPLOY_AMPS = 40;
    public static Integer DEPLOY_CERTIFICATE_TEMPLATES = 50;
    public static Integer GENERATE_TEMPORARY_CERTIFICATE = 55;
    public static Integer DEPLOY_CERTIFICATE_AUTHORITIES = 60;
    public static Integer DEPLOY_EXTERNAL_SECURITY = 70;
    public static Integer DEPLOY_PROTECTED_COLLECTIONS = 80;

    public static Integer DEPLOY_TRIGGERS_DATABASE = 100;
    public static Integer DEPLOY_SCHEMAS_DATABASE = 100;
    public static Integer DEPLOY_CONTENT_DATABASES = 120;
    public static Integer DEPLOY_OTHER_DATABASES = 130;
    public static Integer DEPLOY_FORESTS = 150;

    public static Integer DEPLOY_REST_API_SERVERS = 200;
    public static Integer UPDATE_REST_API_SERVERS = 250;
    public static Integer DEPLOY_OTHER_SERVERS = 300;

    // Modules have to be loaded after the REST API server has been updated, for if the deployer is expecting to load
    // modules via SSL, then the REST API server must already be configured with a certificate template
    public static Integer LOAD_MODULES = 400;


    public static Integer DEPLOY_SCHEDULED_TASKS = 800;

    public static Integer DEPLOY_DEFAULT_PIPELINES = 900;
    public static Integer DEPLOY_PIPELINES = 905;
    public static Integer DEPLOY_DOMAINS = 910;
    public static Integer DEPLOY_CPF_CONFIGS = 920;

    public static Integer DEPLOY_FLEXREP_CONFIGS = 1000;
    public static Integer DEPLOY_FLEXREP_TARGETS = 1010;
    
    public static Integer DEPLOY_SQL_VIEWS = 1100;

    // Undo constants
    public static Integer DELETE_GROUPS = 10000;

    public static Integer DELETE_USERS = 9000;
    public static Integer DELETE_CERTIFICATE_TEMPLATES = 9010;
    public static Integer DELETE_CERTIFICATE_AUTHORITIES = 9020;
    public static Integer DELETE_EXTERNAL_SECURITY = 9030;
    public static Integer DELETE_PROTECTED_COLLECTIONS = 9040;
    public static Integer DELETE_PRIVILEGES = 9050;
    public static Integer DELETE_AMPS = 9060;
    public static Integer DELETE_ROLES = 9070;

    public static Integer DELETE_CONTENT_DATABASES = 8000;
    public static Integer DELETE_OTHER_DATABASES = 8005;
    public static Integer DELETE_TRIGGERS_DATABASE = 8010;
    public static Integer DELETE_SCHEMAS_DATABASE = 8020;
    public static Integer DELETE_FORESTS = 8040;

    public static Integer DELETE_REST_API_SERVERS = 7000;
    public static Integer DELETE_OTHER_SERVERS = 7010;

    public static Integer DELETE_SCHEDULED_TASKS = 1000;
}
