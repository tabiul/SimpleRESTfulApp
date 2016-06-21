package com.restapp.storage.exception;

public class DataStorageException extends Exception {
    private static final long serialVersionUID = -9171538947761384034L;

    public DataStorageException() {
        super();
    }

    public DataStorageException(final String message) {
        super(message);
    }

    public DataStorageException(Throwable ex) {
        super(ex);
    }

    public DataStorageException(final String message, Throwable ex) {
        super(message, ex);
    }
}
