# Lab 3 - Process variable dictionary (synthetic data only)

| Variable | Type | Set by | Purpose |
|---|---|---|---|
| patientRef | string | Start form | Synthetic reference PT-SYN-nnnn (no real patient identifiers) |
| department | string | Start form | General Medicine or Dermatology |
| preferredDate | string YYYY-MM-DD | Start form | Appointment preference; must not be in the past |
| timeOfDay | string | Start form / worker | Any / Morning / Afternoon (defaults to Any) |
| symptomsSummary | string | Start form | Synthetic free-text summary (max 500 chars) |
| severity | string | Start form | Mild / Moderate / Severe (DMN input) |
| symptomDays | number | Start form | Symptom duration in days (DMN input) |
| ageGroup | string | Start form | Child / Adult / Senior (DMN input) |
| redFlagBreathing | boolean | Start form, normalised by worker | Urgency indicator: breathing difficulty (DMN input) |
| redFlagChestPain | boolean | Start form, normalised by worker | Urgency indicator: chest pain (DMN input) |
| validationPassed | boolean | Worker triage-validate-registration | Gateway 'Registration valid?' |
| validationErrors | list of string | Worker | Validation problems |
| searchFrom | string YYYY-MM-DD | Worker | Prepared availability-search start date |
| triageResult | object {triageCategory, rationale} | DMN triage_category | Routine / Priority Review / Urgent Assessment |
| priorityReviewOutcome | string | Priority review form | Proceed to booking / Refer elsewhere |
| assessmentNotes | string | Urgent assessment form | Synthetic clinician notes |
| slotsResponse | object | REST Connector | Full HTTP response from the scheduling API |
| availableSlots | list of object | REST Connector | Up to 5 slots {slotId, start, clinic, slotType} |
| slotCount | number | REST Connector | Number of slots; gateway 'Slots available?' |
| bookingStatus | string | Worker triage-book-appointment | CONFIRMED |
| bookingId | string | Worker | Simulated booking id |
| bookedSlot | string | Worker | ISO date-time of the booked slot |
| staffAction | string | No-slot review form | Manual slot arranged / Added to waitlist |
| staffNote | string | No-slot review form | Optional note |
