package com.restapp.handler;

import com.restapp.pojo.Bird;

import java.util.List;

public interface RequestHandler {
    /**
     * handles request from client to view all birds
     *
     * @return List<Bird> - empty list if no birds found
     */
    public List<Bird> view();

    /**
     * handles request from client to add a new bird
     *
     * @param name
     * @param color
     * @param height
     * @param weight
     * @return String - success or failure
     */
    public String addBird(String name, String color, double height,
                          double weight);

    /**
     * handles request from client to delete a bird
     *
     * @param name
     * @return String - success or failure
     */
    public String deleteBird(String name);

    /**
     * handles request from client to add new bird sighting
     *
     * @param name
     * @param location
     * @param date
     * @return String - success or failure
     */
    public String addSighting(String name, String location, String date);

    /**
     * handles request from client to view birds based upon sighting for
     * specific date range
     *
     * @param name     - regular expression
     * @param fromDate
     * @param toDate
     * @return List<Bird> - empty list if not data is found
     */
    public List<Bird> viewSighting(String name, String fromDate, String toDate);

    /**
     * handles request from client to shutdown the application
     */
    public void quit();

}
