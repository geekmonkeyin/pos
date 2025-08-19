package com.gkmonk.pos.services.orders;

import com.gkmonk.pos.services.shopify.ShopifyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class OrderCacheServiceImpl {

    @Autowired
    private ShopifyServiceImpl shopifyServiceImpl;


    public void getAllFulfilledOrders() {

    }

}
