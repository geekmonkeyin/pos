package com.gkmonk.pos.controller.bookshopify;

import com.gkmonk.pos.model.legacy.ShopifyFulfillment;
import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.services.orders.OrderCacheServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/order/bookshopify")
public class BookOrderController {

    @Autowired
    private ShopifyServiceImpl shopifyServiceImpl;
    @Autowired
    private OrderCacheServiceImpl orderCacheService;

    @RequestMapping("")
    public ModelAndView bookShopify() {
        ModelAndView model = new ModelAndView();
        //fetch shopify orders.
        List<ShopifyOrders> dispatchedOrders = shopifyServiceImpl.fetchOrderByStatus(OrderStatus.DISPATCHED);
        if(dispatchedOrders == null || dispatchedOrders.isEmpty()) {
           dispatchedOrders = new ArrayList<>();
        }
        dispatchedOrders.stream().filter(dispatchedOrder -> dispatchedOrder.getItems() == null || dispatchedOrder.getItems().isEmpty()).forEach(dispatchedOrder -> {
             for(ShopifyFulfillment shopifyFulfillment : dispatchedOrder.getFulfillments()) {
                 if( dispatchedOrder.getItems() == null){
                        dispatchedOrder.setItems(new ArrayList<>());
                 }
                 if(shopifyFulfillment.getLine_items() != null && !shopifyFulfillment.getLine_items().isEmpty()) {
                     dispatchedOrder.getItems().addAll(shopifyFulfillment.getLine_items());
                 }
             }
        });
        model.setViewName("bookshopify");
        model.addObject("fetchedOrders",dispatchedOrders);
        return model;
    }

    @RequestMapping("/fetchorders")
    public ResponseEntity<String> fetchOrders() {
        //fetch shopify orders.
        Map<String, Object> unfulfilledOrders =  shopifyServiceImpl.fetchPage(5, null);
        if(unfulfilledOrders != null && !unfulfilledOrders.isEmpty()) {
            return ResponseEntity.ok("Fetched orders");
        }
        return ResponseEntity.badRequest().body("No orders found");
    }

    @QueryMapping
    public Map<String, Object> unfulfilledOrders(
            @Argument Integer first,
            @Argument String after
    ) {
        int pageSize = (first == null) ? 50 : first;
        Map<String, Object> unfulfilledOrders =  shopifyServiceImpl.fetchPage(pageSize, after);
        return unfulfilledOrders;
    }



    @QueryMapping
    public List<Map<String, Object>> unfulfilledOrdersAll(
            @Argument Integer pageSize
    ) {
        int size = (pageSize == null) ? 250 : pageSize;
        return shopifyServiceImpl.fetchAll(size);
    }


}
