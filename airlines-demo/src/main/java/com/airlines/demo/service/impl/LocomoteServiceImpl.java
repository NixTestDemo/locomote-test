package com.airlines.demo.service.impl;

import com.airlines.demo.dto.Airline;
import com.airlines.demo.dto.Airport;
import com.airlines.demo.dto.Flight;
import com.airlines.demo.service.LocomoteService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Log4j
public class LocomoteServiceImpl implements LocomoteService {

    public static final String DELIMETER = "/";
    public static final String SEARCH_URL_PATTERN = "%s/%s?date=%s&from=%s&to=%s";
    @Value("${locomote.url}")
    private String locomoteUrl;

    @Value("${locomote.airlines.url}")
    private String airlinesUrl;

    @Value("${locomote.airports.url}")
    private String airportsUrl;

    @Value("${locomote.flights.url}")
    private String flightUrl;

    @Override
    public List<Airline> getAirlines() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Airline[]> response = restTemplate.getForEntity(locomoteUrl + DELIMETER + airlinesUrl, Airline[].class);

        return Arrays.asList(response.getBody());
    }

    @Override
    public List<Airport> getAirports(String city) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Airport[]> response = restTemplate.getForEntity(locomoteUrl + DELIMETER + airportsUrl + city, Airport[].class);

        return Arrays.asList(response.getBody());
    }

    @Override
    public Map<String, List<Flight>> getFlights(String from, String to, LocalDate date) {
        List<Airline> airlines = getAirlines();

        List<Airport> fromAirports = getAirports(from);
        List<Airport> toAirports = getAirports(to);

        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        String url = locomoteUrl + DELIMETER + flightUrl;

        Map<String, List<ListenableFuture<ResponseEntity<Flight[]>>>> futures = new HashMap<>();
        Map<String, List<Flight>> flights = new TreeMap<>();

		LocalDate now = LocalDate.now();
        for (int i = -2; i <= 2; i++) {
            LocalDate searchDate = date.plusDays(i);
			if (searchDate.isBefore(now)) {
				futures.put(searchDate.toString(), null);
				continue;
			}
            for (Airline airline : airlines) {
                for (Airport fromAirport : fromAirports) {
                    for (Airport toAirport : toAirports) {
                        String searchUrl = String.format(SEARCH_URL_PATTERN, url, airline.getCode(), searchDate.toString(), fromAirport.getAirportCode(), toAirport.getAirportCode());
                        ListenableFuture<ResponseEntity<Flight[]>> response = restTemplate.getForEntity(searchUrl, Flight[].class);

                        futures.compute(searchDate.toString(), (key, oldValue) -> addFutureToMap(response, oldValue));

                        try {
                            TimeUnit.MILLISECONDS.sleep(20); // added to avoid 503 (Service Temporarily Unavailable)
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, List<ListenableFuture<ResponseEntity<Flight[]>>>> entry : futures.entrySet()) {
			if (entry.getValue() == null) {
				flights.put(entry.getKey(), new ArrayList<>());
				continue;
			}
        	for (ListenableFuture future : entry.getValue()) {
                try {
                    ResponseEntity<Flight[]> responseEntity = (ResponseEntity<Flight[]>) future.get();
                    flights.compute(entry.getKey(),
                            (key, oldValue) -> addFlights(oldValue, Arrays.asList(responseEntity.getBody())));
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage());
                }
            }
        }

        return flights;
    }

    private List<Flight> addFlights(List<Flight> oldValue, List<Flight> flights) {
        if (oldValue == null) {
            oldValue = new ArrayList<>();
        }
        oldValue.addAll(flights);
        return oldValue;
    }

    private List<ListenableFuture<ResponseEntity<Flight[]>>> addFutureToMap(ListenableFuture response, List oldValue) {
        if (oldValue == null) {
            oldValue = new ArrayList<>();
        }
        oldValue.add(response);
        return oldValue;
    }

}
