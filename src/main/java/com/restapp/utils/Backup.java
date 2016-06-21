package com.restapp.utils;

import com.restapp.pojo.Bird;
import com.restapp.storage.DataStorage;

import java.util.List;

public class Backup implements Runnable {
    private final DataStorage storage;
    private final long delay;
    private final String birdFilePath;
    private final String sightingsFilePath;
    private volatile boolean stop = false;

    public Backup(final DataStorage storage, final long delay,
                  final String birdFilePath, final String sightingsFilePath) {
        this.storage = storage;
        this.delay = delay;
        this.birdFilePath = birdFilePath;
        this.sightingsFilePath = sightingsFilePath;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                System.out.println("Start Backup");
                List<Bird> birds = storage.getAllBirds();
                XmlReaderAndWriter.writeData(birds, birdFilePath, sightingsFilePath);
                System.out.println("End Backup");
                Thread.sleep(delay);
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }

        }
    }

}
