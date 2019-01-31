package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.AbstractConfirmableTask
import com.marklogic.mgmt.resource.databases.DatabaseManager
import org.gradle.api.GradleException

class DeleteDatabaseTask extends AbstractConfirmableTask {

	@Override
	void executeIfConfirmed() {
		if (project.hasProperty("database")) {
			String db = project.property("database")
			DatabaseManager mgr = new DatabaseManager(getManageClient())
			mgr.setForestDelete(DatabaseManager.DELETE_FOREST_DATA)
			println "Deleting primary and replica forests for database: " + db
			mgr.deleteForestsAndReplicas(db)
			println "Deleting database: " + db
			mgr.deleteByName(db)
			println "Finished deleting database: " + db
		} else {
			throw new GradleException("The property 'database' must be specified")
		}
	}
}
