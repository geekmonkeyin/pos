package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.repo.shopify.ShopifyOrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShopifyDBServiceImpl {

    @Autowired
    private ShopifyOrderRepo shopifyOrderRepo;

    public ShopifyOrders saveToDb(ShopifyOrders shopifyOrder) {
        return shopifyOrderRepo.save(shopifyOrder);
    }

    public List<ShopifyOrders> saveToDb(List<ShopifyOrders> shopifyOrders) {
        return shopifyOrderRepo.saveAll(shopifyOrders);
    }


    public List<ShopifyOrders> getOrderByStatus(OrderStatus orderStatus) {
        Optional<List<ShopifyOrders>> shopifyOrders = shopifyOrderRepo.findByStatus(orderStatus.name());
        return shopifyOrders.orElse(null);
    }
}
