package com.rjrudin.marklogic.mgmt.api.database;

import java.util.ArrayList;
import java.util.List;

import com.rjrudin.marklogic.mgmt.ResourceManager;
import com.rjrudin.marklogic.mgmt.api.API;
import com.rjrudin.marklogic.mgmt.api.Resource;
import com.rjrudin.marklogic.mgmt.api.forest.Forest;
import com.rjrudin.marklogic.mgmt.databases.DatabaseManager;

public class Database extends Resource {

    private String databaseName;
    private List<String> forest;
    private String securityDatabase;
    private String schemaDatabase;
    private String triggersDatabase;
    private Boolean enabled;
    private Integer retiredForestCount;
    private String language;
    private String stemmedSearches;
    private Boolean wordSearches;
    private Boolean wordPositions;
    private Boolean fastPhraseSearches;
    private Boolean fastReverseSearches;
    private Boolean tripleIndex;
    private Boolean triplePositions;
    private Boolean fastCaseSensitiveSearches;
    private Boolean fastDiacriticSensitiveSearches;
    private Boolean fastElementWordSearches;
    private Boolean elementWordPositions;
    private Boolean fastElementPhraseSearches;
    private Boolean elementValuePositions;
    private Boolean attributeValuePositions;
    private Boolean fieldValueSearches;
    private Boolean fieldValuePositions;
    private Boolean threeCharacterSearches;
    private Boolean threeCharacterWordPositions;
    private Boolean fastElementCharacterSearches;
    private Boolean trailingWildcardSearches;
    private Boolean trailingWildcardWordPositions;
    private Boolean fastElementTrailingWildcardSearches;
    private Boolean twoCharacterSearches;
    private Boolean oneCharacterSearches;
    private Boolean uriLexicon;
    private Boolean collectionLexicon;
    private Boolean reindexerEnable;
    private Integer reindexerThrottle;
    private Long reindexerTimestamp;
    private String directoryCreation;
    private Boolean maintainLastModified;
    private Boolean maintainDirectoryLastModified;
    private Boolean inheritPermissions;
    private Boolean inheritCollections;
    private Boolean inheritQuality;
    private Long inMemoryLimit;
    private Long inMemoryListSize;
    private Long inMemoryTreeSize;
    private Long inMemoryRangeIndexSize;
    private Long inMemoryReverseIndexSize;
    private Long inMemoryTripleIndexSize;
    private Long largeSizeThreshold;
    private String locking;
    private String journaling;
    private Long journalSize;
    private Integer journalCount;
    private Boolean preallocateJournals;
    private Boolean preloadMappedData;
    private Boolean preloadReplicaMappedData;
    private String rangeIndexOptimize;
    private Long positionsListMaxSize;
    private String formatCompatibility;
    private String indexDetection;
    private String expungeLocks;
    private String tfNormalization;
    private String mergePriority;
    private Long mergeMaxSize;
    private Long mergeMinSize;
    private Integer mergeMinRatio;
    private Long mergeTimestamp;
    private Boolean retainUntilBackup;
    private List<Element> elementWordQueryThrough;
    private List<Element> phraseThrough;
    private List<Element> phraseAround;
    private List<ElementIndex> rangeElementIndex;
    private List<Field> field;
    private Boolean rebalancerEnable;
    private Integer rebalancerThrottle;
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
     * @param f
     */
    public void attach(String forestName) {
        if (forest == null) {
            forest = new ArrayList<>();
        }
        forest.add(forestName);
        save();
    }

    public Forest attachNewForest(String forestName) {
        Forest f = getApi().newForest(forestName);
        f.save();
        attach(f);
        return f;
    }

    public void detach(Forest f) {
        detach(f.getForestName());
    }

    public void detach(String forestName) {
        if (forest == null || !forest.contains(forestName)) {
            logger.warn(format("Forest %s not in list of known forests for database, so not detaching"));
        } else {
            forest.remove(forestName);
            save();
        }
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
}
