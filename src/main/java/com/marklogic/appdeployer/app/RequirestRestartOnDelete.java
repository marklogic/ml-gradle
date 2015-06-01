package com.marklogic.appdeployer.app;

/**
 * Marker interface for plugins that require a restart after completing their onDelete method. Implemented as a marker
 * interface so that ProjectManager knows to get the last restart timestamp before invoking the plugin.
 */
public interface RequirestRestartOnDelete {

}
