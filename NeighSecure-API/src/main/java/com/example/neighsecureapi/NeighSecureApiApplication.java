package com.example.neighsecureapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class NeighSecureApiApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/El_Salvador"));

        SpringApplication.run(NeighSecureApiApplication.class, args);
    }

}
