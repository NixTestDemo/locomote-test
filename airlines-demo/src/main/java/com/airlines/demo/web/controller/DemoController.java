package com.airlines.demo.web.controller;

import com.airlines.demo.dto.Airline;
import com.airlines.demo.dto.Airport;
import com.airlines.demo.dto.Flight;
import com.airlines.demo.service.LocomoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class DemoController {

    @Autowired
    private LocomoteService locomoteService;


    @GetMapping("/airlines")
    public List<Airline> getAirlines() {
        return locomoteService.getAirlines();
    }

    @GetMapping("/airports")
    public List<Airport> getAirports(@RequestParam String city) {
        return locomoteService.getAirports(city);
    }

    @GetMapping("/search")
    public Map<String, List<Flight>> getFlights(@RequestParam String from,
                                                @RequestParam String to,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date)  {
        return locomoteService.getFlights(from, to, date);
    }

}
