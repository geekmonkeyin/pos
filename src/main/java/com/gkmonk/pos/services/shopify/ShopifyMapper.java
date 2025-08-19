package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.Inventory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

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
