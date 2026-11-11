# Lab 2 - DMN decision table `order_priority`

Hit policy **FIRST**. Inputs: `orderTotal` (INR), `customerCategory`, `deliveryPreference`. Outputs: `priority`, `slaHours`.

| # | Order value | Customer category | Delivery | Priority | SLA hours |
|---|---|---|---|---|---|
| 1 | - | - | Same-day | Urgent | 4 |
| 2 | >= 50000 | - | Express | Urgent | 4 |
| 3 | - | - | Express | High | 24 |
| 4 | - | Gold | Standard | High | 24 |
| 5 | >= 50000 | - | Standard | High | 24 |
| 6 | - | - | - | Normal | 72 |

## Sample evaluations

| Order | orderTotal | Category | Delivery | Rule | Result |
|---|---|---|---|---|---|
| Test 1 | 2499 | Silver | Standard | R6 | Normal / 72 h |
| Test 4 | 75249 | Gold | Express | R2 | Urgent / 4 h |
| Gold, 1 x PROD-100 | 1299 | Gold | Standard | R4 | High / 24 h |
| Any | 1699 | Standard | Same-day | R1 | Urgent / 4 h |
