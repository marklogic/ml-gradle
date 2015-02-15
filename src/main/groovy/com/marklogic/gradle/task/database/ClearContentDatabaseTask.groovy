package com.marklogic.gradle.task.database

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.manage.ManageTask
import com.marklogic.gradle.xcc.XccHelper

class ClearContentDatabaseTask extends ManageTask {

    @TaskAction
    void clearModules() {
        if (!xdbcServerExists()) {
            println "No XDBC server exists yet, so not clearing modules\n"
            return
        }

        String xquery = null;
        
        if (project.hasProperty("collection")) {
            xquery = "xdmp:collection-delete('" + project.property("collection") + "')"
        } else if (project.hasProperty("deleteAll")) {
            xquery = "for \$forest-id in xdmp:database-forests(xdmp:database()) return xdmp:forest-clear(\$forest-id)"
        } else {
            println "To delete the documents in one collection, specify a collection via -Pcollection=name."
            println "To delete all documents in the database, include the deleteAll parameter e.g. -PdeleteAll=true"
            return
        }
        
        println "Clearing modules: " + xquery + "\n"
        new XccHelper(getDefaultXccUrl()).executeXquery(xquery)
    }
}
