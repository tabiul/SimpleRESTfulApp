package com.restapp.pojo;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

public class Sighting {
    private final String location;
    private final Date date;

    public Sighting(String location, Date date) {
        this.location = location;
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Sighting [location=" + location + ", date=" + date + "]";
    }
}
