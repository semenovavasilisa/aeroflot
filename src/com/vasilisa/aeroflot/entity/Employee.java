package com.vasilisa.aeroflot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Employee {
    private Integer id;
    private String name;
    private Position position;
    private LocalDate workStart;
    private LocalDate birthday;
}
