/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.database;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TokenizerOverride {

	private String character;

	@XmlElement(name = "tokenizer-class")
	private String tokenizerClass;

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public String getTokenizerClass() {
		return tokenizerClass;
	}

	public void setTokenizerClass(String tokenizerClass) {
		this.tokenizerClass = tokenizerClass;
	}

}
