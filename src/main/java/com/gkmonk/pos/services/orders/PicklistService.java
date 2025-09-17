package com.gkmonk.pos.services.orders;


import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.model.order.PickItem;
import com.gkmonk.pos.services.InventoryServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyDBServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PicklistService {
    private final Map<String, PickItem> store = new ConcurrentHashMap<>();

    @Autowired
    private ShopifyDBServiceImpl shopifyDBService;

    @Autowired
    private InventoryServiceImpl inventoryService;

   // @PostConstruct
    public void seed() {

        add(new PickItem("1", "VAR123", "Naruto Uzumaki Figurine", 2,
                "https://images.unsplash.com/photo-1600490036275-35f7ae9f1e6a?q=80&w=600&auto=format&fit=crop",
                PickItem.Priority.HIGH, false, "Fragile: add extra bubble wrap.","SB218"));
        add(new PickItem("2", "VAR456", "One Piece LED Lamp", 1,
                "https://images.unsplash.com/photo-1542219550-37153d387c6c?q=80&w=600&auto=format&fit=crop",
                PickItem.Priority.MEDIUM, true, "Include USB cable.","SB218"));
        add(new PickItem("3", "VAR789", "Demon Slayer Keychain Set", 5,
                "https://images.unsplash.com/photo-1520975922474-5f22873c4a5c?q=80&w=600&auto=format&fit=crop",
                PickItem.Priority.LOW, false, "Small mailer ok.","R218"));
        add(new PickItem("4", "VAR101", "Jujutsu Kaisen Plush", 3,
                "https://images.unsplash.com/photo-1537633552985-df8429e8048b?q=80&w=600&auto=format&fit=crop",
                PickItem.Priority.HIGH, false, "Do not fold the tag.","SB233"));
    }

    public void add(PickItem item) {
        if(store.containsKey(item.getVariationId())){
            //update quantity
            PickItem existing = store.get(item.getVariationId());
            existing.setQty(existing.getQty() + item.getQty());
            item = existing;
        }else {
            item.setStorage(getStorage(item.getVariationId()));
        }
            store.put(item.getVariationId(), item);

    }

    private String getStorage(String variationId) {
        List<Inventory>  inventories =  inventoryService.fetchProductsUsingId(variationId);
        return (inventories != null && !inventories.isEmpty()) ? StringUtils.isNotBlank(inventories.get(0).getStorage())? inventories.get(0).getStorage() : "N/A": "N/A";
    }

    public List<PickItem> list(String query) {
        if(store.isEmpty()){

            List<ShopifyOrders> shopifyOrders = shopifyDBService.getOrderByStatus(OrderStatus.DISPATCHED);
            List<PickItem> pickItems = ShopifyMapper.convertShopifyOrdersToPickItems(shopifyOrders);
            pickItems.forEach(this::add);
        }
        return store.values().stream()
                .sorted(Comparator.comparing(PickItem::isPicked) // pending first
                .thenComparing(PickItem::getStorage)
                .thenComparing(PickItem::getPriority).reversed())
                .filter(it -> {
                    if (StringUtils.isBlank(query)) return true;
                    String q = query.toLowerCase();
                    return it.getName().toLowerCase().contains(q) ||
                            it.getVariationId().toLowerCase().contains(q);
                })
                .collect(Collectors.toList());
    }

    public void toggle(String id) {
        PickItem it = store.get(id);
        if (it != null) it.setPicked(!it.isPicked());
    }

    public void markAllPicked(Collection<String> ids) {
        ids.forEach(id -> {
            PickItem it = store.get(id);
            if (it != null) it.setPicked(true);
        });
    }

    public long countPicked(Collection<PickItem> items) {
        return items.stream().filter(PickItem::isPicked).count();
    }
}
