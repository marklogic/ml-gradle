package com.marklogic.appdeployer.command.forests;

import com.marklogic.appdeployer.command.CommandContext;

import java.util.List;

public interface HostCalculator {

	List<String> calculateHostNames(String databaseName, CommandContext context);

}
