package com.restapp.storage.impl;

import com.google.inject.Inject;
import com.restapp.pojo.Bird;
import com.restapp.pojo.Sighting;
import com.restapp.storage.DataStorage;
import com.restapp.storage.exception.DataStorageException;
import com.restapp.utils.Common;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class SqlBasedStorage implements DataStorage {
    private final DataSource dataSource;

    @Inject
    public SqlBasedStorage(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean addBird(Bird bird) throws DataStorageException {
        try (Connection connection = dataSource.getConnection()) {

            String insertStatement = "insert into bird(name, color, height, weight) values(?,?,?,?)";
            PreparedStatement statement = connection
                    .prepareStatement(insertStatement);
            statement.setString(1, bird.getName());
            statement.setString(2, bird.getColor());
            statement.setDouble(3, bird.getHeight());
            statement.setDouble(4, bird.getWeight());
            return statement.executeUpdate() == 1 ? true : false;
        } catch (Exception ex) {
            throw new DataStorageException("error in addBird", ex);
        }

    }

    @Override
    public boolean deleteBird(String name) throws DataStorageException {
        try (Connection connection = dataSource.getConnection()) {
            String deleteBird = "delete from bird where name = ?";
            String deleteSighting = "delete from sighting where name = ?";

            PreparedStatement deleteSightingStatement = connection
                    .prepareStatement(deleteSighting);
            deleteSightingStatement.setString(1, name);

            PreparedStatement deleteBirdStatement = connection
                    .prepareStatement(deleteBird);
            deleteBirdStatement.setString(1, name);

            deleteSightingStatement.executeUpdate();

            return deleteBirdStatement.executeUpdate() == 1 ? true : false;

        } catch (Exception ex) {
            throw new DataStorageException("error in deleteBird", ex);
        }

    }

    @Override
    public boolean addSighting(String name, Sighting sighting)
            throws DataStorageException {
        try (Connection connection = dataSource.getConnection()) {
            String insertStatement = "insert into sighting(name, location, date) values(?,?,?)";
            PreparedStatement statement = connection
                    .prepareStatement(insertStatement);
            statement.setString(1, name);
            statement.setString(2, sighting.getLocation());
            statement.setString(3, Common.dateFormatWithTime.format(sighting.getDate()));
            return statement.executeUpdate() == 1 ? true : false;
        } catch (Exception ex) {
            throw new DataStorageException("error in addSighting", ex);
        }

    }

    @Override
    public List<Bird> getAllBirds() throws DataStorageException {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("select * from sighting where name = ?");
            ResultSet rs = statement.executeQuery("select * from bird");
            List<Bird> birdList = new ArrayList<>();
            while (rs.next()) {
                Bird bird = new Bird(rs.getString("name"),
                        rs.getString("color"), rs.getDouble("weight"),
                        rs.getDouble("height"));
                preparedStatement.setString(1, bird.getName());
                ResultSet rsSighting = preparedStatement.executeQuery();
                while (rsSighting.next()) {
                    bird.addSighting(new Sighting(rsSighting
                            .getString("location"), Common.dateFormatWithTime.parse(rsSighting
                            .getString("date"))));
                }
                birdList.add(bird);
            }
            return Collections.unmodifiableList(birdList);
        } catch (Exception ex) {
            throw new DataStorageException("error in getAllBirds", ex);
        }

    }

    @Override
    public List<Bird> getBirdByDate(Date sightingFrom, Date sightingTo)
            throws DataStorageException {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("select * from sighting");
                      ResultSet rs = preparedStatement.executeQuery();
            List<Bird> birdList = new ArrayList<>();
            Map<String, List<Sighting>> sightingMap = new HashMap<>();
            while (rs.next()) {
                Date sightingDate = Common.dateFormatWithoutTime.parse(rs.getString("date"));
                if(sightingDate.after(sightingFrom) && sightingDate.before(sightingTo)) {
                    if (sightingMap.containsKey(rs.getString("name"))) {
                        sightingMap.get(rs.getString("name")).add(
                                new Sighting(rs.getString("location"), Common.dateFormatWithTime.parse(rs.getString("date"))));
                    } else {
                        List<Sighting> sightings = new ArrayList<>();
                        sightings.add(new Sighting(rs.getString("location"),
                                Common.dateFormatWithTime.parse(rs.getString("date"))));
                        sightingMap.put(rs.getString("name"), sightings);
                    }
                }
            }
            PreparedStatement preparedStatementBird = connection
                    .prepareStatement("select * from bird where name = ?");
            for (String key : sightingMap.keySet()) {
                preparedStatementBird.setString(1, key);
                ResultSet rsBird = preparedStatementBird.executeQuery();
                while (rsBird.next()) {
                    Bird bird = new Bird(rsBird.getString("name"),
                            rsBird.getString("color"),
                            rsBird.getDouble("weight"),
                            rsBird.getDouble("height"));
                    bird.addAllSighting(sightingMap.get(key));
                    birdList.add(bird);
                }
            }
            return birdList;
        } catch (Exception ex) {
            throw new DataStorageException("error in getBirdByDate", ex);
        }
    }

}
