package com.gkmonk.pos.services.shopify.v2;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.services.shopify.ShopifyClient;
import com.gkmonk.pos.services.shopify.ShopifyDBServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ShopifyProductV2ServiceImpl {

    @Autowired
    private ShopifyClient shopifyClient;
    @Autowired
    private ShopifyDBServiceImpl shopifyDBService;

    public List<Inventory> fetchProduct(String productId) {
        productId = "gid://shopify/Product/"+productId; // Replace with your real product GID
        Mono<Map> products = shopifyClient.findProductById(productId);
        Map responseProducts = products.block();
        Map data = (Map) responseProducts.get("data");
        Map product  = (Map) data.get("product");
        return getInventoryFromMap(product);
    }

    private List<Inventory> getInventoryFromMap(Map product) {
        int size = ((List)((Map)product.get("variants")).get("edges")).size();
        List<Inventory> inventories = new ArrayList<>();
        for(int i = 0; i < size; i++ ) {
            Map variant = (Map)((Map) ((List)((Map)product.get("variants")).get("edges")).get(i)).get("node");
            Inventory inventory = new Inventory();
            inventory.setProductTitle(product.get("title").toString() + " - " + variant.get("title").toString());
            inventory.setDescription(product.get("description").toString());
            inventory.setProductGID(variant.get("id").toString());
            inventory.setShopifyQuantity(Integer.parseInt(variant.get("inventoryQuantity").toString()));
            inventory.setPrice(Double.parseDouble(variant.get("price").toString()));
            inventory.setUpcId(ShopifyMapper.getIdFromGid(variant.get("id").toString()));
            inventory.setProductVariantId(inventory.getUpcId());
            inventory.setProductId(ShopifyMapper.getIdFromGid(product.get("id").toString()));
            inventories.add(inventory);
        }
        return inventories;
    }

}
