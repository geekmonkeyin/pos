package com.gkmonk.pos.services;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopifyServiceImpl {

    @Value("${shopify.product.url:.json}")
    private String productUrl;
    @Value("${shopify.single.order.url}")
    private String singleOrderUrl;
    private String dynamicProductUrl;
    @Value("${shopify.protocol}")
    private String protocol;
    @Value("${shopify.token}")
    private String shopifyToken;
    @Value("${shopify.domain}")
    private String shopifyDomain;
    @Value("${shopify.shortToken}")
    private String shortToken;

    public void updateShopifyDetails(Inventory inventory){
        String id = StringUtils.isNotBlank(inventory.getProductId()) ? inventory.getProductId() : inventory.getProductVariantId();

        dynamicProductUrl = productUrl.replace("$$", "/"+id+".json");
        dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
        try {
            JsonObject response = callShopify(dynamicProductUrl);
            if(isErrorResponse(response)){
                dynamicProductUrl = productUrl.replace("products$$", "variants/"+id+".json");
                dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
                Thread.sleep(3000);
                response = callShopify(dynamicProductUrl);
                if(isErrorResponse(response)){
                    return;
                }
                String productId = getProductIdFromVariantCall(response);
                inventory.setProductId(productId);
                updateShopifyDetails(inventory);
                return;
            }
            updateTitle(response,inventory);
            updateStock(response,inventory);
            updateImages(response,inventory);
        } catch (UnirestException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private void updateImages(JsonObject response, Inventory inventory) {
        JsonObject product = getProduct(response);
        if(product == null){
            return;
        }
        JsonArray variants =  getVariants(product);
        variants.forEach( var ->{
            String varId = String.valueOf(var.getAsJsonObject().get("id"));
            if(inventory.getUpcId().equalsIgnoreCase(varId)){
                String imageId = String.valueOf(var.getAsJsonObject().get("image_id"));
                String imageUrl = getImageUrl(product,imageId);
                if(imageUrl != null){
                    inventory.setImageUrl(imageUrl.replace("\"",""));
                }
            }
        });
    }

    private String getImageUrl(JsonObject response , String imageId) {
            JsonArray imageArr = response.getAsJsonArray("images");
            for(int i = 0; i < imageArr.size(); i++) {
                    JsonObject imgObj = imageArr.get(i).getAsJsonObject();
                    if (imgObj.get("id").getAsString().equalsIgnoreCase(imageId)) {
                        return imgObj.get("src").toString();
                    }
            }
            return null;
    }

    private void updateStock(JsonObject response, Inventory inventory) {
        JsonObject product = getProduct(response);
        if(product == null){
            return;
        }
        JsonArray variants = getVariants(product);
        if(variants !=null ){

            variants.forEach(variant -> {
                boolean noVariant = false;

                if(variants.size() == 1){
                    noVariant = true;
                }
               JsonObject variantObject = variant.getAsJsonObject();
               updateProductStorageforVariant(variantObject,inventory,noVariant);
           });
        }
    }

    private void updateProductStorageforVariant(JsonObject variantObject, Inventory inventory,boolean noVariant) {
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

    private String getProductId(JsonObject variantObject) {

        return getProduct(variantObject).get("id").getAsString();
    }

    private String getVariantId(JsonObject variantObject) {
        return variantObject!= null ? variantObject.get("id").getAsString() : null;
    }

    private int getQuantity(JsonObject variantObject) {
            if(isProductNestedJson(variantObject)) {
                return getVariants(getProduct(variantObject)).get(0).getAsJsonObject().get("inventory_quantity").getAsInt();
            }
            return variantObject != null ? variantObject.get("inventory_quantity").getAsInt() : 0;
    }

    private boolean isProductNestedJson(JsonObject variantObject) {
        return getProduct(variantObject) != null;
    }

    private JsonArray getVariants(JsonObject product) {
        return product.getAsJsonArray("variants");
    }

    private void updateTitle(JsonObject response, Inventory inventory) {
        String title = getTitle(response);
        if(isTitleMatched(title,inventory.getProductTitle())){
            return;
        }
    }

    private boolean isTitleMatched(String productTitle, String title) {
        return productTitle != null && productTitle.equalsIgnoreCase(title);
    }

    private String getTitle(JsonObject response) {
        JsonObject product = getProduct(response);
        return product != null ? product.get("title").getAsString() : null;
    }

    private JsonObject getProduct(JsonObject response) {
        return response.getAsJsonObject("product");
    }

    public JsonObject callShopify(String url) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get(url)
                .asString();
        Gson gson = new Gson();
        return gson.fromJson(response.getBody(), JsonObject.class);
    }

    public List<Inventory> fetchProductFromShopify(String productId) {

        dynamicProductUrl = productUrl.replace("$$", "/"+productId+".json");
        dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
        try {
            Thread.sleep(3000);
            JsonObject response = callShopify(dynamicProductUrl);
            if(isErrorResponse(response)){
                dynamicProductUrl = productUrl.replace("products$$", "variants/"+productId+".json");
                dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
                Thread.sleep(3000);
                response = callShopify(dynamicProductUrl);
                if(isErrorResponse(response)){
                    return null;
                }
                productId = getProductIdFromVariantCall(response);
                return fetchProductFromShopify(productId);
            }
            return convertJSONToInventory(response);
        } catch (UnirestException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getProductIdFromVariantCall(JsonObject response) {
        return response.getAsJsonObject("variant").get("product_id").getAsString();
    }

    private boolean isErrorResponse(JsonObject response) {
        return response == null || response.get("errors") != null;
    }

    private List<Inventory> convertJSONToInventory(JsonObject response) {
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

    private List<Inventory> convertJSONToInventoryVariations(JsonArray variants,String title) {
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

    private String getVariantTitle(JsonObject variantObject) {

        return variantObject != null ? variantObject.get("title").getAsString() : null;
    }

    private Integer getVariantQty(JsonObject variantObject) {
        return variantObject != null ? variantObject.get("inventory_quantity").getAsInt() : null;
    }

    private String getVariantSku(JsonObject variantObject) {
        return variantObject != null ? variantObject.get("sku").getAsString() : null;
    }

    private boolean isMoreVariationAvailable(JsonObject response) {
        JsonObject product = getProduct(response);
        if(product == null){
            return false;
        }
        JsonArray variants = getVariants(product);
        return variants != null && variants.size() > 1;
    }

    public boolean archieveOrder(String orderId){

        String orderUrl = singleOrderUrl.replaceAll("[$]", "");
        orderUrl += orderId + "/close.json";
        String url = protocol + shopifyDomain + orderUrl;
        Unirest.setTimeouts(0, 0);
        try {
            HttpResponse<String> response = Unirest.post(url).header("X-Shopify-Access-Token", shortToken).header(
                            "Cookie",
                            "_master_udr=eyJfcmFpbHMiOnsibWVzc2FnZSI6IkJBaEpJaWs0WWpKaE5qSXhOaTA0TWpoakxUUXpNemN0T0RZeVlTMHdOalV5T0dFMFlUYzJZV1VHT2daRlJnPT0iLCJleHAiOiIyMDI1LTA4LTAzVDE2OjM3OjA1LjA2MFoiLCJwdXIiOiJjb29raWUuX21hc3Rlcl91ZHIifX0%3D--d556c187ef14f04dfb8d6c3f244c288469dbf0c3; _secure_admin_session_id=8c213cda947e64c9d936c0ce99f3b976; _secure_admin_session_id_csrf=8c213cda947e64c9d936c0ce99f3b976; identity-state=BAhbBkkiJWEwMDQyNjNiNWFmZDFlNjI3MjJmOWYxMDM3YzMwYTA5BjoGRUY%3D--456c9e112177239233a9713de22eb00bfc7d67c2; identity-state-a004263b5afd1e62722f9f1037c30a09=BAh7DEkiDnJldHVybi10bwY6BkVUSSI0aHR0cHM6Ly9iYWQyMmEtMi5teXNob3BpZnkuY29tL2FkbWluL2F1dGgvbG9naW4GOwBUSSIRcmVkaXJlY3QtdXJpBjsAVEkiQGh0dHBzOi8vYmFkMjJhLTIubXlzaG9waWZ5LmNvbS9hZG1pbi9hdXRoL2lkZW50aXR5L2NhbGxiYWNrBjsAVEkiEHNlc3Npb24ta2V5BjsAVDoMYWNjb3VudEkiD2NyZWF0ZWQtYXQGOwBUZhcxNjkyMDUyMDgzLjU3NzcxODdJIgpub25jZQY7AFRJIiUxZmY5OTFmOGIyNjVmZjM4ZWM4OGQ1ODA5Yzg2YWU5YQY7AEZJIgpzY29wZQY7AFRbEEkiCmVtYWlsBjsAVEkiN2h0dHBzOi8vYXBpLnNob3BpZnkuY29tL2F1dGgvZGVzdGluYXRpb25zLnJlYWRvbmx5BjsAVEkiC29wZW5pZAY7AFRJIgxwcm9maWxlBjsAVEkiTmh0dHBzOi8vYXBpLnNob3BpZnkuY29tL2F1dGgvcGFydG5lcnMuY29sbGFib3JhdG9yLXJlbGF0aW9uc2hpcHMucmVhZG9ubHkGOwBUSSIwaHR0cHM6Ly9hcGkuc2hvcGlmeS5jb20vYXV0aC9iYW5raW5nLm1hbmFnZQY7AFRJIkJodHRwczovL2FwaS5zaG9waWZ5LmNvbS9hdXRoL21lcmNoYW50LXNldHVwLWRhc2hib2FyZC5ncmFwaHFsBjsAVEkiPGh0dHBzOi8vYXBpLnNob3BpZnkuY29tL2F1dGgvc2hvcGlmeS1jaGF0LmFkbWluLmdyYXBocWwGOwBUSSI3aHR0cHM6Ly9hcGkuc2hvcGlmeS5jb20vYXV0aC9mbG93LndvcmtmbG93cy5tYW5hZ2UGOwBUSSI%2BaHR0cHM6Ly9hcGkuc2hvcGlmeS5jb20vYXV0aC9vcmdhbml6YXRpb24taWRlbnRpdHkubWFuYWdlBjsAVEkiPmh0dHBzOi8vYXBpLnNob3BpZnkuY29tL2F1dGgvbWVyY2hhbnQtYmFuay1hY2NvdW50Lm1hbmFnZQY7AFRJIg9jb25maWcta2V5BjsAVEkiDGRlZmF1bHQGOwBU--c93ddcf687d56c4fd33b649e6a12f9e4dc31ad0a")
                    .asString();
            return true;
        } catch (UnirestException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }
}
