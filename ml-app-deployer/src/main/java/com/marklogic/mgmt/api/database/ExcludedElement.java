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

@XmlAccessorType(XmlAccessType.FIELD)
public class ExcludedElement extends Element {

	@XmlElement(name = "attribute-namespace-uri")
	private String attributeNamespaceUri;

	@XmlElement(name = "attribute-localname")
	private String attributeLocalname;

	@XmlElement(name = "attribute-value")
	private String attributeValue;

	public String getAttributeNamespaceUri() {
		return attributeNamespaceUri;
	}

	public void setAttributeNamespaceUri(String attributeNamespaceUri) {
		this.attributeNamespaceUri = attributeNamespaceUri;
	}

	public String getAttributeLocalname() {
		return attributeLocalname;
	}

	public void setAttributeLocalname(String attributeLocalname) {
		this.attributeLocalname = attributeLocalname;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
}
