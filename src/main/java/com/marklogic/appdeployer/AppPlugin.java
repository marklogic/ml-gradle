package com.marklogic.appdeployer;

/**
 * A plugin is used to provide additional behavior when creating and deleting an application; it keeps the AppDeployer
 * instance from being tightly coupled to all the things a developer may want to happen as part of creating or deleting
 * an application.
 */
public interface AppPlugin {

    /**
     * Return a number corresponding to the order in which this plugin should execute when an application is being
     * created, where the lower the number, the earlier the plugin is invoked.
     */
    public Integer getSortOrderOnCreate();

    /**
     * Return a number corresponding to the order in which this plugin should execute when an application is being
     * deleted, where the lower the number, the earlier the plugin is invoked.
     */
    public Integer getSortOrderOnDelete();

    /**
     * Configure the application in some way when it is being created.
     * 
     * @param context
     */
    public void onCreate(AppPluginContext context);

    /**
     * Delete some aspect of the application while it is being deleted. This gives the plugin a chance to cleanup/delete
     * whatever it created. A plugin that needs MarkLogic to restart after it's done deleting resources (such as a
     * plugin that deletes an app server) should also implement the RequiresRestartOnDelete marker interface.
     * 
     * @param context
     */
    public void onDelete(AppPluginContext context);
}
