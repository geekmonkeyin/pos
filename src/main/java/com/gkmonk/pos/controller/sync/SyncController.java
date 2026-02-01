//package com.gkmonk.pos.controller.sync;
//
//import com.gkmonk.pos.constants.BackendConstants;
//import com.gkmonk.pos.model.OrderCourierStatus;
//import com.gkmonk.pos.services.courier.IOrdersSyncService;
//import com.gkmonk.pos.services.db.impl.DBCourierSyncServiceImpl;
//import com.gkmonk.pos.services.log.LogService;
//import com.gkmonk.pos.services.poddetails.PendingOrderServiceImpl;
//import com.gkmonk.pos.utils.StringUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Controller
//@RequestMapping("/v1/order/sync")
//@Slf4j
//public class SyncController {
//
//    @Autowired
//    List<IOrdersSyncService> courierServices;
//    @Autowired
//    DBCourierSyncServiceImpl dbCourierSyncService;
//    @Autowired
//    PendingOrderServiceImpl pendingOrderService;
//    @Autowired
//    private LogService logService;
//
//    @GetMapping("/custom")
//    public ModelAndView searchCustom(){
//        ModelAndView modelAndView = new ModelAndView("customsearch");
//        return modelAndView;
//    }
//
//
//    @Scheduled(fixedRate = 6 * 60 * 60 * 1000L)  // 6 hours in ms
//    public void scheduleTask() {
//        System.out.println("verified status");
//        syncByStatus("verified");
//             System.out.println("packed status");
//        syncByStatus("packed");
//        syncByStatus("intransit");
//        syncByStatus("outfordelivery");
//        syncByStatus("delivered");
//        System.out.println("verified status");
//    }
//
//
//    @GetMapping("/{status}")
//    public ModelAndView syncByStatus(@PathVariable String status){
//       Thread thread = new Thread() {
//           @Override
//           public void run() {
//                //sync all the records
//                   logService.getAllLogs().clear();
//                    logService.addLog("Calling DB for all the orders with status:"+status);
//                   try {
//                       int page = 0;
//                       int size = 25;
//                    while(true) {
//                        logService.addLog("Page:"+page);
//                        List<OrderCourierStatus> orderCourierStatuses = pendingOrderService.findByStatus(status, size, page++);
//
//
//                        if(page == 1){
//                            logService.addLog("Debug this line:"+page);
//
//                        }
//
//                        if (orderCourierStatuses == null || orderCourierStatuses.isEmpty()) {
//                            logService.addLog("No records found for status:" + status);
//                            break;
//                        }
//                        logService.addLog("Total records to process:" + orderCourierStatuses.size());
//                        AtomicInteger count = new AtomicInteger();
//                        orderCourierStatuses.forEach(orderCourierStatus -> {
//                            logService.addLog("***************Sno.: " + (count.getAndIncrement()) + " , Processing order with AWB:" + orderCourierStatus.getAwb()+"*****************");
//                            if("90187958291".equalsIgnoreCase(orderCourierStatus.getAwb())){
//                                logService.addLog("Debugging for AWB: " + orderCourierStatus.getAwb());
//                            }
//                            if(orderCourierStatus.getStatus().toUpperCase().contains("DELIVERED")){
//                                logService.addLog("final status of  AWB: " + orderCourierStatus.getAwb() + " as it is already delivered or RTO.");
//
//                            }
//                            if(StringUtils.isEmpty(orderCourierStatus.getAwb())){
//                                orderCourierStatus.setStatus(BackendConstants.COURIER_STATUS_DELIVERED);
//                                updatePOD(orderCourierStatus);
//                            }
//
//                            syncDBAndCourierStatus(orderCourierStatus);
//                        });
//                        Thread.sleep(50000l);
//                    }
//
//
//                   } catch (Exception e) {
//                       logService.addLog(e.getMessage());
//                   }
//           }
//       };
//
//       thread.start();
//        ModelAndView modelAndView = new ModelAndView("syncbystatus");
//        return modelAndView;
//    }
//
//    private void syncDBAndCourierStatus(OrderCourierStatus orderCourierStatus) {
//        for (IOrdersSyncService courierService : courierServices) {
//            try {
//                if (orderCourierStatus.getAwb() == null) {
//                    continue;
//                }
//                logService.addLog(orderCourierStatus.getOrderId(), "History Available : " + orderCourierStatus.getCourierStatusHistory().size());
//                logService.addLog(orderCourierStatus.getOrderId(), "Syncing with service: " + courierService.getClass().getSimpleName());
//                if("90193990143".equalsIgnoreCase(orderCourierStatus.getAwb())){
//                    logService.addLog(orderCourierStatus.getOrderId(), "Skipping AWB: " + orderCourierStatus.getAwb() + " for debugging purposes.");
//                }
//                /*if (orderCourierStatus.getCourierStatusHistory() != null && !orderCourierStatus.getCourierStatusHistory().isEmpty()) {
//                    continue;
//                }*/
//                if(BackendConstants.COURIER_STATUS_DELIVERED.equalsIgnoreCase(orderCourierStatus.getStatus()) || BackendConstants.COURIER_STATUS_RTO.equalsIgnoreCase(orderCourierStatus.getStatus()) || BackendConstants.COURIER_STATUS_REFUNDED.equalsIgnoreCase(orderCourierStatus.getStatus())){
//                    dbCourierSyncService.saveDBStatus(orderCourierStatus);
//                    logService.addLog(orderCourierStatus.getOrderId(), "Successfully updated status for AWB:" + orderCourierStatus.getAwb());
//
//                    updatePOD(orderCourierStatus);
//
//                }
//                else {
//                    OrderCourierStatus courierStatus = courierService.syncCourier(orderCourierStatus.getAwb());
//
//                    if (courierStatus != null) {
//                        //fetch from db if success and update the main defaultCourierStatus.
//                        orderCourierStatus.setCourierStatusHistory(courierStatus.getCourierStatusHistory());
//                        dbCourierSyncService.saveDBStatus(orderCourierStatus);
//                        logService.addLog(orderCourierStatus.getOrderId(), "Successfully updated status for AWB:" + courierStatus.getAwb());
//                        updatePOD(orderCourierStatus);
//                    }
//                }
//            } catch (Exception e) {
//                logService.addLog(orderCourierStatus.getOrderId(), "Exception:" + e.getMessage());
//            }
//        }
//    }
//
//    private void updatePOD(OrderCourierStatus orderCourierStatus) {
//        logService.addLog(orderCourierStatus.getOrderId(),orderCourierStatus.getOrderId()+"Updating Pending order db with courier status:"+orderCourierStatus.getStatus());
//        //new thread
//
//            try {
//                boolean updated = pendingOrderService.updatePOD(orderCourierStatus);
//                if(updated){
//                    dbCourierSyncService.saveDBStatus(orderCourierStatus);
//                }
//                logService.addLog(orderCourierStatus.getOrderId(),"POD updated:"+updated+" successfully for AWB:"+orderCourierStatus.getAwb()+" with status: "+orderCourierStatus.getStatus());
//            } catch (Exception e) {
//                logService.addLog(orderCourierStatus.getOrderId(),"Error updating POD: " + e.getMessage());
//            }
//    }
//
//    @GetMapping("/search/{awb}")
//    public ResponseEntity<OrderCourierStatus> searchAWV(@PathVariable String awb){
//        OrderCourierStatus defaultOrderStatus = new OrderCourierStatus();
//        defaultOrderStatus.setStatus("Not Found");
//        defaultOrderStatus.setAwb(awb);
//        for(IOrdersSyncService courierService : courierServices) {
//            try {
//                OrderCourierStatus orderCourierStatus = courierService.syncCourier(awb);
//                if (orderCourierStatus != null) {
//                    //fetch from db if success and update the main defaultCourierStatus.
//                    OrderCourierStatus dbStatus = pendingOrderService.findByAWB(awb);
//                    dbCourierSyncService.saveDBStatus(dbStatus);
//                    dbStatus.setCourierStatusHistory(orderCourierStatus.getCourierStatusHistory());
//                    dbCourierSyncService.saveCourierStatus(dbStatus);
//
//                    //get the latest status from pendingOrder.
//                    return ResponseEntity.ok(dbStatus);
//                }
//            }catch (Exception e){
//                    System.out.println("Exception while fetching AWB: " + awb + " from service: " + courierService.getClass().getSimpleName() + " : "+e.getMessage());
//            }
//        }
//        return ResponseEntity.ok(defaultOrderStatus);
//    }
//
//
//
//}
