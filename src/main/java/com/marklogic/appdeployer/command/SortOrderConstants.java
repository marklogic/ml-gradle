package com.marklogic.appdeployer.command;

public abstract class SortOrderConstants {

    public static Integer CREATE_ROLES = 10;
    public static Integer CREATE_USERS = 20;
    public static Integer CREATE_AMPS = 30;
    public static Integer CREATE_PRIVILEGES = 40;
    public static Integer CREATE_CERTIFICATE_TEMPLATES = 45;
    public static Integer CREATE_CERTIFICATE_AUTHORITIES = 50;
    public static Integer CREATE_EXTERNAL_SECURITY = 60;
    public static Integer CREATE_PROTECTED_COLLECTIONS = 70;

    public static Integer CREATE_REST_API_SERVERS = 100;
    public static Integer CREATE_TRIGGERS_DATABASE = 200;
    public static Integer CREATE_SCHEMAS_DATABASE = 250;
    public static Integer UPDATE_CONTENT_DATABASES = 300;
    public static Integer LOAD_MODULES_ORDER = 500;

    // This would have to be after loading modules in case the rewriter is modified
    public static Integer UPDATE_REST_API_SERVERS = 600;
    public static Integer MANAGE_OTHER_SERVERS = 650;
    
    public static Integer CREATE_SCHEDULED_TASKS = 800;
    
    public static Integer LOAD_DEFAULT_PIPELINES = 900;
    public static Integer CREATE_PIPELINES = 905;
    public static Integer CREATE_DOMAINS = 910;
    public static Integer CREATE_CPF_CONFIGS = 920;
    
    public static Integer CREATE_SQL_VIEWS = 1000;
    
}
