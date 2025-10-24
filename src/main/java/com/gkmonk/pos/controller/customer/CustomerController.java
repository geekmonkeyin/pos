package com.gkmonk.pos.controller.customer;

import com.gkmonk.pos.model.order.Customer;
import com.gkmonk.pos.services.shopify.ShopifyServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/customer")
public class CustomerController {

    @Autowired
    private ShopifyServiceImpl shopifyService;

    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomer(@RequestParam("phone")String phoneno) throws Exception {
        List<Customer> cutomers = shopifyService.getCustomerByPhoneNo(phoneno);
        return ResponseEntity.ok(cutomers);
    }
}
