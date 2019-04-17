package com.marklogic.appdeployer.command.databases;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.ResourceReference;
import com.marklogic.appdeployer.command.SortOrderConstants;
import com.marklogic.appdeployer.command.forests.DeployForestsCommand;
import com.marklogic.mgmt.SaveReceipt;
import com.marklogic.mgmt.api.configuration.Configuration;
import com.marklogic.mgmt.api.configuration.Configurations;
import com.marklogic.mgmt.api.database.Database;
import com.marklogic.mgmt.api.forest.Forest;
import com.marklogic.mgmt.resource.databases.DatabaseManager;
import com.marklogic.mgmt.util.ObjectMapperFactory;

import java.io.File;
import java.util.*;

/**
 * This commands handles deploying/undeploying every database file except the "default" ones of content-database.json,
 * triggers-database.json, and schemas-database.json. Those default ones are supported for ease-of-use, but it's not
 * uncommon to need to create additional databases (and perhaps REST API servers to go with them).
 * <p>
 * A key aspect of this class is its attempt to deploy/undeploy databases in the correct order. For each database file
 * that it finds that's not one of the default ones, a DeployDatabaseCommand will be created. These commands are then
 * sorted based on their dependencies between each other (a database can depend on another database for schemas, triggers,
 * or security).
 * <p>
 * If the above strategy doesn't work for you, you can always resort to naming your database files to control the order
 * that they're processed in. Be sure to set "setSortOtherDatabasesByDependencies" to false in the AppConfig object that
 * is passed in via a CommandContext to disable the sorting that this command will otherwise perform.
 * </p>
 */
public class DeployOtherDatabasesCommand extends AbstractUndoableCommand {

	// Each of these is copied to the instances of DeployDatabaseCommand that are created
    private int forestsPerHost = 1;
	private boolean checkForCustomForests = true;
	private String forestFilename;
	private boolean createForestsOnEachHost = true;

	/**
	 * Defines database names that, by default, this command will never undeploy.
	 */
	private Set<String> defaultDatabasesToNotUndeploy = new HashSet<>();

	private DeployDatabaseCommandFactory deployDatabaseCommandFactory = new DefaultDeployDatabaseCommandFactory();

    public DeployOtherDatabasesCommand() {
        this(1);
    }

    public DeployOtherDatabasesCommand(int forestsPerHost) {
    	setForestsPerHost(forestsPerHost);
	    setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
	    setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
	    initializeDefaultDatabasesToNotUndeploy();

	    setSupportsResourceMerging(true);
	    setResourceIdPropertyName("database-name");
	    setResourceClassType(Database.class);
    }

	protected void initializeDefaultDatabasesToNotUndeploy() {
		defaultDatabasesToNotUndeploy = new HashSet<>();
		defaultDatabasesToNotUndeploy.add("App-Services");
		defaultDatabasesToNotUndeploy.add("Documents");
		defaultDatabasesToNotUndeploy.add("Extensions");
		defaultDatabasesToNotUndeploy.add("Fab");
		defaultDatabasesToNotUndeploy.add("Last-Login");
		defaultDatabasesToNotUndeploy.add("Meters");
		defaultDatabasesToNotUndeploy.add("Modules");
		defaultDatabasesToNotUndeploy.add("Schemas");
		defaultDatabasesToNotUndeploy.add("Security");
		defaultDatabasesToNotUndeploy.add("Triggers");
	}

    @Override
    public void execute(CommandContext context) {
        List<DeployDatabaseCommand> deployDatabaseCommands = buildDatabaseCommands(context);
        sortCommandsBeforeExecute(deployDatabaseCommands, context);

	    List<ResourceReference> resourceReferences = new ArrayList<>();

        for (DeployDatabaseCommand c : deployDatabaseCommands) {
        	c.setPostponeForestCreation(context.getAppConfig().isDeployForestsWithCma());
            c.execute(context);

            String payload = c.getPayloadBeforeMerging();
            if (payload != null) {
	            resourceReferences.add(new ResourceReference(null, convertPayloadToObjectNode(context, payload)));
            }
        }

	    /**
	     * Reuse the parent class method for merging ResourceReference objects, but we don't need the files - we only
	     * need ObjectNodes, so we build a new list containing those and then sort them as needed.
	     */
	    List<ResourceReference> mergedReferences = mergeResources(resourceReferences);
        List<ObjectNode> mergedResources = new ArrayList<>();
        mergedReferences.forEach(reference -> mergedResources.add(reference.getObjectNode()));

        if (context.getAppConfig().isSortOtherDatabaseByDependencies()) {
	        Collections.sort(mergedResources, new DatabaseObjectNodeComparator(ObjectMapperFactory.getObjectMapper()));
        }

	    final DatabaseManager mgr = new DatabaseManager(context.getManageClient());
	    for (ObjectNode resource : mergedResources) {
		    saveResource(mgr, context, resource.toString());
	    }

	    // Either create forests in one bulk CMA request, or via a command per database
	    if (context.getAppConfig().isDeployForestsWithCma()) {
		    deployAllForestsInSingleCmaRequest(context, deployDatabaseCommands);
	    }
	    else {
	    	deployDatabaseCommands.forEach(command -> {
			    DeployForestsCommand dfc = command.getDeployForestsCommand();
			    if (dfc != null) {
			    	dfc.execute(context);
			    }
		    });
	    }

	    /**
	     * In the event that a sub-database is defined for a database that has files in multiple config paths, the
	     * sub-database will be saved multiple times, as the sub-database command iterates over every config paths.
	     * That shouldn't cause any problems, though it's not as efficient as it could be.
	     */
	    deployDatabaseCommands.forEach(command -> {
		    DeploySubDatabasesCommand subDatabasesCommand = command.getDeploySubDatabasesCommand();
		    if (subDatabasesCommand != null) {
			    subDatabasesCommand.execute(context);
		    }
	    });
    }

