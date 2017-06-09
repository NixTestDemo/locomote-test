package com.airlines.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Flight {

    String flightNum;

    Airline airline;

    Integer durationMin;

    Destination start;

    Destination finish;

    Integer price;

}
