package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.services.externalapi.APIProxyService;
import com.gkmonk.pos.utils.POSConstants;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopifyServiceImpl {

    @Value("${shopify.product.url:.json}")
    private String productUrl;
    @Value("${shopify.single.order.url}")
    private String singleOrderUrl;
    private String dynamicProductUrl;
    @Value("${shopify.protocol}")
    private String protocol;
    private String shopifyToken;
    @Value("${shopify.domain}")
    private String shopifyDomain;
    @Autowired
    private ShopifyClient shopifyClient;

    @Autowired
    private APIProxyService apiProxyService;

    private String shortToken;

    @PostConstruct
    void init(){
        shopifyToken = System.getenv().get("shopify.token");
        shortToken  = System.getenv().get("shopify.shortToken");
    }


    public void updateShopifyDetails(Inventory inventory){
        String id = StringUtils.isNotBlank(inventory.getProductId()) ? inventory.getProductId() : inventory.getProductVariantId();
        dynamicProductUrl = productUrl.replace("$$", "/"+id+".json");
        dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
        try {
            JsonObject response = callShopify(dynamicProductUrl);
            if(ShopifyMapper.isErrorResponse(response)){
                dynamicProductUrl = productUrl.replace("products$$", "variants/"+id+".json");
                dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
                Thread.sleep(3000);
                response = callShopify(dynamicProductUrl);
                if(ShopifyMapper.isErrorResponse(response)){
                    return;
                }
                String productId = ShopifyMapper.getProductIdFromVariantCall(response);
                inventory.setProductId(productId);
                updateShopifyDetails(inventory);
                return;
            }
            ShopifyMapper.updateTitle(response,inventory);
            updateStock(response,inventory);
            updateImages(response,inventory);
        } catch (UnirestException | InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    private void updateImages(JsonObject response, Inventory inventory) {
        JsonObject product = ShopifyMapper.getProduct(response);
        if(product == null){
            return;
        }
        JsonArray variants =  ShopifyMapper.getVariants(product);
        variants.forEach( var ->{
            String varId = String.valueOf(var.getAsJsonObject().get("id"));
            if(inventory.getUpcId().equalsIgnoreCase(varId)){
                String imageId = String.valueOf(var.getAsJsonObject().get("image_id"));
                String imageUrl = ShopifyMapper.getImageUrl(product,imageId);
                if(imageUrl != null){
                    inventory.setImageUrl(imageUrl.replace("\"",""));
                }
            }
        });
    }


    private void updateStock(JsonObject response, Inventory inventory) {
        JsonObject product = ShopifyMapper.getProduct(response);
        if(product == null){
            return;
        }
        JsonArray variants = ShopifyMapper.getVariants(product);
        if(variants !=null ){

            variants.forEach(variant -> {
                boolean noVariant = false;

                if(variants.size() == 1){
                    noVariant = true;
                }
               JsonObject variantObject = variant.getAsJsonObject();
               ShopifyMapper.updateProductStorageforVariant(variantObject,inventory,noVariant);
           });
        }
    }


    public JsonObject callShopify(String url) throws UnirestException {
        HttpResponse<String> response = apiProxyService.executeGet(url);
        Gson gson = new Gson();
        return gson.fromJson(response.getBody(), JsonObject.class);
    }

    public List<Inventory> fetchProductFromShopify(String productId) {

        dynamicProductUrl = productUrl.replace("$$", "/"+productId+".json");
        dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
        try {
            Thread.sleep(3000);
            JsonObject response = callShopify(dynamicProductUrl);
            if(ShopifyMapper.isErrorResponse(response)){
                dynamicProductUrl = productUrl.replace("products$$", "variants/"+productId+".json");
                dynamicProductUrl = protocol  + shopifyToken + "@"+shopifyDomain + dynamicProductUrl;
                Thread.sleep(3000);
                response = callShopify(dynamicProductUrl);
                if(ShopifyMapper.isErrorResponse(response)){
                    return null;
                }
                productId = ShopifyMapper.getProductIdFromVariantCall(response);
                return fetchProductFromShopify(productId);
            }
            return ShopifyMapper.convertJSONToInventory(response);
        } catch (UnirestException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean archieveOrder(String orderId){
        String orderUrl = singleOrderUrl.replaceAll("[$]", "");
        orderUrl += orderId + "/close.json";
        String url = protocol + shopifyDomain + orderUrl;
        try {
            Map<String ,String> headers = new HashMap<>();
            headers.put("X-Shopify-Access-Token", shortToken);
            apiProxyService.executePost(url, POSConstants.EMPTY, headers);
            return true;
        } catch (UnirestException e) {
            System.out.println(e.getMessage());
        }
        return false;

    }

    public Map<String, Object> fetchPage(int first, String after) {
        LocalDate from = LocalDate.now(ZoneOffset.UTC).minusDays(30);

        // exclude refunded + cancelled, keep unfulfilled only
        String search = String.format(
                "processed_at:>=%s fulfillment_status:unfulfilled -status:cancelled -financial_status:refunded -financial_status:partially_refunded",
                from
        );
        Map<String, Object> vars = new HashMap<>();
        vars.put("first", Math.min(Math.max(first, 1), 250)); // 1..250
        vars.put("after", after);
        vars.put("query", search);

        Map<String, Object> resp = shopifyClient.post(ShopifyQueries.UNFULFILLED_ORDERS, vars).block();
        Map<String, Object> data = (Map<String, Object>) resp.get("data");
        return (Map<String, Object>) data.get("orders");
    }


    public List<Map<String, Object>> fetchAll(int pageSize) {
        pageSize = Math.min(Math.max(pageSize, 1), 250);

        List<Map<String, Object>> all = new ArrayList<>();
        String cursor = null;
        boolean hasNext = true;

        while (hasNext) {
            Map<String, Object> page = fetchPage(pageSize, cursor);
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) page.get("nodes");
            if (nodes == null || nodes.isEmpty()) break;

            all.addAll(nodes);

            Map<String, Object> pageInfo = (Map<String, Object>) page.get("pageInfo");
            hasNext = (Boolean) pageInfo.get("hasNextPage");
            cursor = (String) pageInfo.get("endCursor");

            // polite throttle: tiny pause between pages
            try { Thread.sleep(200L); } catch (InterruptedException ignored) {}
        }
        return all;
    }
}
