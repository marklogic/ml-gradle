package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class GeospatialIndex {

	@XmlElement(name = "coordinate-system")
	private String coordinateSystem;

	@XmlElement(name = "range-value-positions")
	private Boolean rangeValuePositions;

	@XmlElement(name = "invalid-values")
	private String invalidValues;

	public String getCoordinateSystem() {
		return coordinateSystem;
	}

	public void setCoordinateSystem(String coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
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
