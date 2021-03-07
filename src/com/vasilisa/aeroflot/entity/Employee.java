package com.vasilisa.aeroflot.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Employee {
    private Integer id;
    private String name;
    private String position;
    private LocalDate workStart;
    private LocalDate birthday;
}
