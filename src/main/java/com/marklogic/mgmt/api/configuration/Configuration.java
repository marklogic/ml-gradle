package com.marklogic.mgmt.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.api.server.Server;

import java.util.List;

/**
 * Does not yet support XML marshalling via JAXB.
 */
public class Configuration {

	@JsonProperty("amp")
	private List<Amp> amps;

	@JsonProperty("database")
	private List<Database> databases;

	@JsonProperty("forest")
	private List<Forest> forests;

	@JsonProperty("group")
	private List<Group> groups;

	@JsonProperty("role")
	private List<Role> roles;

	@JsonProperty("server")
	private List<Server> servers;

	@JsonProperty("user")
	private List<User> users;

	public List<Amp> getAmps() {
		return amps;
	}

	public void setAmps(List<Amp> amps) {
		this.amps = amps;
	}

	public List<Forest> getForests() {
		return forests;
	}

	public void setForests(List<Forest> forests) {
		this.forests = forests;
	}

	public List<Database> getDatabases() {
		return databases;
	}

	public void setDatabases(List<Database> databases) {
		this.databases = databases;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
