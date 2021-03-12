package com.vasilisa.aeroflot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Flight {
    private Long id;
    private Aircrew aircrew;
    private Aircraft aircraft;
    private Airport airportDeparture;
    private Airport airportArrival;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
}
