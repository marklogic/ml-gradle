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

/**
 * This can be used for both element pairs and element attribute pairs.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GeospatialElementPairIndex extends GeospatialIndex {

	@XmlElement(name = "parent-namespace-uri")
	private String parentNamespaceUri;

	@XmlElement(name = "parent-localname")
	private String parentLocalname;

	@XmlElement(name = "latitude-namespace-uri")
	private String latitudeNamespaceUri;

	@XmlElement(name = "latitude-localname")
	private String latitudeLocalname;

	@XmlElement(name = "longitude-namespace-uri")
	private String longitudeNamespaceUri;

	@XmlElement(name = "longitude-localname")
	private String longitudeLocalname;

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

	public String getLatitudeNamespaceUri() {
		return latitudeNamespaceUri;
	}

	public void setLatitudeNamespaceUri(String latitudeNamespaceUri) {
		this.latitudeNamespaceUri = latitudeNamespaceUri;
	}

	public String getLatitudeLocalname() {
		return latitudeLocalname;
	}

	public void setLatitudeLocalname(String latitudeLocalname) {
		this.latitudeLocalname = latitudeLocalname;
	}

	public String getLongitudeNamespaceUri() {
		return longitudeNamespaceUri;
	}

	public void setLongitudeNamespaceUri(String longitudeNamespaceUri) {
		this.longitudeNamespaceUri = longitudeNamespaceUri;
	}

	public String getLongitudeLocalname() {
		return longitudeLocalname;
	}

	public void setLongitudeLocalname(String longitudeLocalname) {
		this.longitudeLocalname = longitudeLocalname;
	}
}
