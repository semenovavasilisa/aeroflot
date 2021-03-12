package com.vasilisa.aeroflot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Aircrew {
    private Long id;
    private List<Employee> employees;
}
