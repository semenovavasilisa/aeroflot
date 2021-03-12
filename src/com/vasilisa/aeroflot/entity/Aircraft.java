package com.vasilisa.aeroflot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Aircraft {
    private Integer id;
    private String name;
    private AircraftModel model;
    private Integer capacity;
    private boolean technicalFault;
}
