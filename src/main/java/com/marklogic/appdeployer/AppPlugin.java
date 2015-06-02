package com.marklogic.appdeployer;

/**
 * A plugin is used to provide additional behavior when creating and deleting an application; it keeps the AppDeployer
 * instance from being tightly coupled to all the things a developer may want to happen as part of creating or deleting
 * an application.
 */
public interface AppPlugin {

    /**
     * Return a number corresponding to the order in which this plugin should execute when an application is being
     * deployed, where the lower the number, the earlier the plugin is invoked.
     */
    public Integer getSortOrderOnDeploy();

    /**
     * Return a number corresponding to the order in which this plugin should execute when an application is being
     * undeployed, where the lower the number, the earlier the plugin is invoked.
     */
    public Integer getSortOrderOnUndeploy();

    /**
     * Configure the application in some way when it is being deployed.
     * 
     * @param context
     */
    public void onDeploy(AppPluginContext context);

    /**
     * Delete some aspect of the application while it is being undeployed. This gives the plugin a chance to cleanup/delete
     * whatever it created.
     * 
     * @param context
     */
    public void onUndeploy(AppPluginContext context);
}
