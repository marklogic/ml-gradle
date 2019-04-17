package com.marklogic.mgmt.api.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class RequestBlackout {

	@XmlElementWrapper(name = "users")
	@XmlElement(name = "user")
	private List<String> user;

	@XmlElementWrapper(name = "roles")
	@XmlElement(name = "role")
	private List<String> role;

	@XmlElement(name = "blackout-type")
	private String blackoutType;

	@XmlElementWrapper(name = "days")
	@XmlElement(name = "day")
	private List<String> day;

	private String period;

	public List<String> getUser() {
		return user;
	}

	public void setUser(List<String> user) {
		this.user = user;
	}

	public List<String> getRole() {
		return role;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

	public String getBlackoutType() {
		return blackoutType;
	}

	public void setBlackoutType(String blackoutType) {
		this.blackoutType = blackoutType;
	}

	public List<String> getDay() {
		return day;
	}

	public void setDay(List<String> day) {
		this.day = day;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
