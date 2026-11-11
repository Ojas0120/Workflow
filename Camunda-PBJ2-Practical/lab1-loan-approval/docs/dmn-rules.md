# Lab 1 - DMN decision table `loan_decision`

Hit policy: **FIRST** (rules are evaluated top to bottom; the first match wins). All thresholds are documented *educational* rules, not real lending criteria.

| # | Credit score | DTI ratio | Loan / annual income | Employment yrs | Decision | Reason |
|---|---|---|---|---|---|---|
| 1 | < 550 | - | - | - | Rejected | Credit score below 550 |
| 2 | - | > 0.5 | - | - | Rejected | Debt-to-income ratio above 50% |
| 3 | - | - | > 5 | - | Rejected | Requested amount exceeds 5x annual income |
| 4 | >= 700 | <= 0.35 | <= 3 | >= 2 | Approved | Meets all automatic approval criteria |
| 5 | - | - | - | - | Manual Review | Borderline application - needs officer review |

## Sample evaluations (hand-checked against the worker formulas)

dtiRatio = (existingMonthlyDebt + requestedAmount / loanTermMonths) / monthlyIncome; loanToIncomeRatio = requestedAmount / (monthlyIncome x 12).

| Test input | Credit score | DTI | Loan / income | Years | First matching rule | Result |
|---|---|---|---|---|---|---|
| APP-1001 | 780 | 0.1528 | 0.3472 | 5 | R4 | Approved |
| APP-1002 | 520 | 0.3333 | 0.6250 | 3 | R1 | Rejected |
| APP-1003 | 650 | 0.2708 | 0.8333 | 4 | R5 | Manual Review |
