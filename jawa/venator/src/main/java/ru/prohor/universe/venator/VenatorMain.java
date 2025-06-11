package ru.prohor.universe.venator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VenatorMain {
    public static void main(String[] args) {
        SpringApplication.run(VenatorMain.class, args);
    }
}
