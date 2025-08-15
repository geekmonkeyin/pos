package com.gkmonk.pos.pod.services;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.utils.MapperUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PODServiceImpl {


    @Value("${mongo.po.uri}")
    private String poMongoURI;
    private MongoTemplate shopifyMongoTemplate;

    @PostConstruct
    public void init() {
        shopifyMongoTemplate = shopifyMongoTemplate();
    }

    public PackedOrder findByAWB(String awb) {
        PackedOrder packedOrder = findByCriteria(Criteria.where("fulfillments.tracking_number").is(awb));
        if (packedOrder != null) {
            //update image
            packedOrder.setAwb(awb);
        }
        return packedOrder;
    }

    public PackedOrder findByCriteria(Criteria criteria){
        Query query = new Query();
        query.addCriteria(criteria);
        Document result = shopifyMongoTemplate.findOne(query, Document.class, "shopifyorders");
        return result != null ? mapDocumentToPackedOrder(result) : null;
    }

    private PackedOrder mapDocumentToPackedOrder(Document result) {
        PackedOrder packedOrder = new PackedOrder();
        packedOrder.setOrderId(result.getString("_id"));
        packedOrder.setCustomerName(MapperUtils.getCustomerName(result));
        packedOrder.setCustomerInfo(MapperUtils.getContactInfo(result));
        packedOrder.setTotalAmount(Double.valueOf(result.getString("total_price")));
        packedOrder.setProductDetails(MapperUtils.getProductDetailsFromDocument(result));
        packedOrder.setPaymentMode(result.getBoolean("cod") ? "COD" : "Prepaid");
        return packedOrder;
    }

    public MongoDatabaseFactory shopifyMongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(shopifyMongoClient(), "livemachine");
    }

    public MongoTemplate shopifyMongoTemplate() {
        return new MongoTemplate(shopifyMongoDatabaseFactory());
    }

    public MongoClient shopifyMongoClient() {
        return MongoClients.create(poMongoURI);
    }

    public PackedOrder findByOrderId(String orderId) {
        return findByCriteria(Criteria.where("_id").is(orderId));
    }


}

