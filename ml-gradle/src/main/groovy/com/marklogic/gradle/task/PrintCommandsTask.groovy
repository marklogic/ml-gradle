/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.gradle.task

import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppDeployer
import com.marklogic.appdeployer.command.Command
import com.marklogic.appdeployer.command.UndoableCommand
import com.marklogic.appdeployer.impl.SimpleAppDeployer

class PrintCommandsTask extends MarkLogicTask {

    @TaskAction
    void printCommands() {
        AppDeployer d = getAppDeployer()
        if (!(d instanceof SimpleAppDeployer)) {
            println "mlAppDeployer is not an instance of SimpleAppDeployer, so unable to print commands"
        } else {
            List<Command> commands = ((SimpleAppDeployer)d).getCommands()
            for (Command c : commands) {
                println "Command class: " + c.getClass().getName()
                println "Execute order: " + c.getExecuteSortOrder()
                if (c instanceof UndoableCommand) {
                    println "Undo order:    " + ((UndoableCommand)c).getUndoSortOrder()
                }
                println ""
            }
        }
    }
}
