# Lab 2 - Gateway routing explanation

| Gateway | Condition (FEEL) | Path |
|---|---|---|
| Order valid? | `validationPassed = true` | Yes -> inventory check; default -> *Order invalid* |
| Stock available? | `stockStatus = "IN_STOCK"` | Yes -> calculate total; default -> *Cannot fulfil: insufficient stock* |
| High-value order? | `orderTotal >= 50000` | Yes -> Tasklist manual verification; default -> payment |
| Verified? | `verificationDecision = "Approved"` | Yes -> payment; default -> *Order cancelled by reviewer* |
| Payment approved? | `paymentStatus = "APPROVED"` | Yes -> fulfilment; default -> *Payment declined* |

All gateways are exclusive; each has a default flow so an unexpected value can never leave the instance stuck without a path.
A payment-gateway HTTP error (e.g. 503) is *not* a gateway outcome: the connector job fails, retries are exhausted and an **incident** appears in Operate.
