# Lab 1 - Process variable dictionary

| Variable | Type | Set by | Purpose |
|---|---|---|---|
| applicantId | string | Start form | Synthetic applicant identifier (also drives mock credit API results) |
| applicantName | string | Start form | Synthetic applicant name |
| age | number | Start form | Applicant age; validated 18-70 |
| requestedAmount | number | Start form | Requested loan amount in INR; validated 10,000-5,000,000 |
| loanTermMonths | number | Start form | Loan term; validated 6-360 |
| monthlyIncome | number | Start form | Gross monthly income in INR; must be > 0 |
| existingMonthlyDebt | number | Start form | Existing monthly debt payments in INR |
| employmentYears | number | Start form | Years in current employment (DMN input) |
| simulateWorkerFailure | boolean | Start form (test hook) / worker resets to false | When true the validation worker throws -> incident (Scenario 5) |
| validationPassed | boolean | Worker loan-validate-applicant | Gateway 'Data valid?' routes on this |
| validationErrors | list of string | Worker | Human-readable validation problems |
| estimatedInstalment | number | Worker | requestedAmount / loanTermMonths (interest-free educational estimate) |
| dtiRatio | number | Worker | (existingMonthlyDebt + estimatedInstalment) / monthlyIncome; DMN input |
| loanToIncomeRatio | number | Worker | requestedAmount / (monthlyIncome x 12); DMN input |
| creditResponse | object | REST Connector (resultVariable) | Full HTTP response of the credit API call |
| creditScore | number | REST Connector (resultExpression) | Credit score 300-850; DMN input |
| riskBand | string | REST Connector | LOW / MEDIUM / HIGH / VERY_HIGH |
| creditReference | string | REST Connector | Mock credit-bureau reference |
| loanDecision | object {decision, reason} | DMN loan_decision | decision = Approved / Rejected / Manual Review |
| reviewerDecision | string | Manual review form | Approved / Rejected - set by the loan officer |
| reviewerComment | string | Manual review form | Optional comment |
| finalStatus | string | Worker loan-notify | APPROVED or REJECTED |
| notificationId | string | Worker loan-notify | Simulated notification id |
| decidedAt | string (ISO-8601) | Worker loan-notify | Timestamp of the final decision |
