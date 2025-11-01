package com.gkmonk.pos.services.courier.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkmonk.pos.model.courier.CourierQuotationRequest;
import com.gkmonk.pos.model.order.CourierOption;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.utils.POSConstants;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShipmozoServiceImpl extends  AbstractServiceImpl {


    private static final String API_URL = "https://shipping-api.com/app/api/v1/rate-calculator";
    private static final String PUBLIC_KEY = "RAC9F1q4BZDTiE7Kfx2P";
    private static final String PRIVATE_KEY = "q3PtfX8wOCxFr6mMe2iL";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Map<String, String> getEDD(String awb) throws Exception {
        return Map.of();
    }

    @Override
    public Map<String, String> getCurrentStatus(String awb) throws Exception {
        return Map.of();
    }

    @Override
    public JsonObject createRequestBody(String awb) {
        return null;
    }

    @Override
    public List<String> getSupportedCourierCompanies() {
        return Arrays.asList(POSConstants.DTDC_COURIER,POSConstants.BLUEDART_COURIER,POSConstants.DELHIVERY_COURIER);
    }

    @Override
    public boolean isReturnSupported() {
        return false;
    }

    @Override
    public boolean isReplacementSupported() {
        return false;
    }

    @Override
    public String createReturnOrder(PackedOrder returnOrder) {
        return "";
    }

    @Override
    public List<CourierOption> getQuote(CourierQuotationRequest request) throws Exception {
        try {
                String jsonBody = MAPPER.writeValueAsString(request);

                HttpResponse<String> res = Unirest.post(API_URL)
                        .header("public-key", PUBLIC_KEY)
                        .header("private-key", PRIVATE_KEY)
                        .header("Content-Type", "application/json")
                        .body(jsonBody)
                        .asString();

                if (res.getStatus() < 200 || res.getStatus() >= 300) {
                    throw new RuntimeException("Rate API failed: " + res.getStatus() + " " + res.getStatusText()
                            + " body=" + res.getBody());
                }

                JsonNode root = MAPPER.readTree(res.getBody());
                JsonNode arr = firstArray(root, "quotes", "data", "rates", "options");
                if (arr == null) {
                    // If API already returns a list of CourierOption-like objects, try direct mapping.
                    if (root.isArray()) arr = root;
                    else throw new RuntimeException("Unexpected rate response shape: " + root.toString());
                }

                List<CourierOption> out = new ArrayList<>();
                for (JsonNode n : arr) {
                    CourierOption co = new CourierOption();
                    co.setId(UUID.randomUUID().toString());

                    co.setName(text(n, "name", "courier", "courier_name", "carrier", "company", "logistics_name"));
                    co.setService(text(n, "service", "service_name", "product", "product_type", "serviceType"));
                    co.setEta(parseEta(n)); // normalized like "1-3 days" / "3 days"
                    co.setCost(number(n, "cost", "price", "rate", "total", "charges", "total_charges", "freight", "amount"));
                    co.setCod(bool(n, "cod", "codAvailable", "cod_available", "is_cod", "supports_cod"));
                    co.setAggregator(text(n, "aggregator", "source", "provider", "platform"));
                    co.setImageURL(text(n, "imageURL", "image", "logo", "carrier_logo", "icon"));

                    // Fallbacks for common nested shapes
                    if (StringUtils.isBlank(co.getName())) {
                        co.setName(text(n.path("carrier"), "name", "code", "title"));
                    }
                    if (StringUtils.isBlank(co.getService())) {
                        co.setService(text(n.path("service"), "name", "code", "title"));
                    }
                    if (co.getCost() == 0d) {
                        co.setCost(number(n.path("pricing"), "total", "base", "net_amount"));
                    }
                    if (isBlank(co.getEta())) {
                        co.setEta(parseEta(n.path("sla")));
                    }

                    // Only add if we have at least a carrier name and a price
                    if (!isBlank(co.getName()) && co.getCost() > 0) {
                        out.add(co);
                    }
                }

                // Sort low→high cost (typical UX)
                out.sort(Comparator.comparingDouble(CourierOption::getCost));
                return out;

            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch/parse courier options: " + e.getMessage(), e);
            }
        }

        /* -------------------- helpers -------------------- */

        private static JsonNode firstArray(JsonNode root, String... candidates) {
            if (root == null) return null;
            if (root.isArray()) return root;
            for (String k : candidates) {
                JsonNode n = root.path(k);
                if (n.isArray()) return n;
            }
            // sometimes nested under "result" or "response"
            for (String top : new String[]{"result", "response"}) {
                JsonNode bucket = root.path(top);
                if (bucket.isArray()) return bucket;
                for (String k : candidates) {
                    JsonNode n = bucket.path(k);
                    if (n.isArray()) return n;
                }
            }
            return null;
        }

        private static String text(JsonNode node, String... keys) {
            for (String k : keys) {
                JsonNode n = node.path(k);
                if (!n.isMissingNode() && !n.isNull()) {
                    String v = n.asText(null);
                    if (v != null && !v.trim().isEmpty()) return v.trim();
                }
            }
            return null;
        }

        private static double number(JsonNode node, String... keys) {
            for (String k : keys) {
                JsonNode n = node.path(k);
                if (n.isNumber()) return n.asDouble();
                // Sometimes numbers come as strings
                if (n.isTextual()) {
                    try { return Double.parseDouble(n.asText().trim()); } catch (Exception ignore) {}
                }
            }
            return 0d;
        }

        private static boolean bool(JsonNode node, String... keys) {
            for (String k : keys) {
                JsonNode n = node.path(k);
                if (n.isBoolean()) return n.asBoolean();
                if (n.isInt() || n.isLong()) return n.asInt() != 0;
                if (n.isTextual()) {
                    String t = n.asText().trim().toLowerCase();
                    if (t.equals("true") || t.equals("yes") || t.equals("y")) return true;
                    if (t.equals("false") || t.equals("no") || t.equals("n")) return false;
                    try { return Integer.parseInt(t) != 0; } catch (Exception ignore) {}
                }
            }
            return false;
        }

        private static String parseEta(JsonNode node) {
            if (node == null || node.isMissingNode() || node.isNull()) return null;

            // Common text fields
            String etaTxt = text(node, "eta", "etaText", "estimated_delivery", "delivery_eta", "tat", "sla_text");
            if (!isBlank(etaTxt)) return etaTxt;

            // Common numeric shapes: "min_days" / "max_days" or "days"
            int min = (int) number(node, "min_days", "min", "from_days");
            int max = (int) number(node, "max_days", "max", "to_days");
            int days = (int) number(node, "days");

            if (min > 0 && max > 0) return min + "-" + max + " days";
            if (days > 0) return days + " days";

            // Sometimes nested again
            String nested = text(node.path("eta"), "text");
            if (!isBlank(nested)) return nested;

            return null;
        }

        private static boolean isBlank(String s) {
            return s == null || s.trim().isEmpty();
        }




    @Override
    public boolean isActive() {
        return Boolean.TRUE;
    }

}
