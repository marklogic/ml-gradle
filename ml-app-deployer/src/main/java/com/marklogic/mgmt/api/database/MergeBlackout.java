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

import com.marklogic.mgmt.api.ApiObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class MergeBlackout extends ApiObject {

	@XmlElement(name = "blackout-type")
	private String blackoutType;

	private Integer limit;

	@XmlElement(name = "merge-priority")
	private String mergePriority;

	@XmlElementWrapper(name = "days")
	private List<String> day;

	private MergePeriod period;

	public String getBlackoutType() {
		return blackoutType;
	}

	public void setBlackoutType(String blackoutType) {
		this.blackoutType = blackoutType;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getMergePriority() {
		return mergePriority;
	}

	public void setMergePriority(String mergePriority) {
		this.mergePriority = mergePriority;
	}

	public List<String> getDay() {
		return day;
	}

	public void setDay(List<String> day) {
		this.day = day;
	}

	public MergePeriod getPeriod() {
		return period;
	}

	public void setPeriod(MergePeriod period) {
		this.period = period;
	}
}
