package com.marklogic.mgmt.api.server;

import java.util.List;

public class RequestBlackout {

	private List<String> user;
	private List<String> role;
	private String blackoutType;
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
