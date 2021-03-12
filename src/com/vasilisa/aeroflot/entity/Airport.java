package com.vasilisa.aeroflot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Airport {
    private Integer id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String country;
    private boolean open;
}
