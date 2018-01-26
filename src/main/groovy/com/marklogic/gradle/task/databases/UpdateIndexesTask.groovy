package com.marklogic.gradle.task.databases

import com.marklogic.gradle.task.MarkLogicTask
import org.gradle.api.tasks.TaskAction

class UpdateIndexesTask extends MarkLogicTask {

	@TaskAction
	void updateIndexes() {
		getAppConfig().setIncludeProperties(
			"database-name",
			"stemmed-searches",
			"word-searches",
			"word-positions",
			"fast-phrase-searches",
			"fast-reverse-searches",
			"triple-index",
			"triple-positions",
			"fast-case-sensitive-searches",
			"fast-diacritic-sensitive-searches",
			"fast-element-word-searches",
			"element-word-positions",
			"fast-element-phrase-searches",
			"element-value-positions",
			"attribute-value-positions",
			"field-value-searches",
			"field-value-positions",
			"three-character-searches",
			"three-character-word-positions",
			"fast-element-character-searches",
			"trailing-wildcard-searches",
			"trailing-wildcard-word-positions",
			"fast-element-trailing-wildcard-searches",
			"word-lexicon",
			"word-lexicons",
			"two-character-searches",
			"one-character-searches",
			"uri-lexicon",
			"collection-lexicon",
			"element-word-query-through",
			"element-word-query-throughs",
			"phrase-through",
			"phrase-throughs",
			"phrase-around",
			"phrase-arounds",
			"range-element-index",
			"range-element-indexes",
			"range-element-attribute-index",
			"range-element-attribute-indexes",
			"range-path-index",
			"range-path-indexes",
			"range-field-index",
			"range-field-indexes",
			"field",
			"fields",
			"geospatial-element-index",
			"geospatial-element-indexes",
			"geospatial-element-child-index",
			"geospatial-element-child-indexes",
			"geospatial-element-pair-index",
			"geospatial-element-pair-indexes",
			"geospatial-element-attribute-pair-index",
			"geospatial-element-attribute-pair-indexes",
			"geospatial-path-index",
			"geospatial-path-indexes",
			"geospatial-region-path-index",
			"geospatial-region-path-indexes"
		)

		deployWithCommandListProperty("mlDatabaseCommands")
	}
}
