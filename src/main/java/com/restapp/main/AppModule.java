package com.restapp.main;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.restapp.pojo.Bird;
import com.restapp.service.Service;
import com.restapp.service.impl.ServiceImpl;
import com.restapp.storage.DataStorage;
import com.restapp.storage.impl.SqlBasedStorage;
import com.restapp.utils.Backup;
import com.restapp.utils.XmlReaderAndWriter;
import org.eclipse.jetty.server.Server;

import javax.sql.DataSource;
import java.util.List;

public class AppModule extends AbstractModule {
    private Server jettyServer;
    private String birdFilePath;
    private String sightingFilePath;
    private Backup backup;

    public void setJettyServer(Server jettyServer) {
        this.jettyServer = jettyServer;
    }

    public void setBirdFilePath(String birdFilePath) {
        this.birdFilePath = birdFilePath;
    }

    public void setSightingFilePath(String sightingFilePath) {
        this.sightingFilePath = sightingFilePath;
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    @Override
    protected void configure() {
        bind(Service.class).to(ServiceImpl.class);
        bind(DataStorage.class).to(SqlBasedStorage.class);
        bind(DataSource.class).toProvider(DatabaseProvider.class);
    }

    @Provides
    @Singleton
    @Inject
    ShutdownService getShutdownService(DataStorage storage) {
        return new ShutdownService() {
            @Override
            public synchronized void shutdown() {
                System.out.println("shutting down ..........");
                try {
                    Runnable r = () -> {
                        try {
                            backup.setStop(true);
                            List<Bird> birds = storage.getAllBirds();
                            XmlReaderAndWriter.writeData(birds, birdFilePath,
                                    sightingFilePath);
                            jettyServer.setStopTimeout(50000L);
                            jettyServer.stop();
                            jettyServer.destroy();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    };
                    new Thread(r).run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
