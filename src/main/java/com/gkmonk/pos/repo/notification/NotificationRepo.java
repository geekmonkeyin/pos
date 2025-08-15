package com.gkmonk.pos.repo.notification;

import com.gkmonk.pos.model.notification.OrderNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepo extends MongoRepository<OrderNotification,String> {
    @Query("{ 'nextUpdate' : { $lte: ?0 }}")
    Optional<List<OrderNotification>> findAllPendingNotfication(String nextUpdate);
}
