package com.marklogic.mgmt.api.database;

import java.util.ArrayList;
import java.util.List;

public class Field {

    private String fieldName;
    private List<FieldPath> fieldPath;
    private Boolean includeRoot;
    private List<String> wordLexicon;
    private List<IncludedElement> includedElement;
    private List<ExcludedElement> excludedElement;

    private Boolean stemmedSearches;
    private Boolean wordSearches;
    private Boolean fieldValueSearches;
    private Boolean fieldValuePositions;
    private Boolean fastPhraseSearches;
    private Boolean fastCaseSensitiveSearches;
    private Boolean fastDiacriticSensitiveSearches;
    private Boolean trailingWildcardSearches;
    private Boolean trailingWildcardWordPositions;
    private Boolean threeCharacterSearches;
    private Boolean threeCharacterWordPositions;
    private Boolean twoCharacterSearches;
    private Boolean oneCharacterSearches;

    public void addFieldPath(FieldPath path) {
        if (fieldPath == null) {
            fieldPath = new ArrayList<>();
        }
        fieldPath.add(path);
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Boolean getIncludeRoot() {
        return includeRoot;
    }

    public void setIncludeRoot(Boolean includeRoot) {
        this.includeRoot = includeRoot;
    }

    public List<ExcludedElement> getExcludedElement() {
        return excludedElement;
    }

    public void setExcludedElement(List<ExcludedElement> excludedElement) {
        this.excludedElement = excludedElement;
    }

    public List<FieldPath> getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(List<FieldPath> fieldPath) {
        this.fieldPath = fieldPath;
    }

    public List<String> getWordLexicon() {
        return wordLexicon;
    }

    public void setWordLexicon(List<String> wordLexicon) {
        this.wordLexicon = wordLexicon;
    }

    public List<IncludedElement> getIncludedElement() {
        return includedElement;
    }

    public void setIncludedElement(List<IncludedElement> includedElement) {
        this.includedElement = includedElement;
    }

    public Boolean getStemmedSearches() {
        return stemmedSearches;
    }

    public void setStemmedSearches(Boolean stemmedSearches) {
        this.stemmedSearches = stemmedSearches;
    }

    public Boolean getWordSearches() {
        return wordSearches;
    }

    public void setWordSearches(Boolean wordSearches) {
        this.wordSearches = wordSearches;
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

    public Boolean getFastPhraseSearches() {
        return fastPhraseSearches;
    }

    public void setFastPhraseSearches(Boolean fastPhraseSearches) {
        this.fastPhraseSearches = fastPhraseSearches;
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
}
