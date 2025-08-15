package com.gkmonk.pos.services.notification;

import com.gkmonk.pos.model.notification.OrderNotification;
import com.gkmonk.pos.repo.notification.NotificationRepo;
import com.gkmonk.pos.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl {

    @Autowired
    private NotificationRepo notificationRepo;

    public OrderNotification findByOrderId(String orderId) {
        Optional<OrderNotification> notification = notificationRepo.findById(orderId);
        return notification.orElse(null);
    }

    public void save(OrderNotification orderNotification) {
        notificationRepo.save(orderNotification);
    }

    public Optional<List<OrderNotification>> findOrdersWithPendingNotifications() {
        return notificationRepo.findAllPendingNotfication(DateUtils.getNextUpdate(0));
    }
}
