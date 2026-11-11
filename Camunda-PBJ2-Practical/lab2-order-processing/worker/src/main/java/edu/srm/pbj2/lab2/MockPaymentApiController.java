package edu.srm.pbj2.lab2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Mock payment gateway (no real payment data is ever processed).
 *   paymentToken "tok_test_ok"       -> 200 {"status":"APPROVED", ...}
 *   paymentToken "tok_test_declined" -> 200 {"status":"DECLINED", ...}
 *   any other token                  -> 200 {"status":"DECLINED", "message":"Unknown test token"}
 *   outage switch "payment" enabled  -> HTTP 503 (connector fails -> retries -> incident in Operate)
 */
@RestController
@RequestMapping("/api")
public class MockPaymentApiController {

    private final OutageSwitch outage;

    public MockPaymentApiController(OutageSwitch outage) {
        this.outage = outage;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("status", "UP", "service", "mock-payment-api");
    }

    @PostMapping("/payments/charge")
    public ResponseEntity<Map<String, Object>> charge(@RequestBody Map<String, Object> request) {
        final Map<String, Object> body = new LinkedHashMap<>();

        if (outage.isDown("payment")) {
            body.put("error", "SERVICE_UNAVAILABLE");
            body.put("message", "Payment gateway unavailable (simulated outage)");
            return ResponseEntity.status(503).body(body);
        }

        final String token = String.valueOf(request.getOrDefault("paymentToken", ""));
        body.put("orderRef", request.get("orderRef"));
        body.put("amount", request.get("amount"));
        body.put("currency", request.getOrDefault("currency", "INR"));

        if ("tok_test_ok".equals(token)) {
            body.put("status", "APPROVED");
            body.put("transactionId", "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            body.put("message", "Payment approved (simulated)");
        } else if ("tok_test_declined".equals(token)) {
            body.put("status", "DECLINED");
            body.put("transactionId", null);
            body.put("message", "Card declined (simulated)");
        } else {
            body.put("status", "DECLINED");
            body.put("transactionId", null);
            body.put("message", "Unknown test token");
        }
        return ResponseEntity.ok(body);
    }
}
