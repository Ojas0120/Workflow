package edu.srm.pbj2.lab1;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * External job workers for the loan-approval BPMN process.
 * Returning a Map completes the job and merges the map into the process variables.
 * Throwing an exception fails the job (retries - 1); when retries reach 0 Zeebe raises an incident.
 */
@Component
public class LoanWorkers {

    private static final Logger log = LoggerFactory.getLogger(LoanWorkers.class);

    /** Job type: loan-validate-applicant */
    @JobWorker(type = "loan-validate-applicant")
    public Map<String, Object> validateApplicant(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        log.info("[validate] job {} for instance {} applicant={}", job.getKey(), job.getProcessInstanceKey(), in.get("applicantId"));

        // Deliberate fault injection for the incident / recovery scenario (Scenario 5).
        // Fix in Operate: edit variable simulateWorkerFailure to false, then click "Retry" on the incident.
        if (Boolean.TRUE.equals(in.get("simulateWorkerFailure"))) {
            throw new IllegalStateException("Simulated worker failure (simulateWorkerFailure=true) for applicant "
                    + in.get("applicantId"));
        }

        final List<String> errors = new ArrayList<>();
        final String applicantId = text(in.get("applicantId"));
        final String applicantName = text(in.get("applicantName"));
        final Double age = number(in.get("age"));
        final Double amount = number(in.get("requestedAmount"));
        final Double term = number(in.get("loanTermMonths"));
        final Double income = number(in.get("monthlyIncome"));
        final Double debt = number(in.get("existingMonthlyDebt"));
        final Double years = number(in.get("employmentYears"));

        if (applicantId.isBlank()) errors.add("applicantId is required");
        if (applicantName.isBlank()) errors.add("applicantName is required");
        if (age == null || age < 18 || age > 70) errors.add("age must be between 18 and 70");
        if (amount == null || amount < 10_000 || amount > 5_000_000) errors.add("requestedAmount must be between 10,000 and 5,000,000");
        if (term == null || term < 6 || term > 360) errors.add("loanTermMonths must be between 6 and 360");
        if (income == null || income <= 0) errors.add("monthlyIncome must be greater than 0");
        if (debt == null || debt < 0) errors.add("existingMonthlyDebt must be 0 or more");
        if (years == null || years < 0) errors.add("employmentYears must be 0 or more");

        final Map<String, Object> out = new HashMap<>();
        out.put("validationPassed", errors.isEmpty());
        out.put("validationErrors", errors);
        // Normalise for the DMN so the decision never sees a missing variable.
        out.put("simulateWorkerFailure", false);

        if (errors.isEmpty()) {
            final double instalment = amount / term;                       // simple, interest-free educational estimate
            final double dti = (debt + instalment) / income;               // (existing debt + new instalment) / monthly income
            final double loanToIncome = amount / (income * 12.0);          // requested amount / annual income
            out.put("estimatedInstalment", round(instalment, 2));
            out.put("dtiRatio", round(dti, 4));
            out.put("loanToIncomeRatio", round(loanToIncome, 4));
        }
        return out;
    }

    /** Job type: loan-notify  (the BPMN passes the input variable "outcome" = APPROVED | REJECTED) */
    @JobWorker(type = "loan-notify")
    public Map<String, Object> notifyApplicant(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final String outcome = text(in.get("outcome"));
        final String notificationId = "NTF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("[notify] applicant={} outcome={} notificationId={} (simulated - nothing is actually sent)",
                in.get("applicantId"), outcome, notificationId);
        final Map<String, Object> out = new HashMap<>();
        out.put("finalStatus", outcome);
        out.put("notificationId", notificationId);
        out.put("decidedAt", Instant.now().toString());
        return out;
    }

    // ---- helpers ----
    private static String text(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }

    private static Double number(Object o) {
        return (o instanceof Number n) ? n.doubleValue() : null;
    }

    private static double round(double v, int places) {
        final double f = Math.pow(10, places);
        return Math.round(v * f) / f;
    }
}
