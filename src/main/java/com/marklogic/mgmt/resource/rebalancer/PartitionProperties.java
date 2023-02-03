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
package com.marklogic.mgmt.resource.rebalancer;

/**
 * This class isn't yet in the "api" package because the endpoint for partition properties returns JSON with a
 * "partition-properties" root node. And I haven't found a good answer for how to ignore that when unmarshalling
 * JSON without setting an ObjectMapper-wide deserialization feature that may negatively impact how that ObjectMapper
 * is used in other contexts.
 */
public class PartitionProperties {

	public final static String AVAILABILITY_ONLINE = "online";
	public final static String AVAILABILITY_OFFLINE = "offline";

	private String availability;
	private String updatesAllowed;

	public boolean isOnline() {
		return AVAILABILITY_ONLINE.equals(availability);
	}

	public boolean isOffline() {
		return AVAILABILITY_OFFLINE.equals(availability);
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	public String getUpdatesAllowed() {
		return updatesAllowed;
	}

	public void setUpdatesAllowed(String updatesAllowed) {
		this.updatesAllowed = updatesAllowed;
	}
}
