package com.restapp.storage;

import com.restapp.pojo.Bird;
import com.restapp.pojo.Sighting;
import com.restapp.storage.exception.DataStorageException;

import java.util.Date;
import java.util.List;

public interface DataStorage {
    public boolean addBird(final Bird bird) throws DataStorageException;

    public boolean deleteBird(final String name) throws DataStorageException;

    public boolean addSighting(final String name, final Sighting sighting)
            throws DataStorageException;

    public List<Bird> getAllBirds() throws DataStorageException;

    public List<Bird> getBirdByDate(final Date sightingFrom,
                                    final Date sightingTo) throws DataStorageException;

}
