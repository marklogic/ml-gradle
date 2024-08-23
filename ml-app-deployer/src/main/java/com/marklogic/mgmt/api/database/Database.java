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
package com.marklogic.mgmt.api.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marklogic.mgmt.api.API;
import com.marklogic.mgmt.api.Resource;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.resource.ResourceManager;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "database-properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Database extends Resource {

	@XmlElement(name = "database-name")
	private String databaseName;

	@XmlElementWrapper(name = "forests")
	private List<String> forest;

	@XmlElement(name = "security-database")
	private String securityDatabase;

	@XmlElement(name = "schema-database")
	private String schemaDatabase;

	@XmlElement(name = "triggers-database")
	private String triggersDatabase;
	private Boolean enabled;

	@XmlElement(name = "retired-forest-count")
	private Integer retiredForestCount;
	private String language;

	@XmlElement(name = "stemmed-searches")
	private String stemmedSearches;

	@XmlElement(name = "word-searches")
	private Boolean wordSearches;

	@XmlElement(name = "word-positions")
	private Boolean wordPositions;

	@XmlElement(name = "fast-phrase-searches")
	private Boolean fastPhraseSearches;

	@XmlElement(name = "fast-reverse-searches")
	private Boolean fastReverseSearches;

	@XmlElement(name = "triple-index")
	private Boolean tripleIndex;

	@XmlElement(name = "triple-positions")
	private Boolean triplePositions;

	@XmlElement(name = "fast-case-sensitive-searches")
	private Boolean fastCaseSensitiveSearches;

	@XmlElement(name = "fast-diacritic-sensitive-searches")
	private Boolean fastDiacriticSensitiveSearches;

	@XmlElement(name = "fast-element-word-searches")
	private Boolean fastElementWordSearches;

	@XmlElement(name = "element-word-positions")
	private Boolean elementWordPositions;

	@XmlElement(name = "fast-element-phrase-searches")
	private Boolean fastElementPhraseSearches;

	@XmlElement(name = "element-value-positions")
	private Boolean elementValuePositions;

	@XmlElement(name = "attribute-value-positions")
	private Boolean attributeValuePositions;

	@XmlElement(name = "field-value-searches")
	private Boolean fieldValueSearches;

	@XmlElement(name = "field-value-positions")
	private Boolean fieldValuePositions;

	@XmlElement(name = "three-character-searches")
	private Boolean threeCharacterSearches;

	@XmlElement(name = "three-character-word-positions")
	private Boolean threeCharacterWordPositions;

	@XmlElement(name = "fast-element-character-searches")
	private Boolean fastElementCharacterSearches;

	@XmlElement(name = "trailing-wildcard-searches")
	private Boolean trailingWildcardSearches;

	@XmlElement(name = "trailing-wildcard-word-positions")
	private Boolean trailingWildcardWordPositions;

	@XmlElement(name = "fast-element-trailing-wildcard-searches")
	private Boolean fastElementTrailingWildcardSearches;

	@XmlElement(name = "two-character-searches")
	private Boolean twoCharacterSearches;

	@XmlElement(name = "one-character-searches")
	private Boolean oneCharacterSearches;

	@XmlElement(name = "uri-lexicon")
	private Boolean uriLexicon;

	@XmlElement(name = "collection-lexicon")
	private Boolean collectionLexicon;

	@XmlElement(name = "reindexer-enable")
	private Boolean reindexerEnable;

	@XmlElement(name = "reindexer-throttle")
	private Integer reindexerThrottle;

	@XmlElement(name = "reindexer-timestamp")
	private Long reindexerTimestamp;

	@XmlElement(name = "directory-creation")
	private String directoryCreation;

	@XmlElement(name = "maintain-last-modified")
	private Boolean maintainLastModified;

	@XmlElement(name = "maintain-directory-last-modified")
	private Boolean maintainDirectoryLastModified;

	@XmlElement(name = "inherit-permissions")
	private Boolean inheritPermissions;

	@XmlElement(name = "inherit-collections")
	private Boolean inheritCollections;

	@XmlElement(name = "inherit-quality")
	private Boolean inheritQuality;

	@XmlElement(name = "in-memory-limit")
	private Long inMemoryLimit;

	@XmlElement(name = "in-memory-list-size")
	private Long inMemoryListSize;

	@XmlElement(name = "in-memory-tree-size")
	private Long inMemoryTreeSize;

	@XmlElement(name = "in-memory-range-index-size")
	private Long inMemoryRangeIndexSize;

	@XmlElement(name = "in-memory-reverse-index-size")
	private Long inMemoryReverseIndexSize;

	@XmlElement(name = "in-memory-triple-index-size")
	private Long inMemoryTripleIndexSize;

	@XmlElement(name = "large-size-threshold")
	private Long largeSizeThreshold;

	@XmlElement(name = "locking")
	private String locking;

	@XmlElement(name = "journaling")
	private String journaling;

	@XmlElement(name = "journal-size")
	private Long journalSize;

	@XmlElement(name = "journal-count")
	private Integer journalCount;

	@XmlElement(name = "preallocate-journals")
	private Boolean preallocateJournals;

	@XmlElement(name = "preload-mapped-data")
	private Boolean preloadMappedData;

	@XmlElement(name = "preload-replica-mapped-data")
	private Boolean preloadReplicaMappedData;

	@XmlElement(name = "range-index-optimize")
	private String rangeIndexOptimize;

	@XmlElement(name = "positions-list-max-size")
	private Long positionsListMaxSize;

	@XmlElement(name = "format-compatibility")
	private String formatCompatibility;

	@XmlElement(name = "index-detection")
	private String indexDetection;

	@XmlElement(name = "expunge-locks")
	private String expungeLocks;

	@XmlElement(name = "tf-normalization")
	private String tfNormalization;

	@XmlElement(name = "merge-priority")
	private String mergePriority;

	@XmlElement(name = "merge-max-size")
	private Long mergeMaxSize;

	@XmlElement(name = "merge-min-size")
	private Long mergeMinSize;

	@XmlElement(name = "merge-min-ratio")
	private Integer mergeMinRatio;

	@XmlElement(name = "merge-timestamp")
	private Long mergeTimestamp;

	@XmlElement(name = "retain-until-backup")
	private Boolean retainUntilBackup;

	@XmlElementWrapper(name = "merge-blackouts")
	@XmlElement(name = "merge-blackout")
	private List<MergeBlackout> mergeBlackout;

	@XmlElementWrapper(name = "database-backups")
	@XmlElement(name = "database-backup")
	private List<DatabaseBackup> databaseBackup;

	@XmlElementWrapper(name = "fragment-roots")
	@XmlElement(name = "fragment-root")
	private List<FragmentRoot> fragmentRoot;

	@XmlElementWrapper(name = "fragment-parents")
	@XmlElement(name = "fragment-parent")
	private List<FragmentParent> fragmentParent;

	@XmlElementWrapper(name = "element-word-query-throughs")
	@XmlElement(name = "element-word-query-through")
	private List<Element> elementWordQueryThrough;

	@XmlElementWrapper(name = "phrase-throughs")
	@XmlElement(name = "phrase-through")
	private List<Element> phraseThrough;

	@XmlElementWrapper(name = "phrase-arounds")
	@XmlElement(name = "phrase-around")
	private List<Element> phraseAround;

	@XmlElementWrapper(name = "range-element-indexes")
	@XmlElement(name = "range-element-index")
	private List<ElementIndex> rangeElementIndex;

	@XmlElementWrapper(name = "range-element-attribute-indexes")
	@XmlElement(name = "range-element-attribute-index")
	private List<ElementAttributeIndex> rangeElementAttributeIndex;

	@XmlElementWrapper(name = "element-word-lexicons")
	@XmlElement(name = "element-word-lexicon")
	private List<ElementWordLexicon> elementWordLexicon;

	@XmlElementWrapper(name = "element-attribute-word-lexicons")
	@XmlElement(name = "element-attribute-word-lexicon")
	private List<ElementAttributeWordLexicon> elementAttributeWordLexicon;

	@XmlElementWrapper(name = "path-namespaces")
	@XmlElement(name = "path-namespace")
	private List<PathNamespace> pathNamespace;

	@XmlElementWrapper(name = "range-path-indexes")
	@XmlElement(name = "range-path-index")
	private List<PathIndex> rangePathIndex;

	@XmlElementWrapper(name = "fields")
	private List<Field> field;

	@XmlElementWrapper(name = "range-field-indexes")
	@XmlElement(name = "range-field-index")
	private List<FieldIndex> rangeFieldIndex;

	@XmlElementWrapper(name = "geospatial-element-indexes")
	@XmlElement(name = "geospatial-element-index")
	private List<GeospatialElementIndex> geospatialElementIndex;

	@XmlElementWrapper(name = "geospatial-element-child-indexes")
	@XmlElement(name = "geospatial-element-child-index")
	private List<GeospatialElementChildIndex> geospatialElementChildIndex;

	@XmlElementWrapper(name = "geospatial-element-pair-indexes")
	@XmlElement(name = "geospatial-element-pair-index")
	private List<GeospatialElementPairIndex> geospatialElementPairIndex;

	@XmlElementWrapper(name = "geospatial-element-attribute-pair-indexes")
	@XmlElement(name = "geospatial-element-attribute-pair-index")
	private List<GeospatialElementPairIndex> geospatialElementAttributePairIndex;

	@XmlElementWrapper(name = "geospatial-path-indexes")
	@XmlElement(name = "geospatial-path-index")
	private List<GeospatialPathIndex> geospatialPathIndex;

	@XmlElementWrapper(name = "geospatial-region-path-indexes")
	@XmlElement(name = "geospatial-region-path-index")
	private List<GeospatialRegionPathIndex> geospatialRegionPathIndex;

	@XmlElementWrapper(name = "default-rulesets")
	@XmlElement(name = "default-ruleset")
	private List<DefaultRuleset> defaultRuleset;

	@XmlElement(name = "database-replication")
	private DatabaseReplication databaseReplication;

	@XmlElementWrapper(name = "database-references")
	@XmlElement(name = "database-reference")
	private List<DatabaseReference> databaseReference;

	@XmlElement(name = "rebalancer-enable")
	private Boolean rebalancerEnable;

	@XmlElement(name = "rebalancer-throttle")
	private Integer rebalancerThrottle;

	@XmlElement(name = "assignment-policy")
	private AssignmentPolicy assignmentPolicy;

	public Database() {
		super();
	}

	public Database(API api, String databaseName) {
		super(api);
		this.databaseName = databaseName;
	}

	@Override
	protected String getResourceLabel() {
		return getDatabaseName();
	}

	@Override
	protected ResourceManager getResourceManager() {
		return new DatabaseManager(getClient());
	}

	@Override
	protected String getResourceId() {
		return databaseName;
	}

	public void addForest(Forest f) {
		addForest(f.getForestName());
	}

	public void addForest(String forestName) {
		if (forest == null) {
			forest = new ArrayList<>();
		}
		forest.add(forestName);
	}

	public void clear() {
		new DatabaseManager(getClient()).clearDatabase(databaseName);
	}

	public void attach(Forest f) {
		attach(f.getForestName());
	}

	/**
	 * TODO In the event this is a new forest with no host set, add a parameter to specify an index of the set of hosts
	 * returned by /manage/v2/hosts (I think the order is guaranteed).
	 *
	 * @param forestName
	 */
	public void attach(String forestName) {
		if (forest == null) {
			forest = new ArrayList<>();
		}
		forest.add(forestName);
		save();
	}

	public Forest attachNewForest(String forestName) {
		Forest f = getApi().forest(forestName);
		f.save();
		attach(f);
		return f;
	}

	public void detach(Forest f) {
		detach(f.getForestName());
	}

	public void detach(String forestName) {
		if (forest == null || !forest.contains(forestName)) {
			getLogger().warn(format("Forest %s not in list of known forests for database, so not detaching"));
		} else {
			forest.remove(forestName);
			save();
		}
	}

	@JsonIgnore
	public List<String> getDatabaseDependencyNames() {
		List<String> list = new ArrayList<>();
		if (StringUtils.hasText(schemaDatabase)) {
			list.add(schemaDatabase);
		}
		if (StringUtils.hasText(triggersDatabase)) {
			list.add(triggersDatabase);
		}
		if (StringUtils.hasText(securityDatabase)) {
			list.add(securityDatabase);
		}
		return list;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public List<String> getForest() {
		return forest;
	}

	public void setForest(List<String> forest) {
		this.forest = forest;
	}

	public List<ElementIndex> getRangeElementIndex() {
		return rangeElementIndex;
	}

	public void setRangeElementIndex(List<ElementIndex> elementIndexes) {
		this.rangeElementIndex = elementIndexes;
	}

	public String getSecurityDatabase() {
		return securityDatabase;
	}

	public void setSecurityDatabase(String securityDatabase) {
		this.securityDatabase = securityDatabase;
	}

	public String getSchemaDatabase() {
		return schemaDatabase;
	}

	public void setSchemaDatabase(String schemaDatabase) {
		this.schemaDatabase = schemaDatabase;
	}

	public String getTriggersDatabase() {
		return triggersDatabase;
	}

	public void setTriggersDatabase(String triggersDatabase) {
		this.triggersDatabase = triggersDatabase;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getRetiredForestCount() {
		return retiredForestCount;
	}

	public void setRetiredForestCount(Integer retiredForestCount) {
		this.retiredForestCount = retiredForestCount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStemmedSearches() {
		return stemmedSearches;
	}

	public void setStemmedSearches(String stemmedSearches) {
		this.stemmedSearches = stemmedSearches;
	}

	public Boolean getWordSearches() {
		return wordSearches;
	}

	public void setWordSearches(Boolean wordSearches) {
		this.wordSearches = wordSearches;
	}

	public Boolean getWordPositions() {
		return wordPositions;
	}

	public void setWordPositions(Boolean wordPositions) {
		this.wordPositions = wordPositions;
	}

	public Boolean getFastPhraseSearches() {
		return fastPhraseSearches;
	}

	public void setFastPhraseSearches(Boolean fastPhraseSearches) {
		this.fastPhraseSearches = fastPhraseSearches;
	}

	public Boolean getFastReverseSearches() {
		return fastReverseSearches;
	}

	public void setFastReverseSearches(Boolean fastReverseSearches) {
		this.fastReverseSearches = fastReverseSearches;
	}

	public Boolean getTripleIndex() {
		return tripleIndex;
	}

	public void setTripleIndex(Boolean tripleIndex) {
		this.tripleIndex = tripleIndex;
	}

	public Boolean getTriplePositions() {
		return triplePositions;
	}

	public void setTriplePositions(Boolean triplePositions) {
		this.triplePositions = triplePositions;
	}

	public Boolean getFastCaseSensitiveSearches() {
		return fastCaseSensitiveSearches;
	}

	public void setFastCaseSensitiveSearches(Boolean fastCaseSensitiveSearches) {
		this.fastCaseSensitiveSearches = fastCaseSensitiveSearches;
	}

	public Boolean getFastDiacriticSensitiveSearches() {
		return fastDiacriticSensitiveSearches;
	}

	public void setFastDiacriticSensitiveSearches(Boolean fastDiacriticSensitiveSearches) {
		this.fastDiacriticSensitiveSearches = fastDiacriticSensitiveSearches;
	}

	public Boolean getFastElementWordSearches() {
		return fastElementWordSearches;
	}

	public void setFastElementWordSearches(Boolean fastElementWordSearches) {
		this.fastElementWordSearches = fastElementWordSearches;
	}

	public Boolean getElementWordPositions() {
		return elementWordPositions;
	}

	public void setElementWordPositions(Boolean elementWordPositions) {
		this.elementWordPositions = elementWordPositions;
	}

	public Boolean getFastElementPhraseSearches() {
		return fastElementPhraseSearches;
	}

	public void setFastElementPhraseSearches(Boolean fastElementPhraseSearches) {
		this.fastElementPhraseSearches = fastElementPhraseSearches;
	}

	public Boolean getElementValuePositions() {
		return elementValuePositions;
	}

	public void setElementValuePositions(Boolean elementValuePositions) {
		this.elementValuePositions = elementValuePositions;
	}

	public Boolean getAttributeValuePositions() {
		return attributeValuePositions;
	}

	public void setAttributeValuePositions(Boolean attributeValuePositions) {
		this.attributeValuePositions = attributeValuePositions;
	}

	public Boolean getFieldValueSearches() {
		return fieldValueSearches;
	}

	public void setFieldValueSearches(Boolean fieldValueSearches) {
		this.fieldValueSearches = fieldValueSearches;
	}

	public Boolean getFieldValuePositions() {
		return fieldValuePositions;
	}

	public void setFieldValuePositions(Boolean fieldValuePositions) {
		this.fieldValuePositions = fieldValuePositions;
	}

	public Boolean getThreeCharacterSearches() {
		return threeCharacterSearches;
	}

	public void setThreeCharacterSearches(Boolean threeCharacterSearches) {
		this.threeCharacterSearches = threeCharacterSearches;
	}

	public Boolean getThreeCharacterWordPositions() {
		return threeCharacterWordPositions;
	}

	public void setThreeCharacterWordPositions(Boolean threeCharacterWordPositions) {
		this.threeCharacterWordPositions = threeCharacterWordPositions;
	}

	public Boolean getFastElementCharacterSearches() {
		return fastElementCharacterSearches;
	}

	public void setFastElementCharacterSearches(Boolean fastElementCharacterSearches) {
		this.fastElementCharacterSearches = fastElementCharacterSearches;
	}

	public Boolean getTrailingWildcardSearches() {
		return trailingWildcardSearches;
	}

	public void setTrailingWildcardSearches(Boolean trailingWildcardSearches) {
		this.trailingWildcardSearches = trailingWildcardSearches;
	}

	public Boolean getTrailingWildcardWordPositions() {
		return trailingWildcardWordPositions;
	}

	public void setTrailingWildcardWordPositions(Boolean trailingWildcardWordPositions) {
		this.trailingWildcardWordPositions = trailingWildcardWordPositions;
	}

	public Boolean getFastElementTrailingWildcardSearches() {
		return fastElementTrailingWildcardSearches;
	}

	public void setFastElementTrailingWildcardSearches(Boolean fastElementTrailingWildcardSearches) {
		this.fastElementTrailingWildcardSearches = fastElementTrailingWildcardSearches;
	}

	public Boolean getTwoCharacterSearches() {
		return twoCharacterSearches;
	}

	public void setTwoCharacterSearches(Boolean twoCharacterSearches) {
		this.twoCharacterSearches = twoCharacterSearches;
	}

	public Boolean getOneCharacterSearches() {
		return oneCharacterSearches;
	}

	public void setOneCharacterSearches(Boolean oneCharacterSearches) {
		this.oneCharacterSearches = oneCharacterSearches;
	}

	public Boolean getUriLexicon() {
		return uriLexicon;
	}

	public void setUriLexicon(Boolean uriLexicon) {
		this.uriLexicon = uriLexicon;
	}

	public Boolean getCollectionLexicon() {
		return collectionLexicon;
	}

	public void setCollectionLexicon(Boolean collectionLexicon) {
		this.collectionLexicon = collectionLexicon;
	}

	public Boolean getReindexerEnable() {
		return reindexerEnable;
	}

	public void setReindexerEnable(Boolean reindexerEnable) {
		this.reindexerEnable = reindexerEnable;
	}

	public Integer getReindexerThrottle() {
		return reindexerThrottle;
	}

	public void setReindexerThrottle(Integer reindexerThrottle) {
		this.reindexerThrottle = reindexerThrottle;
	}

	public Long getReindexerTimestamp() {
		return reindexerTimestamp;
	}

	public void setReindexerTimestamp(Long reindexerTimestamp) {
		this.reindexerTimestamp = reindexerTimestamp;
	}

	public String getDirectoryCreation() {
		return directoryCreation;
	}

	public void setDirectoryCreation(String directoryCreation) {
		this.directoryCreation = directoryCreation;
	}

	public Boolean getMaintainLastModified() {
		return maintainLastModified;
	}

	public void setMaintainLastModified(Boolean maintainLastModified) {
		this.maintainLastModified = maintainLastModified;
	}

	public Boolean getMaintainDirectoryLastModified() {
		return maintainDirectoryLastModified;
	}

	public void setMaintainDirectoryLastModified(Boolean maintainDirectoryLastModified) {
		this.maintainDirectoryLastModified = maintainDirectoryLastModified;
	}

	public Boolean getInheritPermissions() {
		return inheritPermissions;
	}

	public void setInheritPermissions(Boolean inheritPermissions) {
		this.inheritPermissions = inheritPermissions;
	}

	public Boolean getInheritCollections() {
		return inheritCollections;
	}

	public void setInheritCollections(Boolean inheritCollections) {
		this.inheritCollections = inheritCollections;
	}

	public Boolean getInheritQuality() {
		return inheritQuality;
	}

	public void setInheritQuality(Boolean inheritQuality) {
		this.inheritQuality = inheritQuality;
	}

	public Long getInMemoryLimit() {
		return inMemoryLimit;
	}

	public void setInMemoryLimit(Long inMemoryLimit) {
		this.inMemoryLimit = inMemoryLimit;
	}

	public Long getInMemoryListSize() {
		return inMemoryListSize;
	}

	public void setInMemoryListSize(Long inMemoryListSize) {
		this.inMemoryListSize = inMemoryListSize;
	}

	public Long getInMemoryTreeSize() {
		return inMemoryTreeSize;
	}

	public void setInMemoryTreeSize(Long inMemoryTreeSize) {
		this.inMemoryTreeSize = inMemoryTreeSize;
	}

	public Long getInMemoryRangeIndexSize() {
		return inMemoryRangeIndexSize;
	}

	public void setInMemoryRangeIndexSize(Long inMemoryRangeIndexSize) {
		this.inMemoryRangeIndexSize = inMemoryRangeIndexSize;
	}

	public Long getInMemoryReverseIndexSize() {
		return inMemoryReverseIndexSize;
	}

	public void setInMemoryReverseIndexSize(Long inMemoryReverseIndexSize) {
		this.inMemoryReverseIndexSize = inMemoryReverseIndexSize;
	}

	public Long getInMemoryTripleIndexSize() {
		return inMemoryTripleIndexSize;
	}

	public void setInMemoryTripleIndexSize(Long inMemoryTripleIndexSize) {
		this.inMemoryTripleIndexSize = inMemoryTripleIndexSize;
	}

	public Long getLargeSizeThreshold() {
		return largeSizeThreshold;
	}

	public void setLargeSizeThreshold(Long largeSizeThreshold) {
		this.largeSizeThreshold = largeSizeThreshold;
	}

	public String getLocking() {
		return locking;
	}

	public void setLocking(String locking) {
		this.locking = locking;
	}

	public String getJournaling() {
		return journaling;
	}

	public void setJournaling(String journaling) {
		this.journaling = journaling;
	}

	public Long getJournalSize() {
		return journalSize;
	}

	public void setJournalSize(Long journalSize) {
		this.journalSize = journalSize;
	}

	public Integer getJournalCount() {
		return journalCount;
	}

	public void setJournalCount(Integer journalCount) {
		this.journalCount = journalCount;
	}

	public Boolean getPreallocateJournals() {
		return preallocateJournals;
	}

	public void setPreallocateJournals(Boolean preallocateJournals) {
		this.preallocateJournals = preallocateJournals;
	}

	public Boolean getPreloadMappedData() {
		return preloadMappedData;
	}

	public void setPreloadMappedData(Boolean preloadMappedData) {
		this.preloadMappedData = preloadMappedData;
	}

	public Boolean getPreloadReplicaMappedData() {
		return preloadReplicaMappedData;
	}

	public void setPreloadReplicaMappedData(Boolean preloadReplicaMappedData) {
		this.preloadReplicaMappedData = preloadReplicaMappedData;
	}

	public String getRangeIndexOptimize() {
		return rangeIndexOptimize;
	}

	public void setRangeIndexOptimize(String rangeIndexOptimize) {
		this.rangeIndexOptimize = rangeIndexOptimize;
	}

	public Long getPositionsListMaxSize() {
		return positionsListMaxSize;
	}

	public void setPositionsListMaxSize(Long positionsListMaxSize) {
		this.positionsListMaxSize = positionsListMaxSize;
	}

	public String getFormatCompatibility() {
		return formatCompatibility;
	}

	public void setFormatCompatibility(String formatCompatibility) {
		this.formatCompatibility = formatCompatibility;
	}

	public String getIndexDetection() {
		return indexDetection;
	}

	public void setIndexDetection(String indexDetection) {
		this.indexDetection = indexDetection;
	}

	public String getExpungeLocks() {
		return expungeLocks;
	}

	public void setExpungeLocks(String expungeLocks) {
		this.expungeLocks = expungeLocks;
	}

	public String getTfNormalization() {
		return tfNormalization;
	}

	public void setTfNormalization(String tfNormalization) {
		this.tfNormalization = tfNormalization;
	}

	public String getMergePriority() {
		return mergePriority;
	}

	public void setMergePriority(String mergePriority) {
		this.mergePriority = mergePriority;
	}

	public Long getMergeMaxSize() {
		return mergeMaxSize;
	}

	public void setMergeMaxSize(Long mergeMaxSize) {
		this.mergeMaxSize = mergeMaxSize;
	}

	public Long getMergeMinSize() {
		return mergeMinSize;
	}

	public void setMergeMinSize(Long mergeMinSize) {
		this.mergeMinSize = mergeMinSize;
	}

	public Integer getMergeMinRatio() {
		return mergeMinRatio;
	}

	public void setMergeMinRatio(Integer mergeMinRatio) {
		this.mergeMinRatio = mergeMinRatio;
	}

	public Long getMergeTimestamp() {
		return mergeTimestamp;
	}

	public void setMergeTimestamp(Long mergeTimestamp) {
		this.mergeTimestamp = mergeTimestamp;
	}

	public Boolean getRetainUntilBackup() {
		return retainUntilBackup;
	}

	public void setRetainUntilBackup(Boolean retainUntilBackup) {
		this.retainUntilBackup = retainUntilBackup;
	}

	public List<Element> getElementWordQueryThrough() {
		return elementWordQueryThrough;
	}

	public void setElementWordQueryThrough(List<Element> elementWordQueryThrough) {
		this.elementWordQueryThrough = elementWordQueryThrough;
	}

	public List<Element> getPhraseThrough() {
		return phraseThrough;
	}

	public void setPhraseThrough(List<Element> phraseThrough) {
		this.phraseThrough = phraseThrough;
	}

	public List<Element> getPhraseAround() {
		return phraseAround;
	}

	public void setPhraseAround(List<Element> phraseAround) {
		this.phraseAround = phraseAround;
	}

	public List<Field> getField() {
		return field;
	}

	public void setField(List<Field> field) {
		this.field = field;
	}

	public Boolean getRebalancerEnable() {
		return rebalancerEnable;
	}

	public void setRebalancerEnable(Boolean rebalancerEnable) {
		this.rebalancerEnable = rebalancerEnable;
	}

	public Integer getRebalancerThrottle() {
		return rebalancerThrottle;
	}

	public void setRebalancerThrottle(Integer rebalancerThrottle) {
		this.rebalancerThrottle = rebalancerThrottle;
	}

	public AssignmentPolicy getAssignmentPolicy() {
		return assignmentPolicy;
	}

	public void setAssignmentPolicy(AssignmentPolicy assignmentPolicy) {
		this.assignmentPolicy = assignmentPolicy;
	}

	public List<FieldIndex> getRangeFieldIndex() {
		return rangeFieldIndex;
	}

	public void setRangeFieldIndex(List<FieldIndex> rangeFieldIndex) {
		this.rangeFieldIndex = rangeFieldIndex;
	}

	public List<ElementAttributeIndex> getRangeElementAttributeIndex() {
		return rangeElementAttributeIndex;
	}

	public void setRangeElementAttributeIndex(List<ElementAttributeIndex> rangeElementAttributeIndex) {
		this.rangeElementAttributeIndex = rangeElementAttributeIndex;
	}

	public List<ElementWordLexicon> getElementWordLexicon() {
		return elementWordLexicon;
	}

	public void setElementWordLexicon(List<ElementWordLexicon> elementWordLexicon) {
		this.elementWordLexicon = elementWordLexicon;
	}

	public List<ElementAttributeWordLexicon> getElementAttributeWordLexicon() {
		return elementAttributeWordLexicon;
	}

	public void setElementAttributeWordLexicon(List<ElementAttributeWordLexicon> elementAttributeWordLexicon) {
		this.elementAttributeWordLexicon = elementAttributeWordLexicon;
	}

	public List<PathNamespace> getPathNamespace() {
		return pathNamespace;
	}

	public void setPathNamespace(List<PathNamespace> pathNamespace) {
		this.pathNamespace = pathNamespace;
	}

	public List<GeospatialElementIndex> getGeospatialElementIndex() {
		return geospatialElementIndex;
	}

	public void setGeospatialElementIndex(List<GeospatialElementIndex> geospatialElementIndex) {
		this.geospatialElementIndex = geospatialElementIndex;
	}

	public List<GeospatialElementChildIndex> getGeospatialElementChildIndex() {
		return geospatialElementChildIndex;
	}

	public void setGeospatialElementChildIndex(List<GeospatialElementChildIndex> geospatialElementChildIndex) {
		this.geospatialElementChildIndex = geospatialElementChildIndex;
	}

	public List<GeospatialElementPairIndex> getGeospatialElementPairIndex() {
		return geospatialElementPairIndex;
	}

	public void setGeospatialElementPairIndex(List<GeospatialElementPairIndex> geospatialElementPairIndex) {
		this.geospatialElementPairIndex = geospatialElementPairIndex;
	}

	public List<GeospatialElementPairIndex> getGeospatialElementAttributePairIndex() {
		return geospatialElementAttributePairIndex;
	}

	public void setGeospatialElementAttributePairIndex(List<GeospatialElementPairIndex> geospatialElementAttributePairIndex) {
		this.geospatialElementAttributePairIndex = geospatialElementAttributePairIndex;
	}

	public List<GeospatialPathIndex> getGeospatialPathIndex() {
		return geospatialPathIndex;
	}

	public void setGeospatialPathIndex(List<GeospatialPathIndex> geospatialPathIndex) {
		this.geospatialPathIndex = geospatialPathIndex;
	}

	public List<GeospatialRegionPathIndex> getGeospatialRegionPathIndex() {
		return geospatialRegionPathIndex;
	}

	public void setGeospatialRegionPathIndex(List<GeospatialRegionPathIndex> geospatialRegionPathIndex) {
		this.geospatialRegionPathIndex = geospatialRegionPathIndex;
	}

	public List<DefaultRuleset> getDefaultRuleset() {
		return defaultRuleset;
	}

	public void setDefaultRuleset(List<DefaultRuleset> defaultRuleset) {
		this.defaultRuleset = defaultRuleset;
	}

	public DatabaseReplication getDatabaseReplication() {
		return databaseReplication;
	}

	public void setDatabaseReplication(DatabaseReplication databaseReplication) {
		this.databaseReplication = databaseReplication;
	}

	public List<DatabaseReference> getDatabaseReference() {
		return databaseReference;
	}

	public void setDatabaseReference(List<DatabaseReference> databaseReference) {
		this.databaseReference = databaseReference;
	}

	public List<DatabaseBackup> getDatabaseBackup() {
		return databaseBackup;
	}

	public void setDatabaseBackup(List<DatabaseBackup> databaseBackup) {
		this.databaseBackup = databaseBackup;
	}

	public List<MergeBlackout> getMergeBlackout() {
		return mergeBlackout;
	}

	public void setMergeBlackout(List<MergeBlackout> mergeBlackout) {
		this.mergeBlackout = mergeBlackout;
	}

	public List<FragmentRoot> getFragmentRoot() {
		return fragmentRoot;
	}

	public void setFragmentRoot(List<FragmentRoot> fragmentRoot) {
		this.fragmentRoot = fragmentRoot;
	}

	public List<FragmentParent> getFragmentParent() {
		return fragmentParent;
	}

	public void setFragmentParent(List<FragmentParent> fragmentParent) {
		this.fragmentParent = fragmentParent;
	}

	public List<PathIndex> getRangePathIndex() {
		return rangePathIndex;
	}

	public void setRangePathIndex(List<PathIndex> rangePathIndex) {
		this.rangePathIndex = rangePathIndex;
	}
}
