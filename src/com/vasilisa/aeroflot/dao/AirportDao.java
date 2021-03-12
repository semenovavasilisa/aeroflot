package com.vasilisa.aeroflot.dao;

import com.vasilisa.aeroflot.entity.Airport;
import com.vasilisa.aeroflot.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AirportDao implements Dao<Integer, Airport> {

    private static final AirportDao INSTANCE = new AirportDao();
    private static final String DELETE_SQL = """
            DELETE FROM airport
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO airport (name, latitude, longitude, country, open)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE airport
            SET name = ?,
            latitude = ?,
            longitude = ?,
            country = ?,
            open = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id,
            name,
            latitude,
            longitude,
            country,
            open
            FROM airport
            """;


    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ? """;

    private AirportDao() {
    }

    @Override
    @SneakyThrows
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Airport save(Airport airport) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setString(1, airport.getName());
            preparedStatement.setBigDecimal(2, airport.getLatitude());
            preparedStatement.setBigDecimal(3, airport.getLongitude());
            preparedStatement.setString(4, airport.getCountry());
            preparedStatement.setBoolean(5, airport.isOpen());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            airport.setId(generatedKeys.getInt("id"));
            return airport;
        }
    }

    @Override
    @SneakyThrows
    public void update(Airport airport) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, airport.getName());
            preparedStatement.setBigDecimal(2, airport.getLatitude());
            preparedStatement.setBigDecimal(3, airport.getLongitude());
            preparedStatement.setString(4, airport.getCountry());
            preparedStatement.setBoolean(5, airport.isOpen());
            preparedStatement.setInt(6, airport.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public Optional<Airport> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Airport airport = null;
            if (resultSet.next()) {
                airport = buildAirport(resultSet);
            }
            return Optional.ofNullable(airport);
        }
    }

    @Override
    @SneakyThrows
    public List<Airport> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Airport> airports = new ArrayList<>();
            while (resultSet.next()) {
                airports.add(buildAirport(resultSet));
            }
            return airports;
        }
    }

    @SneakyThrows
    private Airport buildAirport(ResultSet resultSet) {
        return new Airport(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBigDecimal("latitude"),
                resultSet.getBigDecimal("longitude"),
                resultSet.getString("country"),
                resultSet.getBoolean("open")
        );
    }

    public static AirportDao getInstance() {
        return INSTANCE;
    }
}
