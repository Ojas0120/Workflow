# Lab 2 - Process variable dictionary

| Variable | Type | Set by | Purpose |
|---|---|---|---|
| customerRef | string | Start form | Synthetic customer reference CUST-nnn |
| productId | string | Start form | PROD-100 / PROD-200 / PROD-300 (simulated catalogue) |
| quantity | number | Start form | Whole number 1-100 |
| deliveryPreference | string | Start form | Standard / Express / Same-day (DMN input) |
| paymentToken | string | Start form | MOCK token only (`tok_test_ok`, `tok_test_declined`); real card numbers are rejected by the validator |
| validationPassed | boolean | Worker order-validate | Gateway 'Order valid?' |
| validationErrors | list of string | Worker order-validate | Reasons for rejection |
| orderRef | string | Worker order-validate | Generated order reference |
| customerCategory | string | Worker order-validate | Gold / Silver / Standard from simulated customer table (DMN input) |
| availableStock | number | Worker order-check-inventory | Simulated units in stock |
| stockStatus | string | Worker order-check-inventory | IN_STOCK or INSUFFICIENT; gateway 'Stock available?' |
| unitPrice | number | Worker order-calculate-total | INR per unit |
| shippingFee | number | Worker order-calculate-total | Standard 99 / Express 249 / Same-day 499 |
| orderTotal | number | Worker order-calculate-total | unitPrice x quantity + shippingFee; DMN input and high-value gateway |
| priorityResult | object {priority, slaHours} | DMN order_priority | Urgent / High / Normal with SLA hours |
| verificationDecision | string | Order verification form | Approved / Rejected by reviewer (only for orderTotal >= 50,000) |
| verificationComment | string | Order verification form | Optional |
| paymentResponse | object | REST Connector | Full HTTP response from the mock payment API |
| paymentStatus | string | REST Connector | APPROVED or DECLINED; gateway 'Payment approved?' |
| transactionId | string | REST Connector | Mock transaction id (null when declined) |
| paymentMessage | string | REST Connector | Gateway message |
| shipmentId | string | Worker order-fulfil | Simulated shipment id |
| fulfilmentPriority | string | Worker order-fulfil | Copy of priorityResult.priority |
| orderStatus | string | Worker order-fulfil | FULFILLED |
