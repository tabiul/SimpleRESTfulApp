package com.restapp.handler.rest;

import com.google.inject.Inject;
import com.restapp.handler.RequestHandler;
import com.restapp.pojo.Bird;
import com.restapp.service.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public class RESTfulHandler implements RequestHandler {
    @Inject
    private Service service;

    @GET
    @Path("view/bird")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public List<Bird> view() {
        return service.getAllBirds();
    }

    @GET
    @Path("add/bird")
    @Override
    public String addBird(@QueryParam("name") String name,
                          @QueryParam("color") String color,
                          @QueryParam("height") double height,
                          @QueryParam("weight") double weight) {
        if (service.addBird(name, color, weight, height)) {
            return "success";
        } else {
            return "fail";
        }
    }

    @GET
    @Path("delete/bird")
    @Override
    public String deleteBird(@QueryParam("name") String name) {
        if (service.deleteBird(name)) {
            return "success";
        } else {
            return "fail";
        }
    }

    @GET
    @Path("add/sighting")
    @Override
    public String addSighting(@QueryParam("name") String name,
                              @QueryParam("location") String location,
                              @QueryParam("date") String date) {
        if (service.addSighting(name, location, date)) {
            return "success";
        } else {
            return "fail";
        }
    }

    @GET
    @Path("view/sighting")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public List<Bird> viewSighting(@QueryParam("name") String name,
                                   @QueryParam("fromDate") String fromDate,
                                   @QueryParam("toDate") String toDate) {

        return service.getSighting(name, fromDate, toDate);
    }

    @DELETE
    @Path("quit")
    @Override
    public void quit() {
        service.shutdown();
    }
}
