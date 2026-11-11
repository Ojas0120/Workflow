# Lab 2 - Test-case table

Stock resets when the worker restarts (PROD-100: 50, PROD-200: 5, PROD-300: 0). Fill **Actual outcome / Status** from your own runs.

| # | Scenario | Input file | Expected route | Expected end | Actual outcome | Status |
|---|---|---|---|---|---|---|
| 1 | Stock available, payment succeeds | t1-stock-and-payment-ok.json | total 2,499 -> priority Normal -> no review -> payment APPROVED -> fulfil | Order fulfilled |  |  |
| 2 | Insufficient stock | t2-insufficient-stock.json | 10 x PROD-200 > stock 5 -> gateway 'No' | Cannot fulfil: insufficient stock |  |  |
| 3 | Payment declined | t3-payment-declined.json | payment status DECLINED -> default flow | Payment declined |  |  |
| 4 | High-value -> manual review | t4-high-value-manual-review.json | total 75,249 -> Urgent -> Tasklist verify -> Approved -> payment -> fulfil | Order fulfilled (or cancelled if Rejected) |  |  |
| 5 | Payment service fails, recover | t5-payment-outage.json | Enable outage -> connector 503 x3 -> incident -> disable outage -> Retry in Operate -> payment OK -> fulfil | Order fulfilled |  |  |
| 6 | Invalid order (qty 0, card-like token) | t6-invalid-order.json | validationPassed=false | Order invalid |  |  |
