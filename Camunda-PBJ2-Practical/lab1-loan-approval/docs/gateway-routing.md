# Lab 1 - Gateway routing explanation

| Gateway | Condition (FEEL) | Path |
|---|---|---|
| Data valid? | `validationPassed = true` | Yes -> credit assessment; default -> *Application incomplete / invalid* |
| (boundary) Credit API error / timeout | BPMN error `CREDIT_API_ERROR` from the connector's error expression | -> Manual review (Tasklist) |
| Decision? | `loanDecision.decision = "Approved"` | -> Record & notify APPROVED |
| | `loanDecision.decision = "Rejected"` | -> Record & notify REJECTED |
| | default | Manual Review -> Tasklist review |
| Reviewer outcome? | `reviewerDecision = "Approved"` | -> notify APPROVED; default -> notify REJECTED |

A worker exception (Scenario 5) is not a gateway outcome: the job fails, retries reach 0 and Operate shows an incident on *Validate applicant*.
