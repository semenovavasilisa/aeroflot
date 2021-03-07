package com.vasilisa.aeroflot.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Airport {
    private Integer id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String country;
    private Boolean open;
}
