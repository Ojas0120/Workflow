# Lab 1 - Test-case table

Fill the **Actual outcome** and **Status** columns from your own runs (Operate instance keys + screenshots). Start variables are in `test-inputs/`.

| # | Scenario | Input file | Expected route | Expected end event | Actual outcome | Status |
|---|---|---|---|---|---|---|
| 1 | Meets approval criteria | t1-approve.json | Validate -> Credit (780) -> DMN R4 Approved -> Notify APPROVED | Loan approved |  |  |
| 2 | Fails eligibility | t2-reject-low-score.json | Validate -> Credit (520) -> DMN R1 Rejected -> Notify REJECTED | Loan rejected |  |  |
| 3 | Needs manual review | t3-manual-review.json | Credit (650) -> DMN R5 Manual Review -> Tasklist review -> reviewer decides | Approved or Rejected per reviewer |  |  |
| 4a | Credit API error (HTTP 503) | t4-credit-api-error.json | Credit connector fails -> boundary error CREDIT_API_ERROR -> Manual review task | Per reviewer |  |  |
| 4b | Credit API timeout (8 s > 5 s read timeout) | t4b-credit-api-timeout.json | Same as 4a | Per reviewer |  |  |
| 5 | Worker failure then recovery | t5-worker-failure.json | Validation job fails twice -> incident in Operate -> edit simulateWorkerFailure=false -> Retry -> continues as test 1 | Loan approved |  |  |
| 6 | Invalid input (age 17) | t6-invalid-data.json | Validation passes=false -> gateway 'No' | Application incomplete / invalid |  |  |
