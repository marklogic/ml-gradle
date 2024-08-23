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
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Element {

	@XmlElement(name = "namespace-uri")
	private String namespaceUri;

	/**
	 * As of ML 10.0-2, this won't work for both JSON and XML. When multiple values exist, the Manage API expects the
	 * JSON representation to be an array of strings, while the XML representation always expects a single value.
	 */
	@XmlElementWrapper(name = "localname")
	private List<String> localname;

	public String getNamespaceUri() {
		return namespaceUri;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}

	public List<String> getLocalname() {
		return localname;
	}

	public void setLocalname(List<String> localname) {
		this.localname = localname;
	}

}
