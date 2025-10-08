package com.gkmonk.pos.utils;

import com.gkmonk.pos.model.CSVAnnotations;
import com.gkmonk.pos.model.pod.CustomerInfo;
import com.gkmonk.pos.model.pod.ProductDetails;
import com.google.gson.JsonObject;
import org.bson.Document;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MapperUtils {

    private MapperUtils(){}

    public static Map<String,Integer> convertCSVHeaderToMap(String[] headerFields){
        Map<String,Integer> headerMap = new HashMap<>();
        for(int i = 0 ; i < headerFields.length; i++){
            // Code to convert CSV header to map
            headerMap.put(headerFields[i], i);
        }
        return headerMap;
    }

    public static void toBeanFromJson(JSONObject jsonObject, Object object){
         Class<?> objectClass  = object.getClass();
        Field[] fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            // Code to set fields of object
            CSVAnnotations annotation = field.getAnnotation(CSVAnnotations.class);
            if(annotation == null){
                continue;
            }
            String key = annotation.column();
            if(!jsonObject.has(key)){
                continue;
            }
            try {
                String fieldName = StringUtils.capitalizeFirstLetter(field.getName());
                Method method = objectClass.getDeclaredMethod("set"+fieldName,field.getType());
                castToType(field,jsonObject.get(key).toString(),object,method);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(jsonObject);
    }

    public static void toBean(String[] fields, Object object, Map<String, Integer> headerMap) {
        Class<?> objectClass  = object.getClass();
        Field[] field = objectClass.getDeclaredFields();
        for (Field f : field) {
            // Code to set fields of object
            CSVAnnotations annotation = f.getAnnotation(CSVAnnotations.class);
            String key = annotation.column();
            if(!headerMap.containsKey(key)){
                continue;
            }
            int index = headerMap.get(key);
            try {
                String fieldName = StringUtils.capitalizeFirstLetter(f.getName());

                Method method = objectClass.getDeclaredMethod("set"+fieldName,f.getType());
                    castToType(f,fields[index],object,method);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }

    private static void castToType(Field f,String field, Object object,Method method) throws InvocationTargetException, IllegalAccessException {
        if(StringUtils.isBlank(field)){
            return;
        }
        switch (f.getType().getName()){
            case "java.lang.String":
                String val = String.valueOf(field);
                if(val.contains(",")){
                    System.out.println("Checking");
                }
                val = val.replaceAll(",","");
                method.invoke(object, val);
                break;
                case "int":
                case "java.lang.Integer":
                    method.invoke(object, Integer.parseInt(field));
                    break;
            case "double":
            case "java.lang.Double":
                method.invoke(object, Double.parseDouble(field));
                break;
            case "boolean":
                case "java.lang.Boolean":
                    method.invoke(object, Boolean.valueOf(field));
                    break;
            default:
                method.invoke(object, String.valueOf(field));
        }
    }


    public static CustomerInfo getContactInfo(Document result) {
        Document address = Optional.ofNullable(result.get("shipping_address", Document.class))
                .orElse(result.get("billing_address", Document.class));

        if (address == null) return null;

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerName(getCustomerName(result));
        customerInfo.setAddress(address.getString("address1"));
        customerInfo.setPhoneNo(PhoneUtils.formatPhoneNo(address.getString("phone")));
        customerInfo.setCity(address.getString("city"));
        customerInfo.setPostalCode(address.getString("zip"));
        customerInfo.setCountry(address.getString("country"));
        customerInfo.setEmail(result.getString("contact_email"));
        customerInfo.setState(address.getString("province"));

        return customerInfo;
    }

    public static List<ProductDetails> getProductDetailsFromDocument(Document result) {
        List<Document> fulfillments = result.getList("fulfillments", Document.class);
        if (fulfillments == null || fulfillments.isEmpty()) return null;

        List<Document> lineItems = fulfillments.get(0).getList("line_items", Document.class);
        if (lineItems == null || lineItems.isEmpty()) return null;

        return lineItems.stream().map(item -> {
            ProductDetails details = new ProductDetails();
            details.setProductId(item.getString("product_id"));
            details.setVariantId(item.getString("variant_id"));
            details.setProductName(item.getString("name"));
            details.setQuantity(item.getInteger("quantity"));
            details.setPrice(Double.parseDouble(item.getString("price")));
            details.setKeywords(getKeywords(details.getProductName()));
            return details;
        }).collect(Collectors.toList());
    }

    private static List<String> getKeywords(String productName) {
        return Arrays.stream(productName.toLowerCase().split(" "))
                .filter(word -> !Arrays.asList("wrap","message","gift","the", "with", "for", "and", "a", "of").contains(word))
                .collect(Collectors.toList());
    }

    public static boolean isLineItemsEmpty(List<Document> lineItems) {
        return lineItems == null || lineItems.isEmpty();
    }

    public static String getCustomerName(Document result) {
        Document billing = result.get("billing_address", Document.class);
        if (billing == null) return "Unknown";

        String firstName = Optional.ofNullable(billing.getString("first_name")).orElse("");
        String lastName = Optional.ofNullable(billing.getString("last_name")).orElse("");
        return (firstName + " " + lastName).trim();
    }

    public static JsonObject getPickupDetails() {
        JsonObject pickup = new JsonObject();
        pickup.addProperty("vendor_name", "GIAN Retails Pvt Ltd");
        pickup.addProperty("address_1", "Geekmonkey warehouse, 54/10 Rajpur Road, Near Pantaloons Showroom");
        pickup.addProperty("address_2", "Adjacent to Ram Sharanam Ashram");
        pickup.addProperty("city", "Dehradun");
        pickup.addProperty("state", "Uttarakhand");
        pickup.addProperty("postcode", "248001");
        pickup.addProperty("country", "India");
        pickup.addProperty("phone", "9560772223");
        return pickup;
    }

    public static JsonObject getGSTDetails() {
        JsonObject gstDetails = new JsonObject();
        gstDetails.addProperty("gst_number", POSConstants.EMPTY);
        gstDetails.addProperty("cgst", POSConstants.EMPTY);
        gstDetails.addProperty("igst", POSConstants.EMPTY);
        gstDetails.addProperty("sgst", POSConstants.EMPTY);
        gstDetails.addProperty("hsn_number", POSConstants.EMPTY);
        gstDetails.addProperty("ewaybill_number", POSConstants.EMPTY);
        return gstDetails;
    }
}
