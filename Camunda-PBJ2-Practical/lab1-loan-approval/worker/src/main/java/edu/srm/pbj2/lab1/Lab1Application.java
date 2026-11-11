package edu.srm.pbj2.lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab 1 - Loan approval.
 * One Spring Boot process hosts:
 *   1. the Camunda 8 job workers (loan-validate-applicant, loan-notify), and
 *   2. a mock credit-assessment REST API that the REST Connector in the BPMN calls.
 */
@SpringBootApplication
public class Lab1Application {
    public static void main(String[] args) {
        SpringApplication.run(Lab1Application.class, args);
    }
}
