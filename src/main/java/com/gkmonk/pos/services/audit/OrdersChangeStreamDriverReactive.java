//package com.gkmonk.pos.services.audit;
//
//import com.mongodb.client.model.Aggregates;
//import com.mongodb.client.model.changestream.FullDocument;
//import com.mongodb.reactivestreams.client.MongoClient;   // reactive driver
//import org.bson.Document;
//import org.bson.conversions.Bson;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class OrdersChangeStreamDriverReactive {
//
//    private final MongoClient client; // injected by Spring Boot's reactive starter
//
//    public OrdersChangeStreamDriverReactive(MongoClient client) {
//        this.client = client;
//    }
//
//    public Flux<Document> ordersStream() {
//        List<Bson> pipeline = List.of(
//                Aggregates.match(new Document("operationType",
//                        new Document("$in", List.of("insert","update","replace"))))
//        );
//
//        return Flux.from(
//                client.getDatabase("mydb")
//                        .getCollection("orders")
//                        .watch(pipeline)
//                        .fullDocument(FullDocument.UPDATE_LOOKUP)
//                        .maxAwaitTime(5, TimeUnit.SECONDS)   // ✅ driver-level, works
//        ).map(evt -> evt.getFullDocument()); // or handle the whole ChangeStreamDocument
//    }
//}
