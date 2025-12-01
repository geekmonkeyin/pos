package com.gkmonk.pos.controller.outward;

import com.gkmonk.pos.model.GSTRate;
import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.sorpo.Outward;
import com.gkmonk.pos.model.sorpo.OutwardStatus;
import com.gkmonk.pos.services.GSTServiceImpl;
import com.gkmonk.pos.services.InventoryServiceImpl;
import com.gkmonk.pos.services.sorpo.OutwardServiceImpl;
import io.micrometer.core.instrument.internal.TimedExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/outward")
@Slf4j
public class OutwardController {

    @Autowired
    private InventoryServiceImpl inventoryService;
    @Autowired
    private GSTServiceImpl gstService;
    @Autowired
    private OutwardServiceImpl outwardService;

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
                log.info("Getting tax rate from db for product type: {}", product.getProductType());
                GSTRate gstRate = gstService.findGSTByCategory(product.getProductType());
                if(gstRate != null) {
                    product.setGstRate(gstRate.getRate());
                    product.setHsnCode(gstRate.getHsn());
                }
            });
    }

    @PostMapping("/save")
    public ResponseEntity<Outward> save(@RequestBody Outward outward) {
        Instant nowUtc = Instant.now();
        ZonedDateTime nowIST = nowUtc.atZone(ZoneId.of("Asia/Kolkata"));
        if(outward.getId() == null){
            outward.setCreatedAt(nowIST.toInstant());
        }else{
            outward.setUpdatedAt(nowIST.toInstant());
        }
        return ResponseEntity.ok(outwardService.saveOutward(outward));
    }
}