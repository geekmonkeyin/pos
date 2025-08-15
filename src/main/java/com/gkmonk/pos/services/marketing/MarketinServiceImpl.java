package com.gkmonk.pos.services.marketing;

import com.gkmonk.pos.model.notification.NotificationType;
import com.gkmonk.pos.model.notification.OrderNotification;
import com.gkmonk.pos.model.pod.CustomerInfo;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.services.OrderReportsServiceImpl;
import com.gkmonk.pos.services.courier.IOrdersSyncService;
import com.gkmonk.pos.services.interakt.InteraktServiceImpl;
import com.gkmonk.pos.utils.DateUtils;
import com.gkmonk.pos.utils.POSConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.gkmonk.pos.utils.POSConstants.BLUEDART_COURIER;
import static com.gkmonk.pos.utils.POSConstants.DELHIVERY_COURIER;
import static com.gkmonk.pos.utils.POSConstants.DTDC_COURIER;
import static com.gkmonk.pos.utils.POSConstants.XPRESSBEES_COURIER;

@Service
public class MarketinServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(MarketinServiceImpl.class);
    @Autowired
    private InteraktServiceImpl interaktService;
    @Autowired
    private List<IOrdersSyncService> ordersSyncServices;
    @Autowired
    private OrderReportsServiceImpl orderReportsService;

    public void sendShippingUpdate(PackedOrder packedOrder, OrderNotification orderNotification) {
        addUser(packedOrder.getCustomerInfo(), orderNotification);
        sendPickedUpNotification(packedOrder, orderNotification);

    }

    public void sendIntransitUpdate(PackedOrder packedOrder, OrderNotification orderNotification) {

        OrderNotification.EventStatus eventStatus = orderNotification.getEvents().get(NotificationType.IN_TRANSIT.name());
        if (eventStatus != null && POSConstants.NOTIFICATION_SENT.equalsIgnoreCase(eventStatus.getStatus())) {
            return;
        }

        String trackingURL = getTrackingURL(packedOrder.getAwb(), packedOrder.getCourierCompany());
        Map<String,String> eddMap = getCurrentStatusForAwb(packedOrder.getAwb(), packedOrder.getCourierCompany());
        String edd = eddMap.get("edd");
        boolean msgSent = interaktService.sendMsgOnProductShipped(packedOrder.getCustomerName(), packedOrder.getAwb(), packedOrder.getCustomerInfo().getPhoneNo(),
                edd, packedOrder.getCourierCompany(), packedOrder.getPaymentMode(), "", trackingURL);
        orderNotification.setNextUpdate(DateUtils.getNextUpdate(NotificationType.IN_TRANSIT.getNextUpdate()));
        orderNotification.setNextStatus(NotificationType.OUT_FOR_DELIVERY.getValue());
        updateEvent(NotificationType.IN_TRANSIT, orderNotification, msgSent);
    }

    private void addUser(CustomerInfo customerInfo, OrderNotification orderNotification) {
        boolean userAdded = interaktService.addUser(customerInfo.getCustomerName(), customerInfo.getPhoneNo());
        updateEvent(NotificationType.USER_ADDED, orderNotification, userAdded);
    }

    private void sendPickedUpNotification(PackedOrder packedOrder, OrderNotification orderNotification) {
        //next update in 1 day
        sendNotification(orderNotification, NotificationType.ORDER_PICKED, NotificationType.IN_TRANSIT, packedOrder);
    }


    private void updateEvent(NotificationType notificationType, OrderNotification orderNotification, boolean sent) {
        Map<String, OrderNotification.EventStatus> events = orderNotification.getEvents() == null ? new HashMap<>() : orderNotification.getEvents();
        OrderNotification.EventStatus eventStatus = new OrderNotification.EventStatus();
        if (sent) {
            eventStatus.setStatus(POSConstants.NOTIFICATION_SENT);
        } else {
            eventStatus.setStatus(POSConstants.NOTIFICATION_FAILED);
        }
        eventStatus.setSentAt(Instant.now());
        events.put(notificationType.name(), eventStatus);
        orderNotification.setEvents(events);
    }

      private String getTrackingURL(String awb, String courierCompany) {
        switch (courierCompany.toUpperCase()) {
            case DELHIVERY_COURIER:
                return "https://www.delhivery.com/track-v2/package" + awb;
            case XPRESSBEES_COURIER:
                return "https://www.xpressbees.com/track/" + awb;
            case "ecom express":
                return "https://www.ecomexpress.in/track/" + awb;
            case BLUEDART_COURIER:
                return "https://www.bluedart.com/web/guest/trackdartresultthirdparty?trackFor=0&trackNo=" + awb;
            case DTDC_COURIER:
                return "https://txk.dtdc.com/ctbs-tracking/customerInterface.tr?submitName=showCITrackingDetails&cType=Consignment&cnNo=" + awb;

            default:
                return "https://www.geekmonkey.in"; // Fallback URL
        }
    }

    /**
     * @param orderNotification
     * @param targetNotificationType if notification is for IN_TRANSIT send IN_TRANSIT
     */
    public void sendNotification(OrderNotification orderNotification, NotificationType targetNotificationType, NotificationType nextNotification, PackedOrder packedOrder) {
        OrderNotification.EventStatus eventStatus = orderNotification.getEvents().get(targetNotificationType.name());
        if (eventStatus != null && POSConstants.NOTIFICATION_SENT.equalsIgnoreCase(eventStatus.getStatus())) {
            return;
        }
        packedOrder.setCourierCompany(orderNotification.getCourierCompany());
        boolean msgSent = callWhatsAppBasedOnNotificationType(targetNotificationType, packedOrder);

        orderNotification.setNextUpdate(DateUtils.getNextUpdate(targetNotificationType.getNextUpdate()));
        orderNotification.setNextStatus(nextNotification.getValue());
        orderNotification.setAwb(packedOrder.getAwb());
        orderNotification.setOrderId(packedOrder.getOrderId());
        orderNotification.setCourierCompany(packedOrder.getCourierCompany());
        updateEvent(targetNotificationType, orderNotification, msgSent);
    }


    private boolean callWhatsAppBasedOnNotificationType(NotificationType targetNotificationType, PackedOrder packedOrder) {
        String trackingURL = getTrackingURL(packedOrder.getAwb(), packedOrder.getCourierCompany());
        String edd = getEstimatedDeliveryDate(packedOrder.getAwb(), packedOrder.getCourierCompany()).get("edd");
        switch (targetNotificationType) {
            case ORDER_PICKED:
                return interaktService.sendMsgOnProductShipped(packedOrder.getCustomerName(), packedOrder.getAwb(), packedOrder.getCustomerInfo().getPhoneNo(),
                        edd, packedOrder.getCourierCompany(), packedOrder.getPaymentMode(), "", trackingURL);
            case IN_TRANSIT:
                return interaktService.sendMsgOnProductInTransit(packedOrder.getCustomerName(), packedOrder.getAwb(), packedOrder.getCustomerInfo().getPhoneNo(),
                        edd, packedOrder.getCourierCompany(), packedOrder.getPaymentMode(), "", trackingURL);
            case OUT_FOR_DELIVERY:
                return interaktService.sendMsgOnProductOutForDelivery(packedOrder.getCustomerName(), packedOrder.getAwb(), packedOrder.getCustomerInfo().getPhoneNo(),
                        edd, packedOrder.getCourierCompany(), packedOrder.getPaymentMode(), "", trackingURL);
            case NOT_DELIVERED:
                //todo
                return false;
            case RETURN_TO_ORIGIN:
                boolean rto = interaktService.sendMsgOnProductDelivered(packedOrder.getCustomerName(), packedOrder.getAwb(), packedOrder.getCustomerInfo().getPhoneNo(),
                        edd, packedOrder.getCourierCompany(), packedOrder.getPaymentMode(), "", trackingURL);
                //do followup

                return rto;
            case DELIVERED:
                //follow up for feedback;
                return interaktService.sendMsgOnProductDelivered(packedOrder.getCustomerName(), packedOrder.getAwb(), packedOrder.getCustomerInfo().getPhoneNo(),
                        edd, packedOrder.getCourierCompany(), packedOrder.getPaymentMode(), "", trackingURL);

        }
        return false;
    }

    public NotificationType sendNextUpdates(OrderNotification orderNotification) {
        NotificationType targetNotification = NotificationType.getNotificationType(orderNotification.getNextStatus());
        if (targetNotification == null || NotificationType.FEEDBACK.equals(targetNotification)) {
            log.warn("No notification type found for next status: {}", orderNotification.getNextStatus());
            return null;
        }
        Map<String,String> currentStatus = getCurrentStatusForAwb(orderNotification.getAwb(),orderNotification.getCourierCompany());
        log.info("Current Status is:{}",currentStatus.get("currentStatus"));
        log.info("Current edd is:{}",currentStatus.get("edd"));
        //todo
        return NotificationType.getNotifiationTypeFromText(currentStatus.get("currentStatus"));
        // updatNextEvent(orderNotification,orderNotification.getNextStatus(),currentStatus.get("status"));
    }



    public Map<String,String> getEstimatedDeliveryDate(String awb, String courierCompany) {
        return fetchFromCourierService(awb, courierCompany, service -> {
            try {
                return service.getEDD(awb);
            } catch (Exception e) {
                log.error("Not able to fetch EDD from :{} : Exception{}", service.getClass(), e.getMessage());
            }
            return Collections.EMPTY_MAP;
        });
    }

    private Map<String,String> getCurrentStatusForAwb(String awb, String courierCompany) {
        return fetchFromCourierService(awb, courierCompany, service -> {
            try {
                Map<String,String> currentStatus =  service.getCurrentStatus(awb);
                if(!currentStatus.isEmpty()){
                    return currentStatus;
                }

            } catch (Exception e) {
                log.error("Not able to fetch Current Status from :{} : Exception{}", service.getClass(), e.getMessage());
            }
            return Collections.EMPTY_MAP;
        });
    }

    private Map fetchFromCourierService(String awb, String courierCompany, Function<IOrdersSyncService, Map<String,String>> serviceFunction) {
        for (IOrdersSyncService service : ordersSyncServices) {
            boolean isSupported = service.getSupportedCourierCompanies().stream()
                    .anyMatch(comp -> comp.equalsIgnoreCase(courierCompany));

            if (isSupported) {
                try {
                    Map<String,String> result = serviceFunction.apply(service);
                    if (result != null && !result.isEmpty()) {
                        return result;
                    }
                } catch (Exception e) {
                    log.error("Error fetching data from service: {} Exception: {}", service.getClass(), e.getMessage());
                }
            }
        }
        return Collections.EMPTY_MAP;
    }

}
