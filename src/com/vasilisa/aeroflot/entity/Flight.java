package com.vasilisa.aeroflot.entity;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Flight {
    private Long id;
    private Aircrew aircrew;
    private Airport airportDeparture;
    private Airport airportArrival;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;
}
