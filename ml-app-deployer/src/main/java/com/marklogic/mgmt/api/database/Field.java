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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Field {

	@XmlElement(name = "field-name")
	private String fieldName;

	@XmlElementWrapper(name = "field-paths")
	@XmlElement(name = "field-path")
	private List<FieldPath> fieldPath;

	@XmlElement(name = "include-root")
	private Boolean includeRoot;

	@XmlElementWrapper(name = "word-lexicons")
	@XmlElement(name = "word-lexicon")
	private List<String> wordLexicon;

	@XmlElementWrapper(name = "included-elements")
	@XmlElement(name = "included-element")
	private List<IncludedElement> includedElement;

	@XmlElementWrapper(name = "excluded-elements")
	@XmlElement(name = "excluded-element")
	private List<ExcludedElement> excludedElement;

	private String metadata;

	@XmlElement(name = "stemmed-searches")
	private String stemmedSearches;

	@XmlElement(name = "word-searches")
	private Boolean wordSearches;

	@XmlElement(name = "field-value-searches")
	private Boolean fieldValueSearches;

	@XmlElement(name = "field-value-positions")
	private Boolean fieldValuePositions;

	@XmlElement(name = "fast-phrase-searches")
	private Boolean fastPhraseSearches;

	@XmlElement(name = "fast-case-sensitive-searches")
	private Boolean fastCaseSensitiveSearches;

	@XmlElement(name = "fast-diacritic-sensitive-searches")
	private Boolean fastDiacriticSensitiveSearches;

	@XmlElement(name = "trailing-wildcard-searches")
	private Boolean trailingWildcardSearches;

	@XmlElement(name = "trailing-wildcard-word-positions")
	private Boolean trailingWildcardWordPositions;

	@XmlElement(name = "three-character-searches")
	private Boolean threeCharacterSearches;

	@XmlElement(name = "three-character-word-positions")
	private Boolean threeCharacterWordPositions;

	@XmlElement(name = "two-character-searches")
	private Boolean twoCharacterSearches;

	@XmlElement(name = "one-character-searches")
	private Boolean oneCharacterSearches;

	@XmlElementWrapper(name = "tokenizer-overrides")
	@XmlElement(name = "tokenizer-override")
	private List<TokenizerOverride> tokenizerOverride;

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

	public List<TokenizerOverride> getTokenizerOverride() {
		return tokenizerOverride;
	}

	public void setTokenizerOverride(List<TokenizerOverride> tokenizerOverride) {
		this.tokenizerOverride = tokenizerOverride;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
