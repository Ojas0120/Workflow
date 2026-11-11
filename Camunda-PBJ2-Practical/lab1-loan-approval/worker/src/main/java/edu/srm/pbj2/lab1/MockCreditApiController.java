package edu.srm.pbj2.lab1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mock credit-assessment API (synthetic data only). Called by the REST Connector in loan-approval.bpmn.
 *
 * Deterministic test hooks (by applicantId):
 *   APP-1001 -> score 780   APP-1002 -> score 520   APP-1003 -> score 650
 *   APP-FAIL-* -> HTTP 503 (simulated outage)
 *   APP-SLOW-* -> responds after 8 s (the connector read timeout is 5 s)
 *   anything else -> pseudo-random but repeatable score 500-849
 */
@RestController
@RequestMapping("/api")
public class MockCreditApiController {

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("status", "UP", "service", "mock-credit-api");
    }

    @PostMapping("/credit/assess")
    public ResponseEntity<Map<String, Object>> assess(@RequestBody Map<String, Object> request) throws InterruptedException {
        final String applicantId = String.valueOf(request.getOrDefault("applicantId", ""));

        if (applicantId.startsWith("APP-FAIL")) {
            final Map<String, Object> err = new LinkedHashMap<>();
            err.put("error", "SERVICE_UNAVAILABLE");
            err.put("message", "Credit bureau temporarily unavailable (simulated)");
            return ResponseEntity.status(503).body(err);
        }
        if (applicantId.startsWith("APP-SLOW")) {
            Thread.sleep(8_000);
        }

        final int score = switch (applicantId) {
            case "APP-1001" -> 780;
            case "APP-1002" -> 520;
            case "APP-1003" -> 650;
            default -> 500 + Math.floorMod(applicantId.hashCode(), 350);
        };
        final String band = score >= 750 ? "LOW" : score >= 650 ? "MEDIUM" : score >= 550 ? "HIGH" : "VERY_HIGH";

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("applicantId", applicantId);
        body.put("creditScore", score);
        body.put("riskBand", band);
        body.put("reference", "CR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        body.put("assessedAt", Instant.now().toString());
        return ResponseEntity.ok(body);
    }
}
