package com.marklogic.gradle.task.database

import groovyx.net.http.HttpResponseDecorator

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.manage.ManageTask
import com.marklogic.gradle.xcc.XccHelper

class ClearModulesTask extends ManageTask {

    String[] excludes

    /**
     * Should be able to optimize this when no excludes exist by clearing the forests belonging to the modules database.
     */
    @TaskAction
    void clearModules() {
        if (!xdbcServerExists()) {
            println "No XDBC server exists yet, so not clearing modules\n"
            return
        }

        String xquery = "xdmp:eval(\"for \$uri in cts:uris((), (), cts:and-query(())) "
        if (excludes) {
            String excludesStr = excludes ? "('" + excludes.join("', '") + "')" : ""
            xquery += "where fn:not(\$uri = " + excludesStr + ") "
        }
        xquery += "return xdmp:document-delete(\$uri)\""
        xquery += ", (), <options xmlns='xdmp:eval'><database>{xdmp:modules-database()}</database></options>)"

        println "Clearing modules: " + xquery + "\n"
        new XccHelper(getDefaultXccUrl()).executeXquery(xquery)
    }
}
