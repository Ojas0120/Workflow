# Lab 3 - Test-case table

Use a future `preferredDate` (edit the JSON if 2026-11-10 has passed). Fill **Actual outcome / Status** from your own runs.

| # | Scenario | Input file | Expected route | Expected end | Actual outcome | Status |
|---|---|---|---|---|---|---|
| 1 | Routine request with slot | t1-routine-with-slot.json | DMN R6 Routine -> slots (>0) -> book first slot | Appointment booked |  |  |
| 2 | Priority staff review | t2-priority-review.json | DMN R3 -> Tasklist priority review -> Proceed -> PRIORITY slots -> book | Appointment booked (or Referred if reviewer refers) |  |  |
| 3 | Urgent assessment | t3-urgent-assessment.json | DMN R1 -> Tasklist immediate assessment | Urgent assessment handed to clinician (simulated) |  |  |
| 4 | No slots available | t4-no-slots.json | Routine -> Dermatology returns 0 slots -> Tasklist no-slot review | Handled by staff |  |  |
| 5 | Scheduling API fails, recover | t5-scheduling-outage.json | Enable outage -> connector 503 x3 -> incident -> disable outage -> Retry -> slots -> book | Appointment booked |  |  |
| 6 | Invalid registration | t6-invalid-registration.json | patientRef not PT-SYN-nnnn | Registration invalid |  |  |
