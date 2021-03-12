package com.vasilisa.aeroflot.dao;

import com.vasilisa.aeroflot.entity.Aircraft;
import com.vasilisa.aeroflot.entity.AircraftModel;
import com.vasilisa.aeroflot.entity.Employee;
import com.vasilisa.aeroflot.entity.Position;
import com.vasilisa.aeroflot.util.ConnectionManager;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDao implements Dao<Integer, Employee> {
    private static final EmployeeDao INSTANCE = new EmployeeDao();
    private static final String DELETE_SQL = """
            DELETE FROM employee
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO employee (name, position, work_start, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE employee
            SET name = ?,
            position = ?,
            work_start = ?,
            birthday = ?
            WHERE id = ?
            """;
    private static final String FIND_ALL_SQL = """
                 SELECT id,
            name,
            position,
            work_start,
            birthday
            FROM employee
            """;
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?""";
    private static final String FIND_BY_AIRCREW_ID_SQL = """
            SELECT employee.id,
            employee.name,
            employee.position,
            employee.work_start,
            employee.birthday
            FROM employee, employee_aircrew
            WHERE employee.id = employee_aircrew.employee_id
            AND employee_aircrew.aircrew_id = ?
                        """;

    private EmployeeDao() {
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
    public Employee save(Employee employee) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getPosition().name());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(employee.getWorkStart().atStartOfDay()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(employee.getBirthday().atStartOfDay()));

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            employee.setId(generatedKeys.getInt("id"));
            return employee;
        }
    }

    @Override
    @SneakyThrows
    public void update(Employee employee) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setString(2, employee.getPosition().name());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(employee.getWorkStart().atStartOfDay()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(employee.getBirthday().atStartOfDay()));
            preparedStatement.setInt(5, employee.getId());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public Optional<Employee> findById(Integer id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
            Employee employee = null;
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        Position.valueOf(resultSet.getString("position")),
                        resultSet.getTimestamp("work_start").toLocalDateTime().toLocalDate(),
                        resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate()
                );
            }
            return Optional.ofNullable(employee);
        }
    }

    @Override
    @SneakyThrows
    public List<Employee> findAll() {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            List<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                employees.add(buildEmployee(resultSet));
            }
            return employees;
        }
    }

    @SneakyThrows
    public List<Employee> findByAircrewId(Long id) {
        try (var connection = ConnectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_AIRCREW_ID_SQL)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            List<Employee> employees = new ArrayList<>();
            while (resultSet.next()) {
                employees.add(buildEmployee(resultSet));
            }
            return employees;
        }
    }

    @SneakyThrows
    private Employee buildEmployee(ResultSet resultSet) {
        return new Employee(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                Position.valueOf(resultSet.getString("position")),
                resultSet.getTimestamp("work_start").toLocalDateTime().toLocalDate(),
                resultSet.getTimestamp("birthday").toLocalDateTime().toLocalDate()
        );
    }

    public static EmployeeDao getInstance() {
        return INSTANCE;
    }
}
