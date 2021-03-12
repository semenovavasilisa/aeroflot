package com.vasilisa.aeroflot.dao;

import com.vasilisa.aeroflot.entity.Aircrew;
import com.vasilisa.aeroflot.util.ConnectionManager;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AircrewDao implements Dao<Long, Aircrew> {
    private static final AircrewDao INSTANCE = new AircrewDao();
    private  final EmployeeDao employeeDao = EmployeeDao.getInstance();
    private static final String DELETE_SQL = """
            DELETE FROM aircrew
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO aircrew 
            DEFAULT VALUES 
            """;
    private static final String UPDATE_SQL = """
             UPDATE aircrew
             SET id = ?
             WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id
            FROM aircrew
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id
            FROM aircrew
            WHERE id = ?
            """;

    private AircrewDao() {
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
    public Aircrew save(Aircrew aircrew) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setLong(1, aircrew.getId());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            aircrew.setId(generatedKeys.getLong("id"));
            aircrew.setEmployees(employeeDao.findByAircrewId(aircrew.getId()));
            return aircrew;
        }
    }

    @Override
    public void update(Aircrew aircrew) {

    }


    @SneakyThrows
    public void update(Aircrew aircrew, Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setLong(1, aircrew.getId());
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public Optional<Aircrew> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            Aircrew aircrew = null;
            if (resultSet.next()) {
                aircrew = new Aircrew(
                        resultSet.getLong("id"),
                        employeeDao.findByAircrewId(id)
                );
            }
            return Optional.ofNullable(aircrew);
        }
    }

    @Override
    @SneakyThrows
    public List<Aircrew> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Aircrew> aircrewList = new ArrayList<>();
            while (resultSet.next()) {
                aircrewList.add(new Aircrew(resultSet.getLong("id"),
                        employeeDao.findByAircrewId(resultSet.getLong("id"))));
            }
            return aircrewList;
        }
    }

    public static AircrewDao getInstance() {
        return INSTANCE;
    }
}
