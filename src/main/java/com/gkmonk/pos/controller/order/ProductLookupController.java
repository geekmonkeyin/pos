package com.gkmonk.pos.controller.order;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.services.shopify.v2.ShopifyOrderV2ServiceImpl;
import com.gkmonk.pos.services.shopify.v2.ShopifyProductV2ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/order/product-lookup")
public class ProductLookupController {

    @Autowired
    private ShopifyProductV2ServiceImpl shopifyService;

    @GetMapping("/productid")
    public ResponseEntity<List<Inventory>> getProductsByProductId(@RequestParam("query") String productId) {
        List<Inventory> inventory = shopifyService.fetchProduct(productId);
        return ResponseEntity.ok(inventory);
    }
}
