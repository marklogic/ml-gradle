package com.marklogic.mgmt.api.database;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class GeospatialRegionPathIndex {

	@XmlElement(name = "path-expression")
	private String pathExpression;

	@XmlElement(name = "coordinate-system")
	private String coordinateSystem;

	@XmlElement(name = "units")
	private String units;

	@XmlElement(name = "geohash-precision")
	private Integer geohashPrecision;

	@XmlElement(name = "invalid-values")
	private String invalidValues;

	public String getPathExpression() {
		return pathExpression;
	}

	public void setPathExpression(String pathExpression) {
		this.pathExpression = pathExpression;
	}

	public String getCoordinateSystem() {
		return coordinateSystem;
	}

	public void setCoordinateSystem(String coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public Integer getGeohashPrecision() {
		return geohashPrecision;
	}

	public void setGeohashPrecision(Integer geohashPrecision) {
		this.geohashPrecision = geohashPrecision;
	}

	public String getInvalidValues() {
		return invalidValues;
	}

	public void setInvalidValues(String invalidValues) {
		this.invalidValues = invalidValues;
	}
}
