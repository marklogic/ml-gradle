package com.rjrudin.marklogic.modulesloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.rjrudin.marklogic.modulesloader.impl.DefaultModulesLoader;
import com.rjrudin.marklogic.modulesloader.impl.XccAssetLoader;

public class ModulesWatcher {

    private static final Logger logger = LoggerFactory.getLogger(ModulesWatcher.class);

    /**
     * List of command-line args:
     * <ol>
     * <li>First arg is a comma-delimited list of file paths to load modules from</li>
     * <li>Second arg is the MarkLogic host to connect to</li>
     * <li>Third arg is the REST API port to connect to on the MarkLogic host (must support XDBC calls)</li>
     * <li>Fourth arg is the MarkLogic username to connect with</li>
     * <li>Fifth arg is the password for the MarkLogic username</li>
     * <li>The optional sixth arg is the fully-qualified class name of a ModulesLoader implementation</li>
     * </ol>
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String commaDelimitedPaths = args[0];
        String host = args[1];
        int port = Integer.parseInt(args[2]);
        String username = args[3];
        String password = args[4];

        ModulesLoader loader = null;
        if (args.length > 5) {
            logger.info("Using " + args[5] + " to load modules");
            loader = (DefaultModulesLoader) Class.forName(args[5]).newInstance();
        } else {
            logger.info("Using " + DefaultModulesLoader.class.getName() + " to load modules");
            XccAssetLoader xal = new XccAssetLoader();
            xal.setHost(host);
            xal.setPort(port);
            xal.setUsername(username);
            xal.setPassword(password);

            DefaultModulesLoader impl = new DefaultModulesLoader(xal);
            impl.setCatchExceptions(true);
            loader = impl;
        }

        logger.info(String.format("Connecting to http://%s:%d as user %s", host, port, username));
        final DatabaseClient client = DatabaseClientFactory.newClient(host, port, username, password,
                Authentication.DIGEST);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Releasing client");
                client.release();
            }
        });

        List<File> dirs = new ArrayList<>();
        for (String path : commaDelimitedPaths.split(",")) {
            File dir = new File(path);
            dirs.add(dir);
            logger.info("Watching directory: " + dir.getAbsolutePath());
        }

        while (true) {
            if (logger.isTraceEnabled()) {
                logger.trace("Auto-installing any modified files");
            }
            for (File dir : dirs) {
                loader.loadModules(dir, client);
            }
            Thread.sleep(1000);
        }
    }
}
