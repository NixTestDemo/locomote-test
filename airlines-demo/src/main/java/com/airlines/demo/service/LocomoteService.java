package com.airlines.demo.service;

import com.airlines.demo.dto.Airline;
import com.airlines.demo.dto.Airport;
import com.airlines.demo.dto.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LocomoteService {

    List<Airline> getAirlines();

    List<Airport> getAirports(String city);

    Map<String, List<Flight>> getFlights(String from, String to, LocalDate date);

}
