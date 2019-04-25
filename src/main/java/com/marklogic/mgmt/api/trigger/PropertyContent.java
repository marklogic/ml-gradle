package com.marklogic.mgmt.api.trigger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyContent {

	@XmlElement(name = "property-name")
	private PropertyName propertyName;

	public PropertyName getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(PropertyName propertyName) {
		this.propertyName = propertyName;
	}
}
