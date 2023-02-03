/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task.trigger

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import com.marklogic.gradle.task.MarkLogicTask;
import com.marklogic.gradle.xcc.XccHelper

/**
 * This is only needed for MarkLogic 8.0-3 and earlier - 8.0-4 supports triggers via the Management API. It's also
 * mis-named a bit - you can only create/recreate a single trigger with it.
 */
class CreateTriggersTask extends MarkLogicTask {

	@Input
	String xccUrl

	@Input
	String triggerName

	@Input
	@Optional
	String description

	@Input
	@Optional
	String triggersDatabaseName

	@Input
	String dataEventScope = "collection"

	@Input
	String dataEventContent = "document"

	@Input
	String dataEventCommit = "pre"

	@Input
	@Optional
	String[] dataEventScopeArgs

	@Input
	@Optional
	String[] dataEventContentArgs

	@Input
	@Optional
	String moduleDatabase

	@Input
	@Optional
	String moduleRoot

	@Input
	@Optional
	String modulePath

	@Input
	boolean enabled = true

	@Input
	String permissions = "xdmp:default-permissions()"

	@Input
	boolean recursive = true

	@Input
	String taskPriority = "normal"

	@Input
	boolean recreate = true

    @TaskAction
    void createTriggers() {
        if (!xccUrl) {
            xccUrl = getAppConfig().getContentXccUrl()
        }
        XccHelper xccHelper = new XccHelper(xccUrl)

        if (!triggersDatabaseName) {
            triggersDatabaseName = getAppConfig().getTriggersDatabaseName()
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
            permissions,
            recursive ? 'fn:true()' : 'fn:false()',
            '"' + taskPriority + '"'
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
