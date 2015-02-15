package com.marklogic.gradle.task.trigger

import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask;
import com.marklogic.gradle.xcc.XccHelper

class CreateTriggersTask extends MarkLogicTask {

    String xccUrl
    String triggerName
    String description
    String triggersDatabaseName
    
    String dataEventScope = "collection"
    String dataEventContent = "document"
    String dataEventCommit = "pre"

    String[] dataEventScopeArgs
    String[] dataEventContentArgs

    String moduleDatabase
    String moduleRoot
    String modulePath

    boolean enabled = true
    String permissions = "xdmp:default-permissions()"
    boolean recursive = true
    String taskPriority = "normal"

    boolean recreate = true

    @TaskAction
    void createTriggers() {
        XccHelper xccHelper = new XccHelper(xccUrl)

        if (!triggersDatabaseName) {
            triggersDatabaseName = getAppConfig().getName() + "-triggers"
        }
        
        boolean isAnyPropertyContent = !dataEventContentArgs

        if (isAnyPropertyContent) {
            if (recreate) {
                removeTrigger(xccHelper, triggerName)
                createTrigger(xccHelper, triggerName, "")
            }
        }
        else {
            for (dataEventContentArg in dataEventContentArgs) {
                String theTriggerName = triggerName + "-" + dataEventContentArg
                removeTrigger(xccHelper, theTriggerName)
                createTrigger(xccHelper, theTriggerName, '"' + dataEventContentArg + '"')
            }
        }
    }

    void createTrigger(XccHelper xccHelper, String triggerName, String dataEventContentArg) {
        println "Creating trigger with name of " + triggerName
        xccHelper.executeXquery(buildCreationXquery(triggerName, dataEventContentArg))
    }

    String buildCreationXquery(String triggerName, String dataEventContentArg) {
        if (moduleDatabase == null) {
            moduleDatabase = getAppConfig().getName() + "-modules"
        }

        String dataEventScopeFunction = 'trgr:' + dataEventScope + '-scope(' + dataEventScopeArgs.collect{'"' + it + '"'}.join(', ') + ')'
        String dataEventContentFunction = 'trgr:' + dataEventContent + '-content(' + dataEventContentArg + ')'
        String dataEventWhenFunction = 'trgr:' + dataEventCommit + '-commit()'

        String[] args = [
            '"' + triggerName + '"',
            '"' + description + '"',
            'trgr:trigger-data-event(' + dataEventScopeFunction + ', ' + dataEventContentFunction + ', ' + dataEventWhenFunction + ')',
            'trgr:trigger-module(xdmp:database("' + moduleDatabase + '"), "' + moduleRoot + '", "' + modulePath + '")',
            enabled ? 'fn:true()' : 'fn:false()',
            permissions
        ]

        return wrapInEval('trgr:create-trigger(' + args.join(', ') + ')')
    }

    void removeTrigger(XccHelper xccHelper, String theTriggerName) {
        String xquery = wrapInEval('trgr:remove-trigger("' + theTriggerName + '")')
        // triggers.xqy doesn't have a function for checking if a trigger exists, so we have to try/catch here in case
        // the trigger doesn't exist
        try {
            println "Attempting to remove trigger with name of " + theTriggerName
            xccHelper.executeXquery(xquery)
        } catch (Exception e) {
            // Ignore, assuming this is because the trigger doesn't exist
        }
    }

    String wrapInEval(String xquery) {
        String preamble = "xdmp:eval('"
        preamble += 'xquery version "1.0-ml"; import module namespace trgr="http://marklogic.com/xdmp/triggers" at "/MarkLogic/triggers.xqy"; '
        
        String ending = "', (), <options xmlns='xdmp:eval'><database>{xdmp:database('" + triggersDatabaseName + "')}</database></options>)"

        return preamble + xquery + ending
    }

}
