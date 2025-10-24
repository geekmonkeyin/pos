package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.Customer;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.services.externalapi.APIProxyService;
import com.gkmonk.pos.utils.POSConstants;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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
    private ShopifyDBServiceImpl shopifyDBService;

    @Autowired
    private APIProxyService apiProxyService;

    private String shortToken;

    @PostConstruct
    void init(){
        shopifyToken = System.getenv().get("shopify_token");
        shortToken  = System.getenv().get("shopify_shortToken");
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
            if(inventory.getProductVariantId().equalsIgnoreCase(varId)){
                String imageId = String.valueOf(var.getAsJsonObject().get("image_id"));
                String imageUrl = isImageNull(imageId) ? product.getAsJsonObject("image").get("src").getAsString() : ShopifyMapper.getImageUrl(product,imageId);
                if(imageUrl != null){
                    inventory.setImageUrl(imageUrl.replace("\"",""));
                }
            }
        });
    }

    private boolean isImageNull(String image) {
        return "null".equalsIgnoreCase(image) || image == null;
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
            List<Inventory> inventoryList =  ShopifyMapper.convertJSONToInventory(response);
            final JsonObject resp = response;
            inventoryList.forEach( inv -> {
                updateImages(resp,inv);
            });
            return inventoryList;
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

    public boolean updateShopifyInventory(String productId, String variantId) {
        //todo
         return false;
    }

    public List<ShopifyOrders> fetchOrderByStatus(OrderStatus status) {
        return shopifyDBService.getOrderByStatus(status);
    }

    public List<Customer> getCustomerByPhoneNo(String phoneNumber) throws Exception {
        Mono<String> customer =  shopifyClient.findCustomersByPhone(phoneNumber);
        log.info(customer.toString());
        return  ShopifyMapper.parseCustomers(customer.block());

    }
}
