package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.repo.shopify.ShopifyOrderRepo;
import com.gkmonk.pos.services.token.AllCredentialsService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopifyDBServiceImpl {

    @Autowired
    private ShopifyOrderRepo shopifyOrderRepo;

    @Autowired
    private AllCredentialsService credentialsService;

    private MongoTemplate cloudMongoTemplate;

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

    public ShopifyOrders getOrderByName(String orderNo) {
            Optional<ShopifyOrders> shopifyOrders = shopifyOrderRepo.findByName(orderNo);
            return shopifyOrders.orElse(null);
    }

    
    private MongoTemplate getMongoCloudTemplate() {
        if (cloudMongoTemplate == null) {
            synchronized (this) {
                if (cloudMongoTemplate == null) {
                    Optional<String> mongoUri = credentialsService.getMongoUri();
                    if (mongoUri.isPresent()) {
                        String databaseName = credentialsService.getMongoDatabaseName().orElse("geekai");
                        MongoClient mongoClient = MongoClients.create(mongoUri.get());
                        SimpleMongoClientDatabaseFactory databaseFactory = new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
                        cloudMongoTemplate = new MongoTemplate(databaseFactory);
                    }
                }
            }
        }
        return cloudMongoTemplate;
    }

    public ShopifyOrders getOrderById(String orderId) {
        Optional<ShopifyOrders> shopifyOrders = shopifyOrderRepo.findById(orderId);
        return shopifyOrders.orElse(null);
    }

    public void saveToCloudDb(ShopifyOrders shopifyOrder) {
        MongoTemplate mongoTemplate = getMongoCloudTemplate();
        if (mongoTemplate != null) {
            mongoTemplate.save(shopifyOrder, "shopifyorders_cloud");
        }
    }
}
