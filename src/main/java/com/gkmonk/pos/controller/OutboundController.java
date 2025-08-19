package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.logs.TaskStatusType;
import com.gkmonk.pos.model.logs.TaskType;
import com.gkmonk.pos.model.notification.NotificationType;
import com.gkmonk.pos.model.notification.OrderNotification;
import com.gkmonk.pos.model.outbound.OutboundOrder;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.pod.services.PODServiceImpl;
import com.gkmonk.pos.services.OrderReportsServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyServiceImpl;
import com.gkmonk.pos.services.logs.TaskLogsServiceImpl;
import com.gkmonk.pos.services.marketing.MarketinServiceImpl;
import com.gkmonk.pos.services.notification.NotificationServiceImpl;
import com.gkmonk.pos.services.outbound.OutboundServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/order/outbound")
public class OutboundController {

        private List<OutboundOrder> outboundOrders;
        private List<String> failedOrder;
        @Autowired
        private PODServiceImpl podService;
        @Autowired
        private OutboundServiceImpl outboundService;
        @Autowired
        private OrderReportsServiceImpl orderReportsService;
        @Autowired
        private MarketinServiceImpl marketinService;
        @Autowired
        private NotificationServiceImpl notificationService;
        @Autowired
        private TaskLogsServiceImpl taskLogsService;
        @Autowired
    private ShopifyServiceImpl shopifyServiceImpl;

    @GetMapping("/addAWBToManifest/{awb}")
    public ResponseEntity<List<OutboundOrder>> addAWBToManifestAsync(@PathVariable("awb") String awb) {
        Thread thread = new Thread() {

            @Override
            public void run() {

                initializeOutboundOrders();
                PackedOrder packedOrder = podService.findByAWB(awb);
                if(packedOrder != null){
                    updateOutboundOrders(packedOrder,awb);
                    updateOrderReports(packedOrder);
                }else{
                    initializeFailedOrders();
                    failedOrder.add(awb);
                }
            }
        };
        thread.start();
    return ResponseEntity.ok(outboundOrders);
    }

    private void updateOrderReports(PackedOrder packedOrder) {
        orderReportsService.saveReport(packedOrder);
    }

    private void initializeFailedOrders() {
        if(failedOrder == null){
            failedOrder = new ArrayList<>();
        }
    }

    private void updateOutboundOrders(PackedOrder packedOrder, String awb) {
        OutboundOrder outboundOrder = new OutboundOrder(awb);
        outboundOrder.setOrderId(packedOrder.getOrderId());
        outboundOrder.setCourierCompany(packedOrder.getCourierCompany());
        outboundOrder.setCustomerName(packedOrder.getCustomerName());
        //already manifested?
        OutboundOrder manifested =  outboundService.findByAWB(awb);
        if(manifested != null) {
            outboundOrder.setPickupDate(manifested.getPickupDate());
        }
        outboundOrders.add(outboundOrder);

    }

    private void initializeOutboundOrders() {
        if(outboundOrders == null){
            outboundOrders = new ArrayList<>();
        }
    }

    @GetMapping("/existingOrders")
    public ResponseEntity<List<OutboundOrder>> existingOrders() {
        if(outboundOrders == null){
            outboundOrders = new ArrayList<>();
        }
        return ResponseEntity.ok(outboundOrders);
    }

