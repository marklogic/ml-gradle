package com.marklogic.mgmt.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.api.group.Group;
import com.marklogic.mgmt.api.security.Amp;
import com.marklogic.mgmt.api.security.Privilege;
import com.marklogic.mgmt.api.security.Role;
import com.marklogic.mgmt.api.security.User;
import com.marklogic.mgmt.api.server.Server;

import java.util.ArrayList;
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

	@JsonProperty("privilege")
	private List<Privilege> privileges;

	@JsonProperty("role")
	private List<Role> roles;

	@JsonProperty("server")
	private List<Server> servers;

	@JsonProperty("user")
	private List<User> users;

	public void addAmp(Amp amp) {
		if (amps == null) amps = new ArrayList<>();
		amps.add(amp);
	}

	public void addDatabase(Database d) {
		if (databases == null) databases = new ArrayList<>();
		databases.add(d);
	}

	public void addForest(Forest f) {
		if (forests == null) forests = new ArrayList<>();
		forests.add(f);
	}

	public void addGroup(Group g) {
		if (groups == null) groups = new ArrayList<>();
		groups.add(g);
	}

	public void addRole(Role r) {
		if (roles == null) roles = new ArrayList<>();
		roles.add(r);
	}

	public void addServer(Server s) {
		if (servers == null) servers = new ArrayList<>();
		servers.add(s);
	}

	public void addUser(User u) {
		if (users == null) users = new ArrayList<>();
		users.add(u);
	}

	public void addPrivilege(Privilege p) {
		if (privileges == null) privileges = new ArrayList<>();
		privileges.add(p);
	}

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

	public List<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Privilege> privileges) {
		this.privileges = privileges;
	}
}
