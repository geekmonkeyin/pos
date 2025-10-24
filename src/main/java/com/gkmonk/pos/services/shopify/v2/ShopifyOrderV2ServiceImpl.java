package com.gkmonk.pos.services.shopify.v2;

import com.gkmonk.pos.model.counter.ShopifyCursor;
import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.repo.counter.ShopifyCursorRepo;
import com.gkmonk.pos.services.shopify.ShopifyClient;
import com.gkmonk.pos.services.shopify.ShopifyDBServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyMapper;
import com.gkmonk.pos.services.shopify.ShopifyQueries;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ShopifyOrderV2ServiceImpl {

    @Autowired
    private ShopifyClient shopifyClient;
    @Autowired
    private ShopifyDBServiceImpl shopifyDBService;
    @Autowired
    private ShopifyCursorRepo cursorRepository;


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

    public Map<String, Object> fetchPage(int first, String after) {
        LocalDate from = LocalDate.now(ZoneOffset.UTC).minusDays(30);

        // exclude refunded + cancelled, keep unfulfilled only
        String search = String.format(
                "processed_at:>=%s fulfillment_status:unfulfilled -status:cancelled -financial_status:refunded -financial_status:partially_refunded",
                from
        );
        Map<String, Object> vars = new HashMap<>();
        vars.put("first", Math.min(Math.max(first, 1), 250)); // 1..250
        vars.put("after", getShopifyCursor());
        vars.put("query", search);

        Map resp = shopifyClient.post(ShopifyQueries.UNFULFILLED_ORDERS, vars).block();
        Map<String, Object> data = (Map<String, Object>) resp.get("data");
        Map<String, Object> unfulfilledOrders = (Map<String, Object>) data.get("orders");
        List<ShopifyOrders> shopifyOrders = convertToShopifyOrders(unfulfilledOrders);
        fulfilledOrders(unfulfilledOrders);
        saveToDb(shopifyOrders);
        saveCursor((Map)unfulfilledOrders.get("pageInfo"));
        return unfulfilledOrders;

    }

    private String getShopifyCursor() {
        Optional<ShopifyCursor> shopifyCursor = cursorRepository.findById("shopify");
        return shopifyCursor.map(ShopifyCursor::getCursor).orElse(null);
    }

    private void saveCursor(Map pageInfo) {
        if(pageInfo != null){
            String endCursor = (String) pageInfo.get("endCursor");
            log.info("endCursor: {}", endCursor);
            ShopifyCursor shopifyCursor = new ShopifyCursor();
            shopifyCursor.setCursor(endCursor);
            shopifyCursor.setStore("shopify");
            shopifyCursor.setUpdatedDate(LocalDateTime.now().toString());
            cursorRepository.save(shopifyCursor);
        }
    }

    private List<ShopifyOrders> convertToShopifyOrders(Map<String, Object> unfulfilledOrders) {
        log.info("unfulfilledOrders: {}", unfulfilledOrders);
        if(unfulfilledOrders != null){
            Object orders = unfulfilledOrders.get("nodes");
            if(orders instanceof List){
                List<?> orderList = (List<?>) orders;
                if(!orderList.isEmpty() ){
                    return ShopifyMapper.convertToShopifyOrders((List<Map<String, Object>>) orderList);
                }
            }
        }
        return List.of();
    }

    private void saveToDb(List<ShopifyOrders> shopifyOrders) {
        shopifyDBService.saveToDb(shopifyOrders);
    }


    private void fulfilledOrders(Map<String, Object> unfulfilledOrders) {
        //fulfilledOrders(unfulfilledOrders);
    }


}
