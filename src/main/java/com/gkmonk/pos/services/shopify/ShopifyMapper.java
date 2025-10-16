package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.legacy.OrderSourceType;
import com.gkmonk.pos.model.legacy.ShopifyAddress;
import com.gkmonk.pos.model.legacy.ShopifyCustomer;
import com.gkmonk.pos.model.legacy.ShopifyFulfillment;
import com.gkmonk.pos.model.legacy.ShopifyLineItems;
import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.Customer;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.model.order.Orders;
import com.gkmonk.pos.model.order.PickItem;
import com.gkmonk.pos.model.pod.CustomerInfo;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.model.pod.ProductDetails;
import com.gkmonk.pos.model.returns.OrderLine;
import com.gkmonk.pos.model.returns.OrderLookupResponse;
import com.gkmonk.pos.utils.POSConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShopifyMapper {


    public static  String getProductIdFromVariantCall(JsonObject response) {
        return response.getAsJsonObject("variant").get("product_id").getAsString();
    }

    public static boolean isErrorResponse(JsonObject response) {
        return response == null || response.get("errors") != null;
    }


    public static List<Inventory> convertJSONToInventory(JsonObject response) {
        List<Inventory> inventoryList = new ArrayList<>();

        Inventory inventory = new Inventory();
        inventory.setProductTitle(getTitle(response));
        if(isMoreVariationAvailable(response)){
            //createVariation;
            inventoryList.addAll(convertJSONToInventoryVariations(getVariants(getProduct(response)),inventory.getProductTitle()));
        }else{
            inventory.setProductVariantId(getProductId(response));
            inventory.setUpcId(getProductId(response));
            inventory.setShopifyQuantity(getQuantity(response));
            inventory.setQuantity(getQuantity(response));
            inventory.setProductId(getProductId(response));
            inventoryList.add(inventory);
        }
        return inventoryList;
    }


    private static List<Inventory> convertJSONToInventoryVariations(JsonArray variants, String title) {
        List<Inventory> inventoryList = new ArrayList<>();
        if(variants != null){
            variants.forEach(variant -> {

                JsonObject variantObject = variant.getAsJsonObject();
                Inventory inventory = new Inventory();
                inventory.setProductTitle(title+ getVariantTitle(variantObject));
                inventory.setProductVariantId(getVariantId(variantObject));
                inventory.setUpcId(getVariantId(variantObject));
                inventory.setProductId(String.valueOf(variantObject.get("product_id")));
                inventory.setProductVariantSku(getVariantSku(variantObject));
                inventory.setQuantity(getVariantQty(variantObject));
                inventory.setShopifyQuantity(getVariantQty(variantObject));
                inventoryList.add(inventory);

            });
        }
        return inventoryList;
    }

    private static String getVariantTitle(JsonObject variantObject) {

        return variantObject != null ? variantObject.get("title").getAsString() : null;
    }

    private static Integer getVariantQty(JsonObject variantObject) {
        return variantObject != null ? variantObject.get("inventory_quantity").getAsInt() : null;
    }

    private static String getVariantSku(JsonObject variantObject) {
        return variantObject != null ? variantObject.get("sku").getAsString() : null;
    }

    private static boolean isMoreVariationAvailable(JsonObject response) {
        JsonObject product = getProduct(response);
        if(product == null){
            return false;
        }
        JsonArray variants = getVariants(product);
        return variants != null && variants.size() > 1;
    }

    private static int getQuantity(JsonObject variantObject) {
        if(isProductNestedJson(variantObject)) {
            return getVariants(getProduct(variantObject)).get(0).getAsJsonObject().get("inventory_quantity").getAsInt();
        }
        return variantObject != null ? variantObject.get("inventory_quantity").getAsInt() : 0;
    }


    private static boolean isProductNestedJson(JsonObject variantObject) {
        return getProduct(variantObject) != null;
    }


    public static JsonArray getVariants(JsonObject product) {
        return product.getAsJsonArray("variants");
    }


    public static String getImageUrl(JsonObject response , String imageId) {
        JsonArray imageArr = response.getAsJsonArray("images");
        for(int i = 0; i < imageArr.size(); i++) {
            JsonObject imgObj = imageArr.get(i).getAsJsonObject();
            if (imgObj.get("id").getAsString().equalsIgnoreCase(imageId)) {
                return imgObj.get("src").toString();
            }
        }
        return null;
    }

    public static void updateProductStorageforVariant(JsonObject variantObject, Inventory inventory,boolean noVariant) {
        String variantId = getVariantId(variantObject);
        if(variantId == null){
            return;
        }
        int inventoryQuantity = getQuantity(variantObject);
        if(noVariant || variantId.equals(inventory.getProductVariantId())){
            inventory.setShopifyQuantity(inventoryQuantity);
            inventory.setProductId(inventory.getProductId());
            inventory.setProductVariantId(variantId);
        }else{
            //addNewInventory(inventory, variantObject, inventoryQuantity);

        }
    }




        public static ShopifyOrders mapToShopifyOrder(Map<String, Object> orderMap) {
            ShopifyOrders shopifyOrder = new ShopifyOrders();
            mapAmount(orderMap,shopifyOrder);
            mapBasicFields(orderMap, shopifyOrder);
            mapCustomerDetails(orderMap, shopifyOrder);
            mapShippingAddress(orderMap, shopifyOrder);
            mapFulfillmentDetails(orderMap, shopifyOrder);
            shopifyOrder.setCustomStatus(OrderStatus.DISPATCHED.name());

            return shopifyOrder;
        }



    private static void mapAmount(Map<String, Object> orderMap, ShopifyOrders shopifyOrder) {
        String totalAmount =  ShopifyMapper.getTotalPriceFromOrder((Map<String, Object>) orderMap.get("currentTotalPriceSet"));
        shopifyOrder.setTotal_price(Double.parseDouble(totalAmount));
    }

    private static String getTotalPriceFromOrder(Map<String, Object> currentTotalPriceSet) {
        return currentTotalPriceSet.get("shopMoney") != null ?
                String.valueOf(((Map<String, Object>)currentTotalPriceSet.get("shopMoney")).get("amount")) : POSConstants.EMPTY;
    }

    private static void mapBasicFields(Map<String, Object> orderMap, ShopifyOrders shopifyOrder) {
        shopifyOrder.setId(ShopifyMapper.getIdFromGid(orderMap.get("id")));
        shopifyOrder.setName(String.valueOf(orderMap.get("name")));
        shopifyOrder.setCreated_at(String.valueOf(orderMap.get("processedAt")));
        shopifyOrder.setFulfillment_status(String.valueOf(orderMap.get("displayFulfillmentStatus")));
        shopifyOrder.setCod(isCod(String.valueOf(orderMap.get("displayFinancialStatus"))));

    }

    private static void mapFulfillmentDetails(Map<String, Object> orderMap, ShopifyOrders shopifyOrder) {
        Map<String, Object> fulfillmentMap = (Map<String, Object>) orderMap.get("fulfillmentOrders");
        if (fulfillmentMap != null) {
            List<Map<String, Object>> fnodes = (List<Map<String, Object>>) fulfillmentMap.get("nodes");
            if(shopifyOrder.getItems() == null){
                shopifyOrder.setItems(new ArrayList<>());
            }
            if (fnodes != null && !fnodes.isEmpty()) {
                ShopifyFulfillment[] fulfillments = fnodes.stream().map(node -> {
                    ShopifyFulfillment fulfillment = new ShopifyFulfillment();
                    fulfillment.setId(ShopifyMapper.getIdFromGid(node.get("id")));
                    fulfillment.setStatus(String.valueOf(node.get("status")));
                   fulfillment.setLine_items(getLineItems(node));
                    shopifyOrder.getItems().addAll(getLineItems(node));
                    return fulfillment;
                }).toArray(ShopifyFulfillment[]::new);
                shopifyOrder.setFulfillments(fulfillments);
            }
        }

    }

    public static List<ShopifyLineItems> getLineItems(Map<String, Object> orderMap) {
        List<ShopifyLineItems> lineItems = new ArrayList<>();
        Map<String,Object>  lineItemMap = (Map<String, Object>) orderMap.get("lineItems");
        // Extract nodes from the map
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) lineItemMap.get("nodes");
        if (nodes != null) {
            for (Map<String, Object> node : nodes) {
                // Extract the lineItem from each node
                Map<String, Object> lineItem = (Map<String, Object>) node.get("lineItem");
                if (lineItem != null) {
                    ShopifyLineItems shopifyLineItems = new ShopifyLineItems();
                    shopifyLineItems.setSku(String.valueOf(lineItem.get("sku")));
                    shopifyLineItems.setQuantity(lineItem.get("quantity") != null ? Integer.parseInt(String.valueOf(lineItem.get("quantity"))) : 0);
                    shopifyLineItems.setName(String.valueOf(lineItem.get("name")));
                    shopifyLineItems.setTitle(String.valueOf(lineItem.get("name")));
                    mapVariantInfoFromLineItem(lineItem, shopifyLineItems);
                    lineItems.add(shopifyLineItems);
                }
            }
        }

        return lineItems;
    }

    private static void mapVariantInfoFromLineItem(Map<String, Object> lineItem, ShopifyLineItems shopifyLineItems) {
        Map<String, Object> variantMap = (Map<String, Object>) lineItem.get("variant");
        if (variantMap != null) {
            shopifyLineItems.setVariant_id(ShopifyMapper.getIdFromGid(variantMap.get("id")));
            shopifyLineItems.setTitle(String.valueOf(variantMap.get("title")));
            Map<String, Object> parentDetails = (Map<String, Object>) variantMap.get("product");
            if(variantMap.get("image") != null){
                shopifyLineItems.setImageUrl(getImageFromVariant((Map<String, Object>) variantMap.get("image")));
            }else{
                shopifyLineItems.setImageUrl(getImageFromTheList((Map<String, Object>) parentDetails.get("images")));
            }
            shopifyLineItems.setProduct_id(getIdFromGid(parentDetails.get("id")));
            shopifyLineItems.setPrice(getDiscountedPrice((Map<String, Object>)lineItem.get("discountedUnitPriceSet")));
        }
    }

    private static String getDiscountedPrice(Map<String, Object> discountedUnitPriceSet) {
        return discountedUnitPriceSet.get("shopMoney") != null ?
                String.valueOf(((Map<String, Object>)discountedUnitPriceSet.get("shopMoney")).get("amount")) : POSConstants.EMPTY;
    }

    private static String getImageFromVariant(Map<String,Object> image) {
        return String.valueOf(image.get("url"));
    }

    private static String getImageFromTheList(Map<String,Object> images) {
           return getImageFromVariant((Map<String, Object>) ((List)images.get("nodes")).getFirst());
    }


    private static void mapShippingAddress(Map<String, Object> orderMap, ShopifyOrders shopifyOrder) {
        Map<String, Object> shippingAddressMap = (Map<String, Object>) orderMap.get("shippingAddress");
        if (shippingAddressMap != null) {
            ShopifyAddress shippingAddress = new ShopifyAddress();
            shippingAddress.setAddress1(String.valueOf(shippingAddressMap.get("address1")));
            shippingAddress.setCity(String.valueOf(shippingAddressMap.get("city")));
            shippingAddress.setProvince(String.valueOf(shippingAddressMap.get("province")));
            shippingAddress.setCountry(String.valueOf(shippingAddressMap.get("country")));
            shippingAddress.setZip(String.valueOf(shippingAddressMap.get("zip")));
            shippingAddress.setPhone(String.valueOf(shippingAddressMap.get("phone")));
            shopifyOrder.setShipping_address(shippingAddress);
        }

    }

    private static void mapCustomerDetails(Map<String, Object> orderMap, ShopifyOrders shopifyOrder) {
        Map<String, Object> customerMap = (Map<String, Object>) orderMap.get("customer");
        if (customerMap != null) {
            ShopifyCustomer customer = new ShopifyCustomer();
            customer.setFirstName(String.valueOf(customerMap.get("displayName")));
            customer.setLastName(POSConstants.EMPTY);
            customer.setEmail(String.valueOf(customerMap.get("email")));
            customer.setPhone(String.valueOf(customerMap.get("phone")));
            customer.setTotalSpent(getTotalSpent(customerMap.get("amountSpent")));
            customer.setNumberOfOrders(getNumberOfOrders(String.valueOf(customerMap.get("numberOfOrders"))));
            shopifyOrder.setCustomer(customer);
        }
    }

    private static int getNumberOfOrders(String numberOfOrders) {
        try {
            return numberOfOrders != null ? Integer.parseInt(numberOfOrders) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static double getTotalSpent(Object amountSpent) {
        try {
            Map<String, Object> amountMap = (Map<String, Object>)amountSpent;
            return amountSpent != null ? Double.parseDouble(String.valueOf(amountMap.get("amount"))) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static boolean isCod(String displayFinancialStatus) {
        return !"PAID".equalsIgnoreCase(displayFinancialStatus);
    }

    public static String getIdFromGid(Object gidOb) {
        String gid = String.valueOf(gidOb);
        if (gid == null || !gid.contains("/")) {
            return null;
        }
        String[] parts = gid.split("/");
        return parts[parts.length - 1];
    }

    public static List<ShopifyOrders> convertToShopifyOrders(List<Map<String, Object>> orderList) {
        List<ShopifyOrders> shopifyOrdersList = new ArrayList<>();
        for (Map<String, Object> orderMap : orderList){
            shopifyOrdersList.add(
                    mapToShopifyOrder(orderMap));
            }
                return shopifyOrdersList;
    }

    public static List<PickItem> convertShopifyOrdersToPickItems(List<ShopifyOrders> shopifyOrders) {

            List<PickItem> pickItems = new ArrayList<>();
            for (ShopifyOrders order : shopifyOrders) {
                if (order.getFulfillments() != null) {
                    for (ShopifyFulfillment fulfillment : order.getFulfillments()) {
                        if (fulfillment.getLine_items() != null) {
                            for (ShopifyLineItems lineItem : fulfillment.getLine_items()) {
                                PickItem pickItem = new PickItem();
                                pickItem.setVariationId(lineItem.getVariant_id());
                                pickItem.setPicked(false);
                                pickItem.setPriority(PickItem.Priority.MEDIUM);
                                pickItem.setName(lineItem.getName());
                                pickItem.setQty(lineItem.getQuantity());
                                pickItem.setImageUrl(lineItem.getImageUrl());
                                pickItem.setId(lineItem.getVariant_id());
                                pickItems.add(pickItem);
                            }
                        }
                    }
                }
            }
            return pickItems;
    }

    public static List<Orders> convertToOrders(List<ShopifyOrders> shopifyOrders) {
        List<Orders> orders = new ArrayList<>();
        if(shopifyOrders != null){
            shopifyOrders.stream().forEach(shopifyOrder -> {
                Orders order = new Orders();
                order.setOrderSourceType(OrderSourceType.SHOPIFY);
                order.setOrderNo(shopifyOrder.getId());
                order.setOrderDate(LocalDate.parse(shopifyOrder.getCreated_at()));
                order.setStatus(OrderStatus.valueOf(shopifyOrder.getCustomStatus()));
                orders.add(order);
            });
        }
        return orders;
    }

    public static ShopifyCustomer convertCustomerInfoToCustomer(CustomerInfo customerInfo) {
        ShopifyCustomer  customer = new ShopifyCustomer();
        customer.setFirstName(customerInfo.getCustomerName());
        customer.setEmail(customerInfo.getEmail());
        customer.setPhone(customerInfo.getPhoneNo());
        return customer;

    }

    public static Optional<OrderLookupResponse> convertToOrderLookupResponse(ShopifyOrders shopifyOrders) {
        return Optional.of(new OrderLookupResponse(
                shopifyOrders.getName(),
                getCustomer(shopifyOrders),
                getOrderLines(shopifyOrders),
                shopifyOrders.getOrder_status_url()
        ));
    }

    private static List<OrderLine> getOrderLines(ShopifyOrders shopifyOrders) {
        List<OrderLine> orderLines = new ArrayList<>();

        shopifyOrders.getItems().forEach(productDetails -> {
            OrderLine orderLine = new OrderLine();
            orderLine.setQty(productDetails.getQuantity());
            orderLine.setTitle(productDetails.getName());
            orderLine.setId(productDetails.getProduct_id());
            orderLine.setVariantId(productDetails.getVariant_id());
            orderLine.setOrderURL(shopifyOrders.getOrder_status_url());
            // List<byte[]> images =  imageDBService.fetchInventoryImagesById(productDetails.getProductId());
            orderLine.setImageURL(productDetails.getImageUrl());
            orderLines.add(orderLine);
        });
        return orderLines;
    }

    private static Customer getCustomer(ShopifyOrders shopifyOrders) {

            Customer customer =  new Customer();
            customer.setName(  shopifyOrders.getCustomer().getFirstName() +" "+ shopifyOrders.getCustomer().getLastName());
            customer.setPhone(  shopifyOrders.getShipping_address().getPhone());
            return customer;
    }

    public static ShopifyOrders convertPackedOrderToShopifyOrder(PackedOrder packedOrder) {
        ShopifyOrders shopifyOrders = new ShopifyOrders();
        shopifyOrders.setName(packedOrder.getGmId());
        shopifyOrders.setCod(packedOrder.isCod());
        shopifyOrders.setId(packedOrder.getOrderId());
        shopifyOrders.setCustomer(convertCustomerPackedOrderToShopifyOrders(packedOrder.getCustomerInfo()));
        shopifyOrders.setItems(convertProductDetailsToShopifyItems(packedOrder.getProductDetails()));
        shopifyOrders.setShipping_address(convertPackOrderToShippingAddress(packedOrder));
        return shopifyOrders;
    }

    private static ShopifyAddress convertPackOrderToShippingAddress(PackedOrder packedOrder) {
        ShopifyAddress shippingAddress = new ShopifyAddress();
        shippingAddress.setName(packedOrder.getCustomerName());
        shippingAddress.setAddress1(packedOrder.getCustomerInfo().getAddress());
        shippingAddress.setZip(packedOrder.getCustomerInfo().getPostalCode());
        shippingAddress.setPhone(packedOrder.getCustomerInfo().getPhoneNo());
        shippingAddress.setCity(packedOrder.getCustomerInfo().getCity());
        shippingAddress.setCountry(packedOrder.getCustomerInfo().getCountry());
        return shippingAddress;
    }

    private static ShopifyCustomer convertCustomerPackedOrderToShopifyOrders(CustomerInfo customerInfo) {
        ShopifyCustomer customer = new ShopifyCustomer();
        customer.setFirstName(customerInfo.getCustomerName());
        customer.setLastName(POSConstants.EMPTY);
        customer.setEmail(customerInfo.getEmail());
        customer.setPhone(customerInfo.getPhoneNo());
        return customer;
    }

    private static List<ShopifyLineItems> convertProductDetailsToShopifyItems(List<ProductDetails> productDetails) {
        List<ShopifyLineItems> shopifyLineItems = new ArrayList<>();
        productDetails.forEach(productDetail -> {
            ShopifyLineItems shopifyLineItem = new ShopifyLineItems();
            shopifyLineItem.setVariant_id(productDetail.getVariantId());
            shopifyLineItem.setProduct_id(productDetail.getProductId());
            shopifyLineItem.setQuantity(productDetail.getQuantity());
            shopifyLineItem.setName(productDetail.getProductName());
            shopifyLineItem.setImageUrl(productDetail.getImageURL());
            shopifyLineItem.setTitle(productDetail.getProductName());

            shopifyLineItems.add(shopifyLineItem);
        });
        return shopifyLineItems;
    }

    private void addNewInventory(Inventory inventory, JsonObject variantObject, int inventoryQuantity) {
        Inventory newInventory = new Inventory();
        newInventory.setShopifyQuantity(inventoryQuantity);
        newInventory.setProductTitle(inventory.getProductTitle()+ "("+getTitle(variantObject)+")");
        newInventory.setProductId(getProductId(variantObject));
        newInventory.setProductVariantId(getProductId(variantObject));
        //newInventory.;
    }

    private static String getProductId(JsonObject variantObject) {

        return getProduct(variantObject).get("id").getAsString();
    }

    private static String getVariantId(JsonObject variantObject) {
        return variantObject!= null ? variantObject.get("id").getAsString() : null;
    }

    public static JsonObject getProduct(JsonObject response) {
        return response.getAsJsonObject("product");
    }

    private static String getTitle(JsonObject response) {
        JsonObject product = getProduct(response);
        return product != null ? product.get("title").getAsString() : null;
    }

    public static void updateTitle(JsonObject response, Inventory inventory) {
        String title = getTitle(response);
        if(isTitleMatched(title,inventory.getProductTitle())){
            return;
        }
    }

    private static boolean isTitleMatched(String productTitle, String title) {
        return productTitle != null && productTitle.equalsIgnoreCase(title);
    }
}
