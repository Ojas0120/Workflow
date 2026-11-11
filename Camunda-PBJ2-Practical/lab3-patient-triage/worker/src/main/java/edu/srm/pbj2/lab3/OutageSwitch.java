package edu.srm.pbj2.lab3;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory fault-injection switches used to simulate a failing downstream service. */
@Component
public class OutageSwitch {
    private final Map<String, Boolean> state = new ConcurrentHashMap<>();

    public boolean isDown(String service) {
        return Boolean.TRUE.equals(state.get(service));
    }

    public void set(String service, boolean down) {
        state.put(service, down);
    }

    public Map<String, Boolean> snapshot() {
        return Map.copyOf(state);
    }
}
