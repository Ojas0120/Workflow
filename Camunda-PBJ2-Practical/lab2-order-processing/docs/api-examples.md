# Lab 2 - REST integration

## REST Connector configuration (task `Task_Payment`)

| Property | Value |
|---|---|
| Job type | `io.camunda:http-json:1` |
| Method / URL | `POST` `http://localhost:8082/api/payments/charge` |
| Body (FEEL) | `={orderRef: orderRef, paymentToken: paymentToken, amount: orderTotal, currency: "INR"}` |
| Timeouts | connect 10 s, read 10 s |
| Result variable | `paymentResponse` |
| Result expression | `={paymentStatus: response.body.status, transactionId: response.body.transactionId, paymentMessage: response.body.message}` |
| Retries / backoff | 3 / PT0S (no error expression: HTTP failures become incidents) |

## Mock payment API (http://localhost:8082)

```bash
curl -s -X POST http://localhost:8082/api/payments/charge -H 'Content-Type: application/json' \
  -d '{"orderRef":"ORD-1","paymentToken":"tok_test_ok","amount":2499,"currency":"INR"}'
```
```json
{"orderRef":"ORD-1","amount":2499,"currency":"INR","status":"APPROVED","transactionId":"TXN-7A1C9E02","message":"Payment approved (simulated)"}
```
Declined: `paymentToken":"tok_test_declined"` returns `"status":"DECLINED","message":"Card declined (simulated)"`.

## Fault injection (incident demo)

```bash
curl -X POST 'http://localhost:8082/admin/outage/payment?enabled=true'    # payment API now returns 503
curl -X POST 'http://localhost:8082/admin/outage/payment?enabled=false'   # corrective action
curl http://localhost:8082/admin/outage                                   # current switches
```
