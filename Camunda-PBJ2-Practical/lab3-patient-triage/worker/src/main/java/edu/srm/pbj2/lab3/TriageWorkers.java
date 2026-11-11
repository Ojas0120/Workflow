package edu.srm.pbj2.lab3;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * External job workers for patient-triage.bpmn.
 * EDUCATIONAL SIMULATION ONLY - synthetic patient references, no real patient data, not clinical advice.
 */
@Component
public class TriageWorkers {

    private static final Logger log = LoggerFactory.getLogger(TriageWorkers.class);
    private static final Pattern PATIENT_REF = Pattern.compile("PT-SYN-\\d{4}");
    private static final Set<String> DEPARTMENTS = Set.of("General Medicine", "Dermatology");
    private static final Set<String> SEVERITY = Set.of("Mild", "Moderate", "Severe");
    private static final Set<String> AGE_GROUP = Set.of("Child", "Adult", "Senior");
    private static final Set<String> TIME_OF_DAY = Set.of("Any", "Morning", "Afternoon");

    private final SchedulingService scheduling;

    public TriageWorkers(SchedulingService scheduling) {
        this.scheduling = scheduling;
    }

    /**
     * Job type: triage-validate-registration.
     * Validates registration fields and prepares the availability request (searchFrom / timeOfDay) that the
     * REST Connector uses to query the scheduling API.
     */
    @JobWorker(type = "triage-validate-registration")
    public Map<String, Object> validateRegistration(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final List<String> errors = new ArrayList<>();

        final String patientRef = text(in.get("patientRef"));
        final String department = text(in.get("department"));
        final String preferredDate = text(in.get("preferredDate"));
        final String symptoms = text(in.get("symptomsSummary"));
        final String severity = text(in.get("severity"));
        final String ageGroup = text(in.get("ageGroup"));
        final String timeOfDay = text(in.get("timeOfDay")).isBlank() ? "Any" : text(in.get("timeOfDay"));
        final Double days = in.get("symptomDays") instanceof Number n ? n.doubleValue() : null;

        if (!PATIENT_REF.matcher(patientRef).matches()) errors.add("patientRef must be a synthetic reference like PT-SYN-0001");
        if (!DEPARTMENTS.contains(department)) errors.add("unknown department");
        try {
            if (LocalDate.parse(preferredDate).isBefore(LocalDate.now())) errors.add("preferredDate must not be in the past");
        } catch (DateTimeParseException e) {
            errors.add("preferredDate must be YYYY-MM-DD");
        }
        if (symptoms.isBlank() || symptoms.length() > 500) errors.add("symptomsSummary is required (max 500 characters)");
        if (!SEVERITY.contains(severity)) errors.add("severity must be Mild, Moderate or Severe");
        if (!AGE_GROUP.contains(ageGroup)) errors.add("ageGroup must be Child, Adult or Senior");
        if (!TIME_OF_DAY.contains(timeOfDay)) errors.add("timeOfDay must be Any, Morning or Afternoon");
        if (days == null || days < 0) errors.add("symptomDays must be 0 or more");

        final Map<String, Object> out = new HashMap<>();
        out.put("validationPassed", errors.isEmpty());
        out.put("validationErrors", errors);
        // Normalise values the DMN and the connector query rely on (checkboxes may be absent when unticked).
        out.put("redFlagBreathing", Boolean.TRUE.equals(in.get("redFlagBreathing")));
        out.put("redFlagChestPain", Boolean.TRUE.equals(in.get("redFlagChestPain")));
        out.put("timeOfDay", timeOfDay);
        out.put("searchFrom", preferredDate);
        log.info("[validate] patient={} dept={} -> passed={}", patientRef, department, errors.isEmpty());
        return out;
    }

    /** Job type: triage-book-appointment - books the first slot returned by the scheduling API. */
    @JobWorker(type = "triage-book-appointment")
    @SuppressWarnings("unchecked")
    public Map<String, Object> bookAppointment(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final List<Map<String, Object>> slots = (List<Map<String, Object>>) in.get("availableSlots");
        final Map<String, Object> out = new HashMap<>();
        for (Map<String, Object> slot : slots) {
            final String slotId = String.valueOf(slot.get("slotId"));
            if (scheduling.book(slotId)) {
                out.put("bookingStatus", "CONFIRMED");
                out.put("bookingId", "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                out.put("bookedSlot", slot.get("start"));
                log.info("[book] patient={} slot={}", in.get("patientRef"), slot.get("start"));
                return out;
            }
        }
        // Every returned slot was taken in the meantime: fail the job so it shows up as an incident for staff to handle.
        throw new IllegalStateException("None of the offered slots could be booked (already taken)");
    }

    private static String text(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }
}
