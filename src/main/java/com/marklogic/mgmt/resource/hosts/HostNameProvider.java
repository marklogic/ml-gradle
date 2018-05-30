package com.marklogic.mgmt.resource.hosts;

import java.util.List;

/**
 * This interface exists primarily to facilitate unit testing of classes that determine what hosts forests should be
 * assigned to without having to connect to a real MarkLogic instance.
 */
public interface HostNameProvider {

	List<String> getHostNames();

	List<String> getGroupHostNames(String groupName);

}
