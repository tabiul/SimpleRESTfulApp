package com.restapp.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.restapp.pojo.Bird;
import com.restapp.pojo.Sighting;
import com.restapp.storage.DataStorage;
import com.restapp.utils.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class AppServer {
    private static final String PORT_PARAMETER = "port";
    private static final String DATA_PARAMETER = "data";
    private static final String PROC_COUNT_PARAMETER = "proc_count";
    private static final String HELP_PARAMETER = "help";
    private static final String BIRD_FILENAME = "birds.xml";
    private static final String SIGHTING_FILENAME = "sightings.xml";
    private int portNumber = 3000;
    private int procCount = 2;
    private String dataFolder = null;

    @SuppressWarnings("static-access")
    public AppServer() {

    }

    public static void main(String[] args) throws Exception {
        AppServer server = new AppServer();
        if(server.parseArguments(args)) server.startServer();
    }

    public boolean parseArguments(String args[]) throws IOException {

        // parse the command line arguments
        OptionParser parser = new OptionParser();
        OptionSpec<Integer> portOption = parser.accepts(PORT_PARAMETER, "listening port").withRequiredArg().ofType(Integer.class);
        OptionSpec<String> dataOption = parser.accepts(DATA_PARAMETER, "folder path containing xml files").withRequiredArg().ofType(String.class).required();
        OptionSpec<Integer> workerOption = parser.accepts(PROC_COUNT_PARAMETER, "number of worker process").withRequiredArg().ofType(Integer.class);
        parser.accepts(HELP_PARAMETER, "show help").forHelp();
        try {
            OptionSet optionSet = parser.parse(args);
            if (optionSet.has(HELP_PARAMETER)) {
                parser.printHelpOn(System.out);
                return false;
            }
            if (optionSet.has(PORT_PARAMETER)) portNumber = optionSet.valueOf(portOption);
            if (optionSet.has(PROC_COUNT_PARAMETER)) procCount = optionSet.valueOf(workerOption);
            if (optionSet.has(DATA_PARAMETER)) {

                dataFolder = optionSet.valueOf(dataOption);
                if (!FileUtils.isValidDirectory(dataFolder)) {
                    throw new RuntimeException("invalid directory " + dataFolder);
                }
                if (!FileUtils.hasReadAndWriteAccess(dataFolder)) {
                    throw new RuntimeException(
                            "need both read and write access to directory " + dataFolder);
                }
            }
            return true;
        } catch (Exception ex) {
            parser.printHelpOn(System.err);
            throw ex;
        }

    }

    private void loadInitialData(DataStorage storage) throws Exception {
        String birdFileName = dataFolder + File.separator + BIRD_FILENAME;
        String sightingFileName = dataFolder + File.separator
                + SIGHTING_FILENAME;
        if (FileValidators.isValidFilePath(birdFileName)
                && FileValidators.isValidFilePath(sightingFileName)) {
            if (FileValidators.hasReadAndWriteAccess(birdFileName)
                    && FileValidators.hasReadAndWriteAccess(sightingFileName)) {
                List<Bird> birds = XmlReaderAndWriter.readBirdFile(birdFileName);
                for (Bird bird : birds) {
                    storage.addBird(bird);
                }
                Map<String, List<Sighting>> sightingMap = XmlReaderAndWriter
                        .readSightingFile(sightingFileName);
                for (Map.Entry<String, List<Sighting>> e : sightingMap
                        .entrySet()) {
                    for (Sighting sighting : e.getValue()) {
                        storage.addSighting(e.getKey(), sighting);
                    }
                }
            } else {
                throw new Exception("need read and write access filename "
                        + birdFileName + " and " + sightingFileName);
            }
        } else {
            throw new Exception("invalid filename " + birdFileName + " and "
                    + sightingFileName);
        }
    }

    private void startJetty(AppModule module) throws Exception {
        ServletContextHandler context = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        Server jettyServer = new Server(portNumber);
        module.setJettyServer(jettyServer);
        ServerConnector connector = new ServerConnector(jettyServer, procCount,
                procCount);
        jettyServer.addConnector(connector);
        jettyServer.setHandler(context);
        StatisticsHandler statisticsHandler = new StatisticsHandler();
        context.setHandler(statisticsHandler);
        context.addFilter(GuiceFilter.class, "/*", EnumSet
                .<javax.servlet.DispatcherType>of(
                        javax.servlet.DispatcherType.REQUEST,
                        javax.servlet.DispatcherType.ASYNC));

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();

        }
    }

    private void launchBackgroundBackupThread(AppModule module,
                                              DataStorage storage) {
        String birdFileName = dataFolder + File.separator + BIRD_FILENAME;
        String sightingFileName = dataFolder + File.separator
                + SIGHTING_FILENAME;
        Backup bk = new Backup(storage, 50000, birdFileName, sightingFileName);
        Thread backupThread = new Thread(bk);
        module.setBackup(bk);
        backupThread.start();
    }

    public void startServer() throws Exception {
        AppModule module = new AppModule();
        module.setBirdFilePath(dataFolder + File.separator + BIRD_FILENAME);
        module.setSightingFilePath(dataFolder + File.separator
                + SIGHTING_FILENAME);
        JeryseyServletModule servletModule = new JeryseyServletModule();
        Injector injector = Guice.createInjector(module, servletModule);

        DataSource dataSource = injector.getProvider(DataSource.class)
                .get();
        DbUtils.createTables(dataSource.getConnection());
        DbUtils.deleteTables(dataSource.getConnection());
        DataStorage storage = injector.getInstance(DataStorage.class);
        loadInitialData(storage);
        launchBackgroundBackupThread(module, storage);
        startJetty(module);
    }

}
