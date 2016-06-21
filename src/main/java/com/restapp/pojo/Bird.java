package com.restapp.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bird {
    private final String name;
    private final String color;
    private final double weight;
    private final double height;
    private List<Sighting> sighthings = new ArrayList<>();

    public Bird(String name, String color, double weight, double height) {
        this.name = name;
        this.color = color;
        this.weight = weight;
        this.height = height;
    }

    public void addSighting(Sighting sighting) {
        sighthings.add(sighting);
    }

    public void addAllSighting(List<Sighting> sightings) {
        sighthings.addAll(sightings);
    }

    public List<Sighting> getSightings() {
        return Collections.unmodifiableList(sighthings);
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Bird [name=" + name + ", color=" + color + ", weight=" + weight
                + ", height=" + height + "]";
    }

}
