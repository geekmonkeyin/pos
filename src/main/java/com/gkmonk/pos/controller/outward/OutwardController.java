package com.gkmonk.pos.controller.outward;

import com.gkmonk.pos.model.GSTRate;
import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.services.GSTServiceImpl;
import com.gkmonk.pos.services.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/outward")
public class OutwardController {

    @Autowired
    private InventoryServiceImpl inventoryService;
    @Autowired
    private GSTServiceImpl gstService;

    @GetMapping("/search")
    public ResponseEntity<List<Inventory>> searchProducts(@RequestParam("query") String query) {

        List<Inventory> products = inventoryService.fetchProductsUsingId(query);
        updateProductsTaxRate(products);

        List<Inventory> filteredProducts = products.stream().filter(p -> p.getUpcId().equalsIgnoreCase(query)).toList();
        if (filteredProducts.size() == 1) {
            return ResponseEntity.ok(filteredProducts);
        }
        return ResponseEntity.ok(products);
    }

    private void updateProductsTaxRate(List<Inventory> products) {

            products.forEach(product -> {
                GSTRate gstRate = gstService.findGSTByCategory(product.getProductType());
                product.setGstRate(gstRate.getRate());
                product.setHsnCode(gstRate.getHsn());
            });
    }
}