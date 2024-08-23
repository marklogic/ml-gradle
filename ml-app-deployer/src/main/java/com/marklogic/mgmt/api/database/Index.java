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
public abstract class Index {

	@XmlElement(name = "scalar-type")
	private String scalarType;

	private String collation;

	@XmlElement(name = "range-value-positions")
	private Boolean rangeValuePositions;

	@XmlElement(name = "invalid-values")
	private String invalidValues;

	public String getScalarType() {
		return scalarType;
	}

	public void setScalarType(String scalarType) {
		this.scalarType = scalarType;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public Boolean getRangeValuePositions() {
		return rangeValuePositions;
	}

	public void setRangeValuePositions(Boolean rangeValuePositions) {
		this.rangeValuePositions = rangeValuePositions;
	}

	public String getInvalidValues() {
		return invalidValues;
	}

	public void setInvalidValues(String invalidValues) {
		this.invalidValues = invalidValues;
	}
}
