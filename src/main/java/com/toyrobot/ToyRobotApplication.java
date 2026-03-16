package com.toyrobot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ToyRobotApplication {

    public static void main(String[] args) {
        log.info("Starting Toy Robot Simulator...");

        SpringApplication.run(ToyRobotApplication.class, args);

        log.info("Toy Robot Simulator shutdown complete");
    }
}
