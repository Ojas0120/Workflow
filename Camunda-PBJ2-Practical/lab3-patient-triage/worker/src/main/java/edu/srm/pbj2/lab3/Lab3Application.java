package edu.srm.pbj2.lab3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lab 3 - Hospital appointment and patient triage (EDUCATIONAL SIMULATION - synthetic patients only,
 * not a clinical tool). Hosts the Camunda 8 job workers and a mock hospital scheduling API that the
 * REST Connector in patient-triage.bpmn calls.
 */
@SpringBootApplication
public class Lab3Application {
    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }
}
