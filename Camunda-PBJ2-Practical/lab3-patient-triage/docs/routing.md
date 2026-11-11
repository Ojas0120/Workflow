# Lab 3 - Routing explanation

| Gateway | Condition | Path |
|---|---|---|
| Registration valid? | `validationPassed = true` | Yes -> DMN; default -> *Registration invalid* |
| Triage category? | `triageResult.triageCategory = "Urgent Assessment"` | -> Tasklist *Immediate clinical assessment* (human) -> end |
| | `... = "Priority Review"` | -> Tasklist *Priority staff review* -> Review outcome gateway |
| | default | Routine -> get slots |
| Review outcome? | `priorityReviewOutcome = "Proceed to booking"` | -> get slots (PRIORITY slot type); default -> *Referred - no booking* |
| Slots available? | `slotCount > 0` | Yes -> book first slot; default -> Tasklist *Staff review: no suitable slot* |

The slot type sent to the scheduling API is computed in the connector query: `PRIORITY` when the triage category is Priority Review, otherwise `ROUTINE`.
