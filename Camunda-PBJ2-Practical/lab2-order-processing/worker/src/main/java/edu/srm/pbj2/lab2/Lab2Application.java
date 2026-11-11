package edu.srm.pbj2.lab2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab 2 - E-commerce order processing.
 * Hosts the Camunda 8 job workers (validate, inventory, total, fulfil) and a mock payment gateway
 * that the REST Connector in order-fulfilment.bpmn calls. Synthetic data only.
 */
@SpringBootApplication
public class Lab2Application {
    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class, args);
    }
}
