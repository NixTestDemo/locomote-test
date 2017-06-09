package com.airlines.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Airport {
    String airportCode;
    String airportName;
    String countryCode;
    String countryName;
}
