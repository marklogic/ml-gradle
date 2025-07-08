/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package example;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Main {

	public static void main(String[] args) {
		// Construct a simple data source that uses the PostgreSQL driver to talk to MarkLogic
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.postgresql.Driver");
		// preferQueryMode=simple is required; readonly is optional, but makes sense since updates aren't allowed
		dataSource.setUrl("jdbc:postgresql://localhost:8005/?preferQueryMode=simple&readonly=true");
		dataSource.setUsername("admin");
		dataSource.setPassword("admin");

		// Construct a Spring JdbcTemplate for easy JDBC access
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		// Run a query!
		jdbcTemplate.query("SELECT SCHEMA, NAME FROM SYS_TABLES", resultSetRow -> {
			String schema = resultSetRow.getString("SCHEMA");
			String name = resultSetRow.getString("NAME");

			System.out.print("SCHEMA: " + schema);
			System.out.println(", NAME: " + name);
		});
	}
}
