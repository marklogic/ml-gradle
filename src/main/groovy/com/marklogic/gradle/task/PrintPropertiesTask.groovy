package com.marklogic.gradle.task

import com.marklogic.appdeployer.DefaultAppConfigFactory
import com.marklogic.mgmt.DefaultManageConfigFactory
import com.marklogic.mgmt.admin.DefaultAdminConfigFactory
import org.gradle.api.tasks.TaskAction

class PrintPropertiesTask extends MarkLogicTask {

	@TaskAction
	void printProperties() {
		DefaultManageConfigFactory manageConfigFactory = getProject().property("mlManageConfigFactory")
		println "\nManage server connection properties"
		for (String name : new TreeSet<>(manageConfigFactory.getPropertyConsumerMap().keySet())) {
			println " - " + name
		}

		DefaultAdminConfigFactory adminConfigFactory = getProject().property("mlAdminConfigFactory")
		println "\nAdmin server connection properties"
		for (String name : new TreeSet<>(adminConfigFactory.getPropertyConsumerMap().keySet())) {
			println " - " + name
		}

		DefaultAppConfigFactory appConfigFactory = getProject().property("mlAppConfigFactory")
		println "\nApplication properties"
		for (String name : new TreeSet<>(appConfigFactory.getPropertyConsumerMap().keySet())) {
			println " - " + name
		}

		println "\nThe Manage server connection properties, Admin server connection properties, and " +
			"application properties are listed above, with each set in alphabetical order. \nFor a list of " +
			"all properties with documentation, see https://github.com/marklogic-community/ml-gradle/wiki/Property-reference ."
	}
}
