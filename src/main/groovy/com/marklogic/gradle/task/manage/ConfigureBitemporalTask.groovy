package com.marklogic.gradle.task.manage

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask;
import com.marklogic.gradle.xcc.XccHelper

class ConfigureBitemporalTask extends MarkLogicTask {

    String xccUrl

    @TaskAction
    void executeXquery() {
        String temporalConfigFilePath = "src/main/xqy/packages/temporal-config.xml"

        if (new File(temporalConfigFilePath).exists()) {
            String xml = new File(temporalConfigFilePath).text

            String xquery =
                "xdmp:eval(\"                                                                          " +
                    "xquery version '1.0-ml';                                                          " +
                    "import module namespace temporal = 'http://marklogic.com/xdmp/temporal'           " +
                    "    at '/MarkLogic/temporal.xqy';                                                 " +
                    "declare variable \$config as element(temporal:config) external;                   " +
                    "(                                                                                 " +
                    "    for \$axis in \$config/temporal:axis                                          " +
                    "    return                                                                        " +
                    "        try {                                                                     " +
                    "          temporal:axis-create(                                                   " +
                    "            \$axis/fn:data(temporal:axis-name),                                   " +
                    "            cts:reference-parse(\$axis/temporal:axis-start/cts:element-reference)," +
                    "            cts:reference-parse(\$axis/temporal:axis-end/cts:element-reference)   " +
                    "          )                                                                       " +
                    "        }                                                                         " +
                    "        catch(\$e) {                                                              " +
                    "            if (\$e/error:code = 'TEMPORAL-DUPAXIS') then                         " +
                    "                ()                                                                " +
                    "            else                                                                  " +
                    "                xdmp:rethrow()                                                    " +
                    "        }                                                                         " +
                    "    ,                                                                             " +
                    "    for \$collection in \$config/temporal:collection                              " +
                    "    return                                                                        " +
                    "        try {                                                                     " +
                    "          temporal:collection-create(                                             " +
                    "            \$collection/fn:data(temporal:collection-name),                       " +
                    "            \$collection/fn:data(temporal:system-axis),                           " +
                    "            \$collection/fn:data(temporal:valid-axis)                             " +
                    "          )                                                                       " +
                    "        }                                                                         " +
                    "        catch(\$e) {                                                              " +
                    "            if (\$e/error:code = 'TEMPORAL-DUPCOLLECTION') then                   " +
                    "                ()                                                                " +
                    "            else                                                                  " +
                    "                xdmp:rethrow()                                                    " +
                    "        }                                                                         " +
                    ")                                                                                 " +
                "\",                                                                                   " +
                "(xs:QName('config'), xdmp:unquote('${xml}')/*))                                       ";

            println "Creating bi-temporal axes and collections"
            
            if (!xccUrl) {
                xccUrl = getDefaultXccUrl()
            }
        
            new XccHelper(xccUrl).executeXquery(xquery)
        }
    }
}


