package com.gkmonk.pos.controller.returns;

import com.gkmonk.pos.model.legacy.OrderSourceType;
import com.gkmonk.pos.model.legacy.ShopifyOrders;
import com.gkmonk.pos.model.order.OrderStatus;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.model.returns.ApproveRefundRequest;
import com.gkmonk.pos.model.returns.OrderLookupResponse;
import com.gkmonk.pos.model.returns.ReshipRequestDTO;
import com.gkmonk.pos.model.returns.ReturnOrder;
import com.gkmonk.pos.model.returns.ReturnVerificationRequest;
import com.gkmonk.pos.model.returns.ReturnVerificationResponse;
import com.gkmonk.pos.pod.services.PODServiceImpl;
import com.gkmonk.pos.services.ImageDBServiceImpl;
import com.gkmonk.pos.services.courier.IOrdersSyncService;
import com.gkmonk.pos.services.returns.ReturnServiceImpl;
import com.gkmonk.pos.services.returns.ReturnWorkflowService;
import com.gkmonk.pos.services.shopify.ShopifyDBServiceImpl;
import com.gkmonk.pos.services.shopify.ShopifyMapper;
import com.gkmonk.pos.utils.ReturnStatus;
import com.gkmonk.pos.utils.StringUtils;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/order/return")
public class ReturnController {

    @Autowired
    private ReturnWorkflowService service;
    @Autowired
    private ShopifyDBServiceImpl  shopifyDBService;

    @Autowired
    private PODServiceImpl podService;

    @Autowired
    private List<IOrdersSyncService> courierServices;
    @Autowired
    private ReturnServiceImpl returnOrderService;
    @Autowired
    private ImageDBServiceImpl imageService;

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
        ShopifyOrders shopifyOrders = shopifyDBService.getOrderByName(orderId);
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


    @GetMapping("/verify")
    public ModelAndView verifyReturn() {
        ModelAndView modelAndView = new ModelAndView("verifyreturn");
        return modelAndView;
    }

    ///v1/order/return/lookup get

    @GetMapping(path = "/lookup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderLookupResponse> lookup(
            @RequestParam @NotBlank String orderNo) {

        ShopifyOrders shopifyOrders = shopifyDBService.getOrderByName(orderNo);
        Optional<OrderLookupResponse> orders = shopifyOrders == null ? service.findOrder(orderNo) : ShopifyMapper.convertToOrderLookupResponse(shopifyOrders);
        return orders
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(
            path = "/verify/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReturnVerificationResponse> submitMultipart(
            @RequestPart("metadata") ReturnVerificationRequest metadata,
            @RequestPart(value = "video", required = false) MultipartFile video,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        // Basic validation -> return 400 instead of 500
        if (metadata == null || metadata.getEmpId() == null || metadata.getEmpId().isBlank()) {
            return ResponseEntity.badRequest().body(
                    ReturnVerificationResponse.error("Employee ID is required")
            );
        }
        if (metadata.getLines() == null || metadata.getLines().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ReturnVerificationResponse.error("At least one line must be marked")
            );
        }

        try {
        for (MultipartFile image : images) {
            // Save or process each image as needed
            String fileId = imageService.saveImages(image.getInputStream(), image.getOriginalFilename());
                metadata.getImages().add(fileId);
        }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        metadata.setReturnStatus(ReturnStatus.DRAFT);
        service.saveReturnVerificationRequest(metadata);
        long imageCount = images != null ? images.size() : 0;
        long videoSize  = (video != null) ? video.getSize() : 0L;

        ReturnVerificationResponse ok = new ReturnVerificationResponse();
        ok.setOrderNo(metadata.getOrderNo());
        ok.setVideoSize(videoSize);
        ok.setMessage("Saved");
        ok.setImageCount(imageCount);
        return ResponseEntity.ok(ok);
    }

    //returnreport
    @GetMapping("/returnreport")
    public ModelAndView returnReport() {
        ModelAndView modelAndView = new ModelAndView("returnreport");
         return modelAndView;
    }

    //approve-refund

    @GetMapping("/returnreceived")
    public ResponseEntity<List<ReturnVerificationRequest>> approveRefund() {
        List<ReturnVerificationRequest> requests = service.getAllReturnRequestsByStatus(ReturnStatus.DRAFT);
        if(requests != null){
            requests.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> r.getImages() != null && !r.getImages().isEmpty())
                    .forEach(r -> {
                        List<String> ids = r.getImages().stream()
                                .filter(Objects::nonNull)
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .distinct()
                                .toList();

                        if (!ids.isEmpty()) {
                            String imageIds = String.join(",", ids);
                            List<byte[]> inventoryImages = imageService.fetchInventoryImagesById(imageIds);
                            r.setReturnProductImages(inventoryImages);
                        }
                    });

        }
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/approve-refund")
    public ResponseEntity<ApiResponse> approveRefund(@RequestBody ApproveRefundRequest body,
                                                     @RequestHeader(value = "X-EMP-ID", required = false) String empIdHeader) {
        try {
            // You can also pull the employee id from your auth principal, header, or session
            String actorEmpId = empIdHeader; // or resolve from SecurityContext
            ReturnVerificationRequest updated = null;//returnOrderService.approveRefund(body, actorEmpId);
            boolean refunded  = service.updateReturnRequest(body,ReturnStatus.APPROVED);
            return ResponseEntity.ok(ApiResponse.ok("Refund approved", body.getReturnId(), ReturnStatus.APPROVED.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail("Unable to approve refund: " + e.getMessage()));
        }
    }

    @PostMapping("/reject-refund")
    public ResponseEntity<ApiResponse> rejectRefund(@RequestBody ApproveRefundRequest body,
                                                     @RequestHeader(value = "X-EMP-ID", required = false) String empIdHeader) {
        try {
            // You can also pull the employee id from your auth principal, header, or session
            String actorEmpId = empIdHeader; // or resolve from SecurityContext
            ReturnVerificationRequest updated = null;//returnOrderService.approveRefund(body, actorEmpId);
            boolean refunded  = service.updateReturnRequest(body,ReturnStatus.REJECTED);
            return ResponseEntity.ok(ApiResponse.ok("Refund Rejected", body.getReturnId(), ReturnStatus.REJECTED.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.fail("Unable to rejected refund: " + e.getMessage()));
        }
    }

    @PostMapping("/reship")
    public ResponseEntity<ApiResponse> createReship(@RequestBody ReshipRequestDTO req) {
        if (CollectionUtils.isEmpty(req.getItems())) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("Select at least one product to reship."));
        }
        boolean anyInvalidQty = req.getItems().stream().anyMatch(i -> i.getQty() <= 0);
        if (anyInvalidQty) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("All quantities must be >= 1."));
        }

