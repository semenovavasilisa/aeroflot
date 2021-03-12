package com.vasilisa.aeroflot.dao;

import com.vasilisa.aeroflot.entity.Aircraft;
import com.vasilisa.aeroflot.entity.AircraftModel;
import com.vasilisa.aeroflot.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AircraftDao implements Dao<Integer, Aircraft> {

    private static final AircraftDao INSTANCE = new AircraftDao();
    private static final String DELETE_SQL = """
            DELETE FROM aircraft
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO aircraft (name, model, capacity, technical_fault)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE aircraft
            SET name = ?,
            model = ?,
            capacity = ?,
            technical_fault = ?
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
                 SELECT id,
            name,
            model,
            capacity,
            technical_fault
            FROM aircraft
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ? """;

    private AircraftDao() {
    }

    @SneakyThrows
    public List<Aircraft> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Aircraft> aircraftList = new ArrayList<>();
            while (resultSet.next()) {
                aircraftList.add(buildAircraft(resultSet));
            }
            return aircraftList;
        }
    }

    @SneakyThrows
    public Optional<Aircraft> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Aircraft aircraft = null;
            if (resultSet.next()) {
                aircraft = buildAircraft(resultSet);
            }
            return Optional.ofNullable(aircraft);
        }
    }

    @SneakyThrows
    public void update(Aircraft aircraft) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, aircraft.getName());
            preparedStatement.setString(2, aircraft.getModel().name());
            preparedStatement.setInt(3, aircraft.getCapacity());
            preparedStatement.setBoolean(4, aircraft.isTechnicalFault());
            preparedStatement.setInt(5, aircraft.getId());

            preparedStatement.executeUpdate();
        }
    }

    @SneakyThrows
    public Aircraft save(Aircraft aircraft) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setString(1, aircraft.getName());
            preparedStatement.setString(2, aircraft.getModel().name());
            preparedStatement.setInt(3, aircraft.getCapacity());
            preparedStatement.setBoolean(4, aircraft.isTechnicalFault());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            aircraft.setId(generatedKeys.getInt("id"));
            return aircraft;
        }
    }

    @SneakyThrows
    public boolean delete(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setInt(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @SneakyThrows
    private Aircraft buildAircraft(ResultSet resultSet) {
        return new Aircraft(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                AircraftModel.valueOf(resultSet.getString("model")),
                resultSet.getInt("capacity"),
                resultSet.getBoolean("technical_fault")
        );
    }

    public static AircraftDao getInstance() {
        return INSTANCE;
    }
}
