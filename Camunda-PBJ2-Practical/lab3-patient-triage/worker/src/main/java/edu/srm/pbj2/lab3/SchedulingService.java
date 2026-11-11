package edu.srm.pbj2.lab3;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simulated hospital scheduling back-end. Shared by the mock REST API (slot search for the REST Connector)
 * and by the booking worker, so both see the same booked-slot state.
 *
 * General Medicine: ROUTINE slots 09:00, 11:00, 14:00, 16:00 and PRIORITY slots 08:30, 13:30 on each of the
 * 5 days starting at the requested date. Dermatology has no slots at all (used for the "no slots" scenario).
 */
@Component
public class SchedulingService {

    private final Set<String> booked = new HashSet<>();
    private final OutageSwitch outage;

    public SchedulingService(OutageSwitch outage) {
        this.outage = outage;
    }

    public static class ServiceUnavailableException extends RuntimeException {
        public ServiceUnavailableException(String msg) {
            super(msg);
        }
    }

    public synchronized List<Map<String, Object>> findSlots(String department, LocalDate from, String slotType, String timeOfDay) {
        if (outage.isDown("scheduling")) {
            throw new ServiceUnavailableException("Scheduling service unavailable (simulated outage)");
        }
        final List<Map<String, Object>> slots = new ArrayList<>();
        if (!"General Medicine".equals(department)) {
            return slots;
        }
        final List<String> times = "PRIORITY".equals(slotType)
                ? List.of("08:30", "13:30")
                : List.of("09:00", "11:00", "14:00", "16:00");
        for (int d = 0; d < 5; d++) {
            final LocalDate date = from.plusDays(d);
            for (String t : times) {
                final boolean morning = LocalTime.parse(t).isBefore(LocalTime.NOON);
                if ("Morning".equals(timeOfDay) && !morning) continue;
                if ("Afternoon".equals(timeOfDay) && morning) continue;
                final String slotId = department.replace(' ', '-') + "|" + date + "T" + t + "|" + slotType;
                if (booked.contains(slotId)) continue;
                final Map<String, Object> slot = new LinkedHashMap<>();
                slot.put("slotId", slotId);
                slot.put("start", date + "T" + t);
                slot.put("clinic", department + " Clinic A");
                slot.put("slotType", slotType);
                slots.add(slot);
                if (slots.size() == 5) return slots;
            }
        }
        return slots;
    }

    /** @return true if booked, false if the slot was already taken */
    public synchronized boolean book(String slotId) {
        if (outage.isDown("scheduling")) {
            throw new ServiceUnavailableException("Scheduling service unavailable (simulated outage)");
        }
        return booked.add(slotId);
    }
}
