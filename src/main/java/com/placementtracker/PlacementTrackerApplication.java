package com.placementtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlacementTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlacementTrackerApplication.class, args);
    }
}