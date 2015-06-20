package com.marklogic.appdeployer.command;

public abstract class SortOrderConstants {

    public static Integer CREATE_ROLES = 10;
    public static Integer CREATE_USERS = 20;
    public static Integer CREATE_AMPS = 30;
    public static Integer CREATE_PRIVILEGES = 40;
    public static Integer CREATE_CERTIFICATE_AUTHORITIES = 50;
    
    public static Integer CREATE_REST_API_SERVERS_ORDER = 100;
    public static Integer CREATE_TRIGGERS_DATABASE_ORDER = 200;
    public static Integer UPDATE_CONTENT_DATABASES_ORDER = 300;
    public static Integer LOAD_MODULES_ORDER = 500;

    // This would have to be after loading modules in case the rewriter is modified
    public static Integer UPDATE_REST_API_SERVERS_ORDER = 600;

    public static Integer CREATE_DOMAINS_ORDER = 900;
    public static Integer CREATE_CPF_CONFIGS_ORDER = 910;
    public static Integer CREATE_PIPELINES_ORDER = 920;
}
