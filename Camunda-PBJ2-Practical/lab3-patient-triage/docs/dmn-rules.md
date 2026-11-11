# Lab 3 - DMN decision table `triage_category` (educational rules only)

> Educational simulation. These rules are **not** clinical guidance and must never be used for real patients.

Hit policy **FIRST**.

| # | Breathing flag | Chest-pain flag | Severity | Duration (days) | Age group | Category | Rationale |
|---|---|---|---|---|---|---|---|
| 1 | true | - | - | - | - | Urgent Assessment | Breathing difficulty red flag |
| 2 | - | true | - | - | - | Urgent Assessment | Chest pain red flag |
| 3 | - | - | Severe | - | - | Priority Review | Severe symptoms |
| 4 | - | - | Moderate | >= 7 | - | Priority Review | Moderate symptoms lasting a week or more |
| 5 | - | - | Moderate | - | Child, Senior | Priority Review | Moderate symptoms in child or senior |
| 6 | - | - | - | - | - | Routine | No urgency indicators |

## Sample evaluations

| Input | Rule | Result |
|---|---|---|
| Mild, 2 days, Adult, no flags | R6 | Routine |
| Severe, 3 days | R3 | Priority Review |
| Moderate + breathing flag | R1 | Urgent Assessment |
| Moderate, 3 days, Senior | R5 | Priority Review |
