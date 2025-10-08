package com.gkmonk.pos.services.returns;

import com.gkmonk.pos.model.order.Customer;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.model.returns.OrderLine;
import com.gkmonk.pos.model.returns.OrderLookupResponse;
import com.gkmonk.pos.model.returns.ReturnVerificationRequest;
import com.gkmonk.pos.pod.services.PODServiceImpl;
import com.gkmonk.pos.repo.returns.ReturnVerificationRepo;
import com.gkmonk.pos.services.ImageDBServiceImpl;
import com.gkmonk.pos.utils.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnWorkflowService {

    @Autowired
    private PODServiceImpl podService;

    @Autowired
    private ImageDBServiceImpl imageDBService;

    @Autowired
    private ReturnVerificationRepo returnVerificationRepo;

    public Optional<OrderLookupResponse> findOrder(String orderNo) {
        // TODO: Replace with Shopify/DB lookup
        PackedOrder packedOrder = podService.findByOrderId(orderNo);
        if (packedOrder == null) return Optional.empty();
        return Optional.of(new OrderLookupResponse(
                packedOrder.getGmId(),
                getCustomer(packedOrder),
                getOrderLines(packedOrder),
                packedOrder.getOrderStatusUrl()
                ));
    }
    private List<OrderLine> getOrderLines(PackedOrder packedOrder) {
        List<OrderLine> orderLines = new ArrayList<>();

        packedOrder.getProductDetails().forEach(productDetails -> {
            OrderLine orderLine = new OrderLine();
            orderLine.setQty(productDetails.getQuantity());
            orderLine.setTitle(productDetails.getProductName());
            orderLine.setId(productDetails.getProductId());
            orderLine.setVariantId(productDetails.getVariantId());
            orderLine.setOrderURL(packedOrder.getOrderStatusUrl());
          // List<byte[]> images =  imageDBService.fetchInventoryImagesById(productDetails.getProductId());
            orderLine.setImageURL(productDetails.getImageURL());
            orderLines.add(orderLine);
        });
        return orderLines;
    }

    private Customer getCustomer(PackedOrder packedOrder) {

      Customer customer =  new Customer();
      customer.setName(  packedOrder.getCustomerInfo().getCustomerName());
      customer.setPhone(  packedOrder.getCustomerInfo().getPhoneNo());
      return customer;
    }

    public ReturnVerificationRequest saveReturnVerificationRequest(ReturnVerificationRequest returnVerificationRequest){
        return returnVerificationRepo.save(returnVerificationRequest);
    }

    public List<ReturnVerificationRequest> getAllReturnRequestsByStatus(ReturnStatus returnStatus) {
        return returnVerificationRepo.findByStatus(returnStatus.name());
    }
}
