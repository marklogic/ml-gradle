package com.marklogic.gradle.task.security

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.xcc.XccHelper

class RemoveAmpTask extends DefaultTask {

    String xccUrl
    String namespace
    String localName
    String documentUri
    String[] databaseNames

    @TaskAction
    void executeXquery() {
        String databaseNamesStr = "'" + databaseNames.join("', '") + "'"

        String xquery = "xdmp:eval(\"xquery version '1.0-ml'; " +
                "import module namespace admin = 'http://marklogic.com/xdmp/admin' at '/MarkLogic/admin.xqy'; " +
                "import module namespace sec = 'http://marklogic.com/xdmp/security' at '/MarkLogic/security.xqy'; " +
                "for \$db in (${databaseNamesStr}) " +
                "where admin:database-exists(admin:get-configuration(), \$db) and " +
                "sec:amp-exists('${namespace}', '${localName}', '${documentUri}', xdmp:database(\$db)) " +
                "return sec:remove-amp('${namespace}', '${localName}', '${documentUri}', xdmp:database(\$db)) " +
                "\", (), <options xmlns='xdmp:eval'><database>{xdmp:security-database()}</database></options>)";

        println "Removing amp"
        new XccHelper(xccUrl).executeXquery(xquery)
    }
}
