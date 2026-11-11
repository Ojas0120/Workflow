# Lab 1 - REST integration

## REST Connector configuration (task `Task_Credit`)

| Property | Value |
|---|---|
| Connector job type | `io.camunda:http-json:1` (REST Outbound Connector) |
| Authentication | None |
| Method / URL | `POST` `http://localhost:8081/api/credit/assess` |
| Body (FEEL) | `={applicantId: applicantId, monthlyIncome: monthlyIncome, requestedAmount: requestedAmount}` |
| Connection / read timeout | 10 s / **5 s** |
| Result variable | `creditResponse` |
| Result expression | `={creditScore: response.body.creditScore, riskBand: response.body.riskBand, creditReference: response.body.reference}` |
| Error expression | `=if error.code != null then bpmnError("CREDIT_API_ERROR", "Credit API error " + string(error.code)) else bpmnError("CREDIT_API_ERROR", "Credit API timeout or I/O error")` |
| Retries / backoff | 3 / PT0S |

A boundary error event (`CREDIT_API_ERROR`) on the task routes failures to the manual review user task, so a credit-service outage never silently approves or rejects an application.

## Mock API examples (run against `http://localhost:8081`)

```bash
curl -s -X POST http://localhost:8081/api/credit/assess -H 'Content-Type: application/json' \
  -d '{"applicantId":"APP-1001","monthlyIncome":120000,"requestedAmount":500000}'
```
```json
{"applicantId":"APP-1001","creditScore":780,"riskBand":"LOW","reference":"CR-3F9A1B2C","assessedAt":"2026-10-28T09:15:00Z"}
```
Error case (`applicantId` starting `APP-FAIL`):
```bash
curl -i -X POST http://localhost:8081/api/credit/assess -H 'Content-Type: application/json' -d '{"applicantId":"APP-FAIL-1004"}'
```
```
HTTP/1.1 503
{"error":"SERVICE_UNAVAILABLE","message":"Credit bureau temporarily unavailable (simulated)"}
```
