package com.rjrudin.marklogic.appdeployer.command;

public abstract class SortOrderConstants {

    public static Integer CREATE_GROUPS = 5;

    public static Integer CREATE_PRIVILEGES = 10;
    public static Integer CREATE_ROLES = 20;
    public static Integer CREATE_USERS = 30;
    public static Integer CREATE_AMPS = 40;
    public static Integer CREATE_CERTIFICATE_TEMPLATES = 50;
    public static Integer CREATE_CERTIFICATE_AUTHORITIES = 60;
    public static Integer CREATE_EXTERNAL_SECURITY = 70;
    public static Integer CREATE_PROTECTED_COLLECTIONS = 80;

    public static Integer CREATE_TRIGGERS_DATABASE = 100;
    public static Integer CREATE_SCHEMAS_DATABASE = 100;
    public static Integer CREATE_CONTENT_DATABASES = 120;
    public static Integer CREATE_FORESTS = 130;

    public static Integer CREATE_REST_API_SERVERS = 200;
    public static Integer LOAD_MODULES_ORDER = 500;

    // This would have to be after loading modules in case the rewriter is modified
    public static Integer UPDATE_REST_API_SERVERS = 600;
    public static Integer CREATE_OTHER_SERVERS = 650;

    public static Integer CREATE_SCHEDULED_TASKS = 800;

    public static Integer LOAD_DEFAULT_PIPELINES = 900;
    public static Integer CREATE_PIPELINES = 905;
    public static Integer CREATE_DOMAINS = 910;
    public static Integer CREATE_CPF_CONFIGS = 920;

    public static Integer CREATE_SQL_VIEWS = 1000;

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
    public static Integer DELETE_TRIGGERS_DATABASE = 8010;
    public static Integer DELETE_SCHEMAS_DATABASE = 8020;
    public static Integer DELETE_FORESTS = 8040;

    public static Integer DELETE_REST_API_SERVERS = 7000;
    public static Integer DELETE_OTHER_SERVERS = 7010;

    public static Integer DELETE_SCHEDULED_TASKS = 1000;
}
