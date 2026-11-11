package edu.srm.pbj2.lab3;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Fault-injection endpoints for the incident / recovery demo.
 *   POST /admin/outage/scheduling?enabled=true -> scheduling API answers HTTP 503
 *   POST /admin/outage/scheduling?enabled=false -> scheduling API works again ("corrective action")
 * Educational use only: these endpoints are unauthenticated (local machine only).
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final OutageSwitch outage;

    public AdminController(OutageSwitch outage) {
        this.outage = outage;
    }

    @PostMapping("/outage/{service}")
    public Map<String, Object> setOutage(@PathVariable("service") String service,
                                         @RequestParam("enabled") boolean enabled) {
        outage.set(service, enabled);
        return Map.of("service", service, "down", enabled);
    }

    @GetMapping("/outage")
    public Map<String, Boolean> outages() {
        return outage.snapshot();
    }
}
