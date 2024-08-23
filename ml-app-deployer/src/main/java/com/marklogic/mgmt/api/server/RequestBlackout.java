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