        @GetMapping("")
        public ModelAndView getOutboundPage() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("outbound");
            if(outboundOrders == null){
                outboundOrders = new ArrayList<>();
            }
            modelAndView.addObject("outboundOrders", outboundOrders);
            return modelAndView;
        }


    @PostMapping("/savemanifest")
    public ResponseEntity<String> saveManifest(@RequestBody Map<String, String> payload) {

        String dateTime = payload.get("dateTime");
        String courier = payload.get("courier");
        String pickedUpBy = payload.get("pickedUpBy");
        String phoneNo = payload.get("phoneNo");
        String deviceName = payload.get("deviceName");

            // Log the start of the task

            if(outboundOrders == null || outboundOrders.isEmpty()){
                return ResponseEntity.ok("No orders to save in manifest");
            }
            // Logic to save the manifest
        String metaData = "Courier: "+ courier + ", Total Orders:"+ outboundOrders.size() +", Picked Up By: " + pickedUpBy + ", Phone No: " + phoneNo + ", Device: " + deviceName;

        taskLogsService.addLogs(TaskType.OUTBOUND_ORDER.name(), TaskStatusType.START.name(), metaData,LocalDate.now().toString());

        outboundOrders.forEach(outboundOrder -> {
                    outboundOrder.setPickupDate(dateTime);
                outboundOrder.setCourierCompany(courier);
                outboundOrder.setPickedUpBy(pickedUpBy);
                outboundOrder.setPhoneNo(phoneNo);
                outboundOrder.setManifestId(LocalDate.now() + "-" + courier);
                sendWhatsappMessage(outboundOrder);
            });

            outboundService.saveManifest(outboundOrders);

            // For now, just clear the list after saving
        taskLogsService.addLogs(TaskType.OUTBOUND_ORDER.name(), TaskStatusType.COMPLETED.name(), metaData,LocalDate.now().toString());

            return ResponseEntity.ok("Manifest saved successfully");
        }

    private void sendWhatsappMessage(OutboundOrder outboundOrder) {

        Optional<List<PackedOrder>> orderByAWB = orderReportsService.findByAWB(outboundOrder.getAwb());
        if(orderByAWB.isPresent()){
            PackedOrder orderDetail  = orderByAWB.get().stream().findFirst().orElse(null);
            if(orderDetail != null){
                orderDetail.setCourierCompany(outboundOrder.getCourierCompany());
                OrderNotification orderNotification = getOrderNotification(orderDetail);
                marketinService.sendShippingUpdate(orderDetail,orderNotification);
                saveOrderNotification(orderNotification);
            }

        }
    }

    private void saveOrderNotification(OrderNotification orderNotification) {
        notificationService.save(orderNotification);
    }

    private OrderNotification getOrderNotification(PackedOrder orderDetail) {
        OrderNotification orderNotification =  notificationService.findByOrderId(orderDetail.getOrderId());
        if(orderNotification == null){
            orderNotification = new OrderNotification();
            orderNotification.setOrderId(orderDetail.getOrderId());
            orderNotification.setLastUpdated(Instant.now());
            orderNotification.setCourierCompany(orderDetail.getCourierCompany());
            orderNotification.setCustomerPhone(orderDetail.getCustomerInfo().getPhoneNo());
        }
        return orderNotification;
    }

    @GetMapping("/clearData")
        public ResponseEntity<String> clearData() {
            if(outboundOrders != null) {
                outboundOrders.clear();
            }
            return ResponseEntity.ok("Outbound data cleared successfully");
        }

    @Scheduled(cron = "0 35 14 * * ?") // Runs daily at 2 PM
    @GetMapping("/syncPendingNotifications")
    public void syncOutboundOrders() {
        System.out.println("*****************Auto Scheduler *****************************");
        Optional<List<OrderNotification>> orderNotifications = notificationService.findOrdersWithPendingNotifications();
        if (orderNotifications.isPresent() && !orderNotifications.get().isEmpty()) {
            orderNotifications.get().forEach(orderNotification -> {
               NotificationType targetNotification =  marketinService.sendNextUpdates(orderNotification);
               if(targetNotification != null) {


                   Optional<List<PackedOrder>> packedOrderList = orderReportsService.findByAWB(orderNotification.getAwb());
                   //        marketinService.sendNotification(targetNotification,);
                   if (packedOrderList.isPresent() && !packedOrderList.get().isEmpty()) {
                       marketinService.sendNotification(orderNotification, targetNotification, targetNotification.getNextNotification(), packedOrderList.get().get(0));
                       if (NotificationType.FEEDBACK.name().equalsIgnoreCase(orderNotification.getNextStatus())) {
                           boolean archieved = shopifyServiceImpl.archieveOrder(orderNotification.getOrderId());
                           if (archieved) {
                               orderNotification.setArchieved(true);
                           }
                       }
                   }

                   saveOrderNotification(orderNotification);
               }
            });
        }
    }

}
