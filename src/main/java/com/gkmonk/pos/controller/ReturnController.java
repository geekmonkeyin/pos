package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.model.returns.ReturnOrder;
import com.gkmonk.pos.pod.services.PODServiceImpl;
import com.gkmonk.pos.services.courier.IOrdersSyncService;
import com.gkmonk.pos.services.returns.ReturnServiceImpl;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/order/return")
public class ReturnController {

    @Autowired
    private PODServiceImpl podService;

    @Autowired
    private List<IOrdersSyncService> courierServices;
    @Autowired
    private ReturnServiceImpl returnOrderService;

    @GetMapping("")
    public ModelAndView getReturnPage() {
        ModelAndView modelAndView = new ModelAndView("return");
        return modelAndView;
    }

    @GetMapping("/initiateReturn")
    public ModelAndView initiateReturn(){
        ModelAndView modelAndView = new ModelAndView("initiatereturn");
        return modelAndView;
    }

    @GetMapping("/fetchDetails/{orderId}")
    public ResponseEntity<PackedOrder> fetchDetails(@PathVariable String orderId) {
        PackedOrder packedOrder = podService.findByOrderId(orderId);
        return ResponseEntity.ok(packedOrder);
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateReturn(@RequestBody Map<String, Object> requestBody) {
        try {
            String orderId = (String) requestBody.get("orderId");
            List<Map<String,Integer>> productIds = ( List<Map<String,Integer>>) requestBody.get("productIds");

            if (orderId == null || productIds == null || productIds.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request: Order ID or Product IDs are missing.");
            }

            PackedOrder packedOrder = podService.findByOrderId(orderId);
            if (packedOrder == null) {
                return ResponseEntity.status(404).body("Order not found.");
            }

            // Process return logic (e.g., update database, mark products as returned)
            // Example: Update the return status for the selected products
            //create return order
            PackedOrder returnOrder = new PackedOrder();
            returnOrder.setOrderId("R"+packedOrder.getOrderId());
            returnOrder.setCustomerInfo(packedOrder.getCustomerInfo());
            returnOrder.setProductDetails(new ArrayList<>());
            returnOrder.setGmId(packedOrder.getGmId());
            returnOrder.setTotalAmount(-180.0d);
             packedOrder.getProductDetails().forEach(product -> {
                 productIds.forEach(productMap -> {
                     if (productMap.containsKey("productId") && product.getProductId().equals(productMap.get("productId"))) {
                         // Logic to mark the product as returned (e.g., update status)
                         product.setQuantity(productMap.get("returnQuantity"));
                         returnOrder.getProductDetails().add(product);
                         double refundableAmount = product.getPrice() * productMap.get("returnQuantity");
                         returnOrder.setTotalAmount(returnOrder.getTotalAmount() + refundableAmount);
                         System.out.println("Processing return for product: " + product.getProductId());
                     }
                 });
            });

             String link = createReturnOrder(returnOrder);
             if(StringUtils.isNotBlank(link)){
                 ReturnOrder returnOrderDetails = new ReturnOrder();
                 returnOrderDetails.setOrderId(returnOrder.getOrderId());
                 returnOrderDetails.setPackedOrder(returnOrder);
                 returnOrderDetails.setLabelLink(link);
                 returnOrderService.saveReturnOrder(returnOrderDetails);
             }
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("labelLink",link);
            responseJson.addProperty("returnAWB", returnOrder.getAwb());

            return ResponseEntity.ok().body(responseJson.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to initiate return: " + e.getMessage());
        }
    }

    private String createReturnOrder(PackedOrder returnOrder) {
        for(IOrdersSyncService service : courierServices){
             if(service.isReturnSupported()){
                   return service.createReturnOrder(returnOrder);

             }
        }
        return "Return service not available";
    }



}
