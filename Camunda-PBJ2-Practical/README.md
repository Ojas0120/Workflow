# PBJ-2 Practical Assignment - Camunda 8 End-to-End Workflow Automation (Labs 1-3)

Synthetic data only. No real payment credentials, secrets or patient data are in this repository.

| Lab | Process | Models | Worker + mock API | Port |
|---|---|---|---|---|
| 1 | Loan approval | `lab1-loan-approval/camunda-models` | `lab1-loan-approval/worker` | 8081 |
| 2 | Order processing & fulfilment | `lab2-order-processing/camunda-models` | `lab2-order-processing/worker` | 8082 |
| 3 | Hospital appointment & triage (educational) | `lab3-patient-triage/camunda-models` | `lab3-patient-triage/worker` | 8083 |

Each lab folder also has `docs/` (variable dictionary, DMN rules, routing, test-case table, API examples) and `test-inputs/` (start variables as JSON).

## Architecture (all labs)
Camunda 8 Run (local: Zeebe, Operate, Tasklist and Connectors, all on localhost) <- job workers (Spring Boot, gRPC 26500) and -> REST Connector -> mock API (same Spring Boot app on localhost:8081/8082/8083). No tunnel or cloud account is needed.

## One-time setup
1. Install **JDK 21-23** (Camunda 8 Run requirement) and set `JAVA_HOME`. Install Desktop Modeler and IntelliJ IDEA.
2. Download **Camunda 8 Run** for your OS from the Camunda downloads page (8.8), extract it, and start it from the `c8run` folder (`./start.sh` on macOS/Linux; see the c8run docs for the Windows command). Wait until http://localhost:8080/operate opens. Login: `demo` / `demo`.
3. In IntelliJ open a lab's `worker` folder (File > Open) as a Maven project, wait for dependencies to load, then run the `LabNApplication` class. The worker connects to localhost:26500 and the mock API listens on 8081 / 8082 / 8083.

Run one lab at a time (or all three; ports differ and job types are unique).

## Deploy and test
1. Open the lab's `camunda-models/` folder in Desktop Modeler. Check the REST Connector task's Properties panel (values are pre-filled; see `docs/api-examples.md`).
2. Deploy the `.dmn` and **all `.form`** files first, then the `.bpmn`, to the cluster at `localhost:8080` (Deploy dialog: Camunda 8 Self-Managed, URL `http://localhost:8080`, authentication None).
3. Tasklist -> Processes -> start the process and fill the form (values from `test-inputs/*.json`), or start from Modeler with the JSON as variables.
4. Work user tasks in Tasklist; watch instances, variables and incidents in Operate.

## Incident / recovery demos
* **Lab 1:** start `t5-worker-failure.json` -> incident on *Validate applicant* -> Operate: edit variable `simulateWorkerFailure` to `false` -> **Retry** incident.
* **Lab 2:** `curl -X POST 'http://localhost:8082/admin/outage/payment?enabled=true'`, start `t5-payment-outage.json`, wait for the incident, switch outage off, **Retry** in Operate.
* **Lab 3:** same with `/admin/outage/scheduling` on port 8083 and `t5-scheduling-outage.json`.
The admin endpoints are unauthenticated and only reachable from your own machine.

## Evidence checklist (take these yourself)
Process diagram, DMN table, deployment confirmation, Tasklist task list + completed form, Operate instance history per scenario, Operate incident before and after the fix, terminal showing the worker running. Put them in each lab's `screenshots/` folder and paste into the report.

## Status of this package
Models were generated programmatically and validated for well-formed XML, layout and routing consistency, but **not opened in Modeler or deployed**; Java was compile-checked against API stubs, **not built with Maven or run**. Expect to fix small issues on first deploy and record real results in the test tables.
