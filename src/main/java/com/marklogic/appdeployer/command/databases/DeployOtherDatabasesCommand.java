package com.marklogic.appdeployer.command.databases;

import com.marklogic.appdeployer.ConfigDir;
import com.marklogic.appdeployer.command.AbstractUndoableCommand;
import com.marklogic.appdeployer.command.CommandContext;
import com.marklogic.appdeployer.command.SortOrderConstants;

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

    public DeployOtherDatabasesCommand() {
        this(1);
    }

    public DeployOtherDatabasesCommand(int forestsPerHost) {
    	setForestsPerHost(forestsPerHost);
	    setExecuteSortOrder(SortOrderConstants.DEPLOY_OTHER_DATABASES);
	    setUndoSortOrder(SortOrderConstants.DELETE_OTHER_DATABASES);
    }

    @Override
    public void execute(CommandContext context) {
        List<DeployDatabaseCommand> list = buildDatabaseCommands(context);
        sortCommandsBeforeExecute(list, context);
        for (DeployDatabaseCommand c : list) {
            c.execute(context);
        }
    }

    protected void sortCommandsBeforeExecute(List<DeployDatabaseCommand> list, CommandContext context) {
    	if (context.getAppConfig().isSortOtherDatabaseByDependencies()) {
		    Collections.sort(list, new DeployDatabaseCommandComparator(context, false));
	    }
	    else {
    		logger.info("Not sorting databases by dependencies; they will be sorted based on their filename instead");
	    }
    }

    @Override
    public void undo(CommandContext context) {
        List<DeployDatabaseCommand> list = buildDatabaseCommands(context);
        sortCommandsBeforeUndo(list, context);
        for (DeployDatabaseCommand c : list) {
            c.undo(context);
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
	    DeployDatabaseCommand c = new DeployDatabaseCommand(file);
	    c.setForestsPerHost(getForestsPerHost());
	    c.setCheckForCustomForests(isCheckForCustomForests());
	    c.setForestFilename(getForestFilename());
	    c.setCreateForestsOnEachHost(isCreateForestsOnEachHost());
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
        ignore.add(DeploySchemasDatabaseCommand.DATABASE_FILENAME);
        ignore.add(DeployTriggersDatabaseCommand.DATABASE_FILENAME);
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
}
