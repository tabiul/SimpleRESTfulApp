package com.restapp.service;

import com.restapp.pojo.Bird;

import java.util.List;

public interface Service {
    public List<Bird> getAllBirds();

    public boolean addBird(String name, String color, double weight,
                           double height);

    public boolean deleteBird(String name);

    public boolean addSighting(String name, String location, String date);

    public List<Bird> getSighting(String name, String fromDate, String toDate);

    public void shutdown();
}
