/*
package com.gkmonk.pos.utils;

import com.gkmonk.pos.model.pod.CustomerInfo;
import com.gkmonk.pos.model.pod.ProductDetails;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@
class MapperUtilsTest {

    @Test
    void testConvertCSVHeaderToMap() {
        String[] headers = {"name", "age", "active"};
        Map<String, Integer> headerMap = MapperUtils.convertCSVHeaderToMap(headers);
        assertEquals(3, headerMap.size());
        assertEquals(0, headerMap.get("name"));
        assertEquals(2, headerMap.get("active"));
    }

    @Test
    void testToBeanFromJson_withValidData() throws JSONException {
        JSONObject json = new JSONObject()
                .put("name", "Alice")
                .put("age", 30)
                .put("active", true);

        MockCSVBean bean = new MockCSVBean();
        MapperUtils.toBeanFromJson(json, bean);

        assertEquals("Alice", bean.getName());
        assertEquals(30, bean.getAge());
        assertTrue(bean.isActive());
    }

    @Test
    void testToBeanFromJson_withMissingField() throws JSONException {
        JSONObject json = new JSONObject()
                .put("name", "Bob");

        MockCSVBean bean = new MockCSVBean();
        MapperUtils.toBeanFromJson(json, bean);

        assertEquals("Bob", bean.getName());
        assertEquals(0, bean.getAge()); // default int
        assertFalse(bean.isActive()); // default boolean
    }

    @Test
    void testToBean_fromCSV() {
        String[] csvRow = {"Charlie", "45", "true"};
        Map<String, Integer> headerMap = new HashMap<>();
        headerMap.put("name", 0);
        headerMap.put("age", 1);
        headerMap.put("active", 2);

        MockCSVBean bean = new MockCSVBean();
        MapperUtils.toBean(csvRow, bean, headerMap);

        assertEquals("Charlie", bean.getName());
        assertEquals(45, bean.getAge());
        assertTrue(bean.isActive());
    }

    @Test
    void testToBean_withBlankValues() {
        String[] csvRow = {"", "", ""};
        Map<String, Integer> headerMap = new HashMap<>();
        headerMap.put(
                "name", 0);
        headerMap.put( "age", 1);
        headerMap.put("active", 2);

        MockCSVBean bean = new MockCSVBean();
        MapperUtils.toBean(csvRow, bean, headerMap);

        assertNull(bean.getName());
        assertEquals(0, bean.getAge());
        assertFalse(bean.isActive());
    }

    @Test
    void testGetCustomerInfo_withShippingAddress() {
        Document shippingAddress = new Document("address1", "123 Street")
                .append("city", "Delhi")
                .append("zip", "110001")
                .append("country", "India");

        Document result = new Document("shipping_address", shippingAddress)
                .append("contact_email", "test@example.com")
                .append("billing_address", new Document("first_name", "A").append("last_name", "B"));

        CustomerInfo info = MapperUtils.getContactInfo(result);

        assertNotNull(info);
        assertEquals("A B", info.getCustomerName());
        assertEquals("123 Street", info.getPhoneNo());
        assertEquals("Delhi", info.getCity());
        assertEquals("India", info.getCountry());
        assertEquals("110001", info.getPostalCode());
        assertEquals("test@example.com", info.getEmail());
    }

    @Test
    void testGetCustomerInfo_withBillingFallback() {
        Document billing = new Document("address1", "456 Road")
                .append("city", "Mumbai")
                .append("zip", "400001")
                .append("country", "India")
                .append("first_name", "John")
                .append("last_name", "Doe");

        Document result = new Document("billing_address", billing)
                .append("contact_email", "john@example.com");

        CustomerInfo info = MapperUtils.getContactInfo(result);

        assertEquals("John Doe", info.getCustomerName());
        assertEquals("456 Road", info.getPhoneNo());
        assertEquals("Mumbai", info.getCity());
        assertEquals("400001", info.getPostalCode());
    }

    @Test
    void testGetProductDetailsFromDocument() {
        Document item = new Document("product_id", "P001")
                .append("name", "Widget")
                .append("quantity", 3);

        List<Document> lineItems = Collections.singletonList(item);
        Document fulfillment = new Document("line_items", lineItems);
        Document result = new Document("fulfillments", Collections.singletonList((fulfillment)));

        List<ProductDetails> products = MapperUtils.getProductDetailsFromDocument(result);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("P001", products.get(0).getProductId());
    }

    @Test
    void testGetProductDetailsFromDocument_emptyFulfillments() {
        Document result = new Document("fulfillments", Collections.emptyList());
        assertNull(MapperUtils.getProductDetailsFromDocument(result));
    }

    @Test
    void testIsLineItemsEmpty() {
        assertTrue(MapperUtils.isLineItemsEmpty(null));
        assertTrue(MapperUtils.isLineItemsEmpty(Collections.emptyList()));
        assertFalse(MapperUtils.isLineItemsEmpty(Collections.singletonList(new Document())));
    }

    @Test
    void testGetCustomerName_withNullNames() {
        Document billing = new Document();  // No first/last name
        Document result = new Document("billing_address", billing);

        assertEquals("", MapperUtils.getCustomerName(result));
    }

}
*/
