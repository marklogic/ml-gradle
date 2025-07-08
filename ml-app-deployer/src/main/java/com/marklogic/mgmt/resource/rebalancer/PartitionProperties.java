/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
