/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.mgmt.api.trigger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Event {

	@XmlElement(name = "data-event")
	private DataEvent dataEvent;

	@XmlElement(name = "database-online-event")
	private DatabaseOnlineEvent databaseOnlineEvent;

	public DataEvent getDataEvent() {
		return dataEvent;
	}

	public void setDataEvent(DataEvent dataEvent) {
		this.dataEvent = dataEvent;
	}

	public DatabaseOnlineEvent getDatabaseOnlineEvent() {
		return databaseOnlineEvent;
	}

	public void setDatabaseOnlineEvent(DatabaseOnlineEvent databaseOnlineEvent) {
		this.databaseOnlineEvent = databaseOnlineEvent;
	}
}
