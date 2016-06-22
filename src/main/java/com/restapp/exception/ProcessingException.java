package com.restapp.exception;

import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Created by tabiul on 22 Jun 2016.
 */
public class ProcessingException extends WebApplicationException {
    public ProcessingException(String message) {
        super(Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).
                entity(message).type("text/plain").build());
    }

}
