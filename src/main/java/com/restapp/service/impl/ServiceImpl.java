package com.restapp.service.impl;

import com.google.inject.Inject;
import com.restapp.main.ShutdownService;
import com.restapp.pojo.Bird;
import com.restapp.pojo.Sighting;
import com.restapp.service.Service;
import com.restapp.storage.DataStorage;
import com.restapp.utils.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ServiceImpl implements Service {
    @Inject
    private DataStorage storage;

    @Inject
    private ShutdownService shutdownService;

    @Override
    public List<Bird> getAllBirds() {
        try {
            return storage.getAllBirds();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<Bird>();
    }

    @Override
    public boolean addBird(String name, String color, double weight,
                           double height) {
        try {
            if (name.isEmpty())
                return false;
            return storage.addBird(new Bird(name, color, weight, height));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteBird(String name) {
        try {
            if (name.isEmpty())
                return false;
            return storage.deleteBird(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean addSighting(String name, String location, String date) {
        try {
            if (name.isEmpty())
                return false;
            return storage.addSighting(name, new Sighting(location,
                    Common.dateFormatWithTime.parse(date)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    public List<Bird> getSighting(String name, String fromDate, String toDate) {
        List<Bird> birdList = new ArrayList<>();
        try {
            if (!name.isEmpty()) {
                List<Bird> birds = storage.getBirdByDate(
                        Common.dateFormatWithoutTime.parse(fromDate),
                        Common.dateFormatWithoutTime.parse(toDate));
                Pattern p = Pattern.compile(name);
                for (Bird bird : birds) {
                    if (p.matcher(bird.getName()).matches()) {
                        birdList.add(bird);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Collections.unmodifiableList(birdList);
    }

    @Override
    public void shutdown() {
        shutdownService.shutdown();
    }

}
