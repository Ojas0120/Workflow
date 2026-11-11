package edu.srm.pbj2.lab3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock hospital scheduling API (synthetic data only).
 *   GET /api/scheduling/slots?department=General Medicine&from=2026-11-02&slotType=ROUTINE&timeOfDay=Any
 * Returns 503 while the "scheduling" outage switch is on (see AdminController).
 */
@RestController
@RequestMapping("/api")
public class MockSchedulingApiController {

    private final SchedulingService scheduling;

    public MockSchedulingApiController(SchedulingService scheduling) {
        this.scheduling = scheduling;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("status", "UP", "service", "mock-scheduling-api");
    }

    @GetMapping("/scheduling/slots")
    public ResponseEntity<Map<String, Object>> slots(@RequestParam("department") String department,
                                                     @RequestParam("from") String from,
                                                     @RequestParam(value = "slotType", defaultValue = "ROUTINE") String slotType,
                                                     @RequestParam(value = "timeOfDay", defaultValue = "Any") String timeOfDay) {
        final Map<String, Object> body = new LinkedHashMap<>();
        try {
            final List<Map<String, Object>> slots = scheduling.findSlots(department, LocalDate.parse(from), slotType, timeOfDay);
            body.put("department", department);
            body.put("count", slots.size());
            body.put("slots", slots);
            return ResponseEntity.ok(body);
        } catch (SchedulingService.ServiceUnavailableException e) {
            body.put("error", "SERVICE_UNAVAILABLE");
            body.put("message", e.getMessage());
            return ResponseEntity.status(503).body(body);
        }
    }
}
