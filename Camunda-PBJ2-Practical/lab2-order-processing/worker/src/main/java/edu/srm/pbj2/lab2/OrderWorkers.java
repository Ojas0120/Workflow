package edu.srm.pbj2.lab2;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * External job workers for order-fulfilment.bpmn (validate, inventory, total, fulfil).
 * Catalogue, customers and stock are simulated in memory (synthetic data only).
 */
@Component
public class OrderWorkers {

    private static final Logger log = LoggerFactory.getLogger(OrderWorkers.class);

    private record Product(String name, double price) {}

    private static final Map<String, Product> CATALOGUE = Map.of(
            "PROD-100", new Product("Wireless Mouse", 1200),
            "PROD-200", new Product("27-inch Monitor", 25000),
            "PROD-300", new Product("Workstation Laptop", 90000));

    /** Simulated stock levels (reset when the worker restarts). */
    private final Map<String, Integer> stock = new HashMap<>(Map.of("PROD-100", 50, "PROD-200", 5, "PROD-300", 0));

    private static final Map<String, String> CUSTOMER_CATEGORY = Map.of("CUST-001", "Gold", "CUST-002", "Silver");
    private static final Map<String, Double> SHIPPING_FEE = Map.of("Standard", 99.0, "Express", 249.0, "Same-day", 499.0);
    private static final Pattern CUSTOMER_REF = Pattern.compile("CUST-\\d{3}");
    private static final Pattern LONG_DIGITS = Pattern.compile("\\d{12,}");
    private static final Set<String> DELIVERY = SHIPPING_FEE.keySet();

    /** Job type: order-validate */
    @JobWorker(type = "order-validate")
    public Map<String, Object> validate(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final List<String> errors = new ArrayList<>();
        final String customerRef = text(in.get("customerRef"));
        final String productId = text(in.get("productId"));
        final String delivery = text(in.get("deliveryPreference"));
        final String token = text(in.get("paymentToken"));
        final Double qty = in.get("quantity") instanceof Number n ? n.doubleValue() : null;

        if (!CUSTOMER_REF.matcher(customerRef).matches()) errors.add("customerRef must look like CUST-001");
        if (!CATALOGUE.containsKey(productId)) errors.add("unknown productId " + productId);
        if (qty == null || qty < 1 || qty > 100 || qty != Math.floor(qty)) errors.add("quantity must be a whole number 1-100");
        if (!DELIVERY.contains(delivery)) errors.add("deliveryPreference must be Standard, Express or Same-day");
        if (!token.startsWith("tok_test_")) errors.add("paymentToken must be a mock token starting with tok_test_");
        if (LONG_DIGITS.matcher(token).find()) errors.add("paymentToken must not contain card-number-like digits");

        final Map<String, Object> out = new HashMap<>();
        out.put("validationPassed", errors.isEmpty());
        out.put("validationErrors", errors);
        out.put("orderRef", "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        out.put("customerCategory", CUSTOMER_CATEGORY.getOrDefault(customerRef, "Standard"));
        log.info("[validate] customer={} product={} qty={} -> passed={}", customerRef, productId, qty, errors.isEmpty());
        return out;
    }

    /** Job type: order-check-inventory */
    @JobWorker(type = "order-check-inventory")
    public Map<String, Object> checkInventory(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final String productId = text(in.get("productId"));
        final int qty = ((Number) in.get("quantity")).intValue();
        final int available;
        synchronized (stock) {
            available = stock.getOrDefault(productId, 0);
        }
        final Map<String, Object> out = new HashMap<>();
        out.put("availableStock", available);
        out.put("stockStatus", available >= qty ? "IN_STOCK" : "INSUFFICIENT");
        log.info("[inventory] {} requested={} available={} -> {}", productId, qty, available, out.get("stockStatus"));
        return out;
    }

    /** Job type: order-calculate-total */
    @JobWorker(type = "order-calculate-total")
    public Map<String, Object> calculateTotal(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final Product p = CATALOGUE.get(text(in.get("productId")));
        final int qty = ((Number) in.get("quantity")).intValue();
        final double shipping = SHIPPING_FEE.get(text(in.get("deliveryPreference")));
        final double total = p.price() * qty + shipping;
        final Map<String, Object> out = new HashMap<>();
        out.put("unitPrice", p.price());
        out.put("shippingFee", shipping);
        out.put("orderTotal", total);
        log.info("[total] {} x {} + shipping {} = {}", qty, p.price(), shipping, total);
        return out;
    }

    /** Job type: order-fulfil  - reserves stock and creates a (simulated) shipment. */
    @JobWorker(type = "order-fulfil")
    @SuppressWarnings("unchecked")
    public Map<String, Object> fulfil(final ActivatedJob job) {
        final Map<String, Object> in = job.getVariablesAsMap();
        final String productId = text(in.get("productId"));
        final int qty = ((Number) in.get("quantity")).intValue();
        synchronized (stock) {
            final int available = stock.getOrDefault(productId, 0);
            if (available < qty) {
                throw new IllegalStateException("Stock changed since the inventory check for " + productId);
            }
            stock.put(productId, available - qty);
        }
        final Map<String, Object> priority = (Map<String, Object>) in.get("priorityResult");
        final Map<String, Object> out = new HashMap<>();
        out.put("shipmentId", "SHP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        out.put("fulfilmentPriority", priority == null ? "Normal" : priority.get("priority"));
        out.put("orderStatus", "FULFILLED");
        log.info("[fulfil] order={} shipment={} priority={}", in.get("orderRef"), out.get("shipmentId"), out.get("fulfilmentPriority"));
        return out;
    }

    private static String text(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }
}
