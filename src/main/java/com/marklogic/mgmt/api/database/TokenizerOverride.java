package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
