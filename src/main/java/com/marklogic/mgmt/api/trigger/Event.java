package com.marklogic.mgmt.api.trigger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

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
