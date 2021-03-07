package com.vasilisa.aeroflot.entity;

import lombok.Data;

@Data
public class Aircraft {
    private Integer id;
    private String name;
    private AircraftModel model;
    private Integer capacity;
    private Boolean technicalFault;

}