	@Override
	public void undo(CommandContext context) {
		List<DeployDatabaseCommand> list = buildDatabaseCommands(context);
		sortCommandsBeforeUndo(list, context);
		for (DeployDatabaseCommand c : list) {
			c.undo(context);
		}
	}

	/**
	 * Each DeployDatabaseCommand is expected to have constructed a DeployForestCommand, but not executed it. Each
	 * DeployForestCommand can then be used to build a list of forests. All of those forests can be combined into a
	 * single list and then submitted to CMA, thereby greatly speeding up the creation of the forests.
	 *
	 * @param context
	 * @param deployDatabaseCommands
	 */
	protected void deployAllForestsInSingleCmaRequest(CommandContext context, List<DeployDatabaseCommand> deployDatabaseCommands) {
	    List<Forest> allForests = new ArrayList<>();
	    for (DeployDatabaseCommand c : deployDatabaseCommands) {
		    DeployForestsCommand deployForestsCommand = c.getDeployForestsCommand();
		    if (deployForestsCommand != null) {
			    allForests.addAll(deployForestsCommand.buildForests(context, false));
		    }
	    }
	    if (!allForests.isEmpty()) {
		    Configuration config = new Configuration();
		    config.setForests(allForests);
		    new Configurations(config).submit(context.getManageClient());
	    }
    }

	/**
	 * Databases have dependencies on each other - they can be the schema or triggers databases for other databases. So
	 * they need to be deployed in an order that ensures any schema/triggers databases exist before a database is
	 * created.
	 *
	 * Once support exists for deploying databases via CMA, this shouldn't be needed (except for supporting clients on
	 * ML that don't yet have CMA).
	 *
	 * @param list
	 * @param context
	 */
	protected void sortCommandsBeforeExecute(List<DeployDatabaseCommand> list, CommandContext context) {
    	if (context.getAppConfig().isSortOtherDatabaseByDependencies()) {
		    Collections.sort(list, new DeployDatabaseCommandComparator(context, false));
	    }
	    else {
    		logger.info("Not sorting databases by dependencies; they will be sorted based on their filename instead");
	    }
    }

    protected void sortCommandsBeforeUndo(List<DeployDatabaseCommand> list, CommandContext context) {
        Collections.sort(list, new DeployDatabaseCommandComparator(context, true));
    }

    protected List<DeployDatabaseCommand> buildDatabaseCommands(CommandContext context) {
        List<DeployDatabaseCommand> dbCommands = new ArrayList<>();
        for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
	        File dir = configDir.getDatabasesDir();
	        if (dir != null && dir.exists()) {
		        ignoreKnownDatabaseFilenames(context);
		        for (File f : listFilesInDirectory(dir)) {
			        if (logger.isDebugEnabled()) {
				        logger.debug("Will process other database in file: " + f.getName());
			        }
			        dbCommands.add(buildDeployDatabaseCommand(f));
		        }
	        } else {
	        	logResourceDirectoryNotFound(dir);
	        }
        }
        return dbCommands;
    }

    protected DeployDatabaseCommand buildDeployDatabaseCommand(File file) {
	    DeployDatabaseCommand c = this.deployDatabaseCommandFactory.newDeployDatabaseCommand(file);
	    c.setForestsPerHost(getForestsPerHost());
	    c.setCheckForCustomForests(isCheckForCustomForests());
	    c.setForestFilename(getForestFilename());
	    c.setCreateForestsOnEachHost(isCreateForestsOnEachHost());
	    c.setDatabasesToNotUndeploy(this.getDefaultDatabasesToNotUndeploy());
	    return c;
    }

    /**
     * Adds to the list of resource filenames to ignore. Some may already have been set via the superclass method.
     *
     * @param context
     */
    protected void ignoreKnownDatabaseFilenames(CommandContext context) {
        Set<String> ignore = new HashSet<>();
        for (ConfigDir configDir : context.getAppConfig().getConfigDirs()) {
        	List<File> list = configDir.getContentDatabaseFiles();
        	if (list != null && !list.isEmpty()) {
		        for (File f : list) {
			        ignore.add(f.getName());
		        }
	        }
        }
        setFilenamesToIgnore(ignore.toArray(new String[]{}));
    }

	public int getForestsPerHost() {
		return forestsPerHost;
	}

	public void setForestsPerHost(int forestsPerHost) {
		this.forestsPerHost = forestsPerHost;
	}

	public boolean isCheckForCustomForests() {
		return checkForCustomForests;
	}

	public void setCheckForCustomForests(boolean checkForCustomForests) {
		this.checkForCustomForests = checkForCustomForests;
	}

	public String getForestFilename() {
		return forestFilename;
	}

	public void setForestFilename(String forestFilename) {
		this.forestFilename = forestFilename;
	}

	public boolean isCreateForestsOnEachHost() {
		return createForestsOnEachHost;
	}

	public void setCreateForestsOnEachHost(boolean createForestsOnEachHost) {
		this.createForestsOnEachHost = createForestsOnEachHost;
	}

	public Set<String> getDefaultDatabasesToNotUndeploy() {
		return defaultDatabasesToNotUndeploy;
	}

	public void setDefaultDatabasesToNotUndeploy(Set<String> defaultDatabasesToNotUndeploy) {
		this.defaultDatabasesToNotUndeploy = defaultDatabasesToNotUndeploy;
	}

	public void setDeployDatabaseCommandFactory(DeployDatabaseCommandFactory deployDatabaseCommandFactory) {
		this.deployDatabaseCommandFactory = deployDatabaseCommandFactory;
	}
}
