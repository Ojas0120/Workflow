# Lab 3 - REST integration (mock hospital scheduling API, synthetic data)

## REST Connector configuration (task `Task_Slots`)

| Property | Value |
|---|---|
| Job type | `io.camunda:http-json:1` |
| Method / URL | `GET` `http://localhost:8083/api/scheduling/slots` |
| Query parameters (FEEL) | `={department: department, from: searchFrom, slotType: if triageResult.triageCategory = "Priority Review" then "PRIORITY" else "ROUTINE", timeOfDay: timeOfDay}` |
| Timeouts | connect 10 s, read 10 s |
| Result variable | `slotsResponse` |
| Result expression | `={availableSlots: response.body.slots, slotCount: response.body.count}` |
| Retries / backoff | 3 / PT0S |

## Mock API (http://localhost:8083)

```bash
curl -s 'http://localhost:8083/api/scheduling/slots?department=General%20Medicine&from=2026-11-10&slotType=ROUTINE&timeOfDay=Morning'
```
```json
{"department":"General Medicine","count":5,"slots":[
  {"slotId":"General-Medicine|2026-11-10T09:00|ROUTINE","start":"2026-11-10T09:00","clinic":"General Medicine Clinic A","slotType":"ROUTINE"}, "..."]}
```
Dermatology returns `{"count":0,"slots":[]}`.

## Fault injection

```bash
curl -X POST 'http://localhost:8083/admin/outage/scheduling?enabled=true'    # API returns 503
curl -X POST 'http://localhost:8083/admin/outage/scheduling?enabled=false'   # corrective action
```
The booking worker uses the same in-process `SchedulingService` as the mock API, so slots booked by the worker disappear from later searches.
