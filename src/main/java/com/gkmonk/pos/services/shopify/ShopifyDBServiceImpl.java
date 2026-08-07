package com.gkmonk.pos.services.shopify;

import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.repo.shopify.ShopifyOrderRepo;
import com.gkmonk.pos.services.token.AllCredentialsService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShopifyDBServiceImpl {

    @Autowired
    private ShopifyOrderRepo shopifyOrderRepo;

    @Autowired
    private AllCredentialsService credentialsService;

    @Value("${spring.data.mongodbcloud.uri:}")
    private String cloudMongoUri;

    @Value("${spring.data.mongodbcloud.database:geekai}")
    private String cloudMongoDatabase;

    private MongoTemplate cloudMongoTemplate;

    private static final String CLOUD_SHOPIFY_COLLECTION = "shopifyorders_cloud";

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

    public ShopifyOrders getOrderById(String orderId) {
        Optional<ShopifyOrders> shopifyOrders = shopifyOrderRepo.findById(orderId);
        return shopifyOrders.orElse(null);
    }

    public ShopifyOrders getOrderForCloudDb(String orderId) {
        if (!StringUtils.hasText(orderId)) {
            return null;
        }

        ShopifyOrders shopifyOrder = getOrderById(orderId);
        if (shopifyOrder != null) {
            return shopifyOrder;
        }

        shopifyOrder = findInDatabase(orderId, "livemachine", "shopifyorders");
        if (shopifyOrder != null) {
            return shopifyOrder;
        }

        return findInDatabase(orderId, "live_orders", "shopifyorders");
    }

    private ShopifyOrders findInDatabase(String orderId, String databaseName, String collectionName) {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoTemplate mongoTemplate = new MongoTemplate(
                    new SimpleMongoClientDatabaseFactory(mongoClient, databaseName)
            );
            Query query = new Query(Criteria.where("id").is(orderId));
            return mongoTemplate.findOne(query, ShopifyOrders.class, collectionName);
        } catch (Exception ex) {
            return null;
        }
    }

    public void saveToCloudDb(ShopifyOrders shopifyOrder) {
        MongoTemplate mongoTemplate = getMongoCloudTemplate();
        if (mongoTemplate != null && shopifyOrder != null) {
            mongoTemplate.save(shopifyOrder, CLOUD_SHOPIFY_COLLECTION);
        }
    }

    private MongoTemplate getMongoCloudTemplate() {
        if (cloudMongoTemplate == null) {
            synchronized (this) {
                if (cloudMongoTemplate == null) {
                    String mongoUri = StringUtils.hasText(cloudMongoUri)
                            ? cloudMongoUri
                            : credentialsService.getMongoUri().orElse(null);
                    if (StringUtils.hasText(mongoUri)) {
                        String databaseName = StringUtils.hasText(cloudMongoDatabase)
                                ? cloudMongoDatabase
                                : credentialsService.getMongoDatabaseName().orElse("geekai");
                        MongoClient mongoClient = MongoClients.create(mongoUri);
                        SimpleMongoClientDatabaseFactory databaseFactory = new SimpleMongoClientDatabaseFactory(mongoClient, databaseName);
                        cloudMongoTemplate = new MongoTemplate(databaseFactory);
                    }
                }
            }
        }
        return cloudMongoTemplate;
    }
}
