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
public class ElementAttributeIndex extends ElementIndex {

	@XmlElement(name = "parent-namespace-uri")
	private String parentNamespaceUri;

	@XmlElement(name = "parent-localname")
	private String parentLocalname;

	public String getParentNamespaceUri() {
		return parentNamespaceUri;
	}

	public void setParentNamespaceUri(String parentNamespaceUri) {
		this.parentNamespaceUri = parentNamespaceUri;
	}

	public String getParentLocalname() {
		return parentLocalname;
	}

	public void setParentLocalname(String parentLocalname) {
		this.parentLocalname = parentLocalname;
	}
}