        // 3) Build a new Order (Reship)
        //    Replace with your own order-number generator if needed.
        ShopifyOrders shopifyOrder =  shopifyDBService.getOrderByName(req.getOrderNo());
        ShopifyOrders reshipOrder = shopifyOrder == null ? ShopifyMapper.convertPackedOrderToShopifyOrder(podService.findByOrderId(req.getOrderNo())): shopifyOrder;
        String reshipOrderNo = reshipOrder.getName()+"-R1";

        ShopifyOrders reship = new ShopifyOrders();
        reship.setName(reshipOrder.getName());
        reship.setId(reshipOrderNo);
        reship.setCreated_at(Instant.now().toString());
        reship.setOrderSourceType(OrderSourceType.SHOPIFY);
        reship.setParentOrderNo(req.getOrderNo());
        reship.setCustomStatus(OrderStatus.DISPATCHED.name());
        reship.setCod(false);

        req.getItems().forEach(item -> {
               String variantID =  item.getVariantId();
            assert shopifyOrder != null;
            shopifyOrder.getItems().forEach(originalItem -> {
                     if(originalItem.getVariant_id().equals(variantID)){
                          originalItem.setQuantity(item.getQty());
                          if(reship.getItems() == null){
                              reship.setItems(new ArrayList<>());
                          }
                          reship.getItems().add(originalItem);
                     }
                });
        });
        log.info("Creating reship order: {} for return: {} items: {}",
                reshipOrderNo, req.getReturnId(), reship.getItems().size());

        return ResponseEntity.ok(ApiResponse.ok("Reship order created.", reshipOrderNo,ReturnStatus.RESHIPPED.name()));
    }



        @Data
    static class ApiResponse {
        private boolean success;
        private String message;
        private String returnId;
        private String status;

        static ApiResponse ok(String message, String returnId, String status) {
            ApiResponse r = new ApiResponse();
            r.success = true; r.message = message; r.returnId = returnId; r.status = status;
            return r;
        }
        static ApiResponse fail(String message) {
            ApiResponse r = new ApiResponse();
            r.success = false; r.message = message;
            return r;
        }
    }

}
