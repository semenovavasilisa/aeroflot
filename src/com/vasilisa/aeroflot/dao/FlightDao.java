package com.vasilisa.aeroflot.dao;

import com.vasilisa.aeroflot.entity.Flight;
import com.vasilisa.aeroflot.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {
    private static final FlightDao INSTANCE = new FlightDao();
    private final AirportDao airportDao = AirportDao.getInstance();
    private final AircraftDao aircraftDao = AircraftDao.getInstance();
    private final AircrewDao aircrewDao = AircrewDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE FROM flight
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO flight (aircrew_id, aircraft_id, airport_departure_id, 
            airport_arrival_id, departure_time, arrival_time)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE flight
            SET aircrew_id = ?,
            aircraft_id = ?,
            airport_departure_id = ?,
            airport_arrival_id = ?,
            departure_time = ?,
            arrival_time = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
                 SELECT id,
            aircrew_id,
            aircraft_id,
            airport_departure_id,
            airport_arrival_id,
            departure_time,
            arrival_time
            FROM flight
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ? """;

    private FlightDao() {
    }

    @Override
    @SneakyThrows
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Flight save(Flight flight) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setLong(1, flight.getAircrew().getId());
            preparedStatement.setInt(2, flight.getAircraft().getId());
            preparedStatement.setInt(3, flight.getAirportDeparture().getId());
            preparedStatement.setInt(4, flight.getAirportArrival().getId());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(flight.getDepartureTime()));

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            flight.setId(generatedKeys.getLong("id"));
            return flight;
        }
    }

    @Override
    @SneakyThrows
    public void update(Flight flight) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setLong(1, flight.getAircrew().getId());
            preparedStatement.setInt(2, flight.getAircraft().getId());
            preparedStatement.setInt(3, flight.getAirportDeparture().getId());
            preparedStatement.setInt(4, flight.getAirportArrival().getId());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(flight.getDepartureTime()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(flight.getArrivalTime()));

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            Flight flight = null;
            if (resultSet.next()) {
                flight = buildFlight(resultSet);
            }
            return Optional.ofNullable(flight);
        }
    }

    @Override
    @SneakyThrows
    public List<Flight> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Flight> flights = new ArrayList<>();
            while (resultSet.next()) {
                flights.add(buildFlight(resultSet));
            }
            return flights;
        }
    }

    @SneakyThrows
    private Flight buildFlight(ResultSet resultSet) {
        return new Flight(
                resultSet.getLong("id"),
                aircrewDao.findById(resultSet.getLong("aircrew_id")).orElse(null),
                aircraftDao.findById(resultSet.getInt("aircraft_id")).orElse(null),
                airportDao.findById(resultSet.getInt("airport_departure_id")).orElse(null),
                airportDao.findById(resultSet.getInt("airport_arrival_id")).orElse(null),
                resultSet.getTimestamp("departure_time").toLocalDateTime(),
                resultSet.getTimestamp("arrival_time").toLocalDateTime()
        );
    }

    public static FlightDao getInstance() {
        return INSTANCE;
    }
}
