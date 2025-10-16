package com.gkmonk.pos.controller.vendor;

import com.gkmonk.pos.model.PaymentReceipts;
import com.gkmonk.pos.model.purchase.PurchaseOrder;
import com.gkmonk.pos.services.PaymentServiceImpl;
import com.gkmonk.pos.services.purchaseorder.PurchaseOrderServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/vendor/ledger")
public class LedgerController {

    @Autowired
    private PurchaseOrderServiceImp purchaseOrderServiceImp;

    @Autowired
    private PaymentServiceImpl paymentService;

    @GetMapping
    public ModelAndView getLedger() {
        return new ModelAndView("ledger");
    }

    /**
     * {
     *   "purchases": [ ... ],
     *   "payments":  [ ... ]
     * }
     */
    @GetMapping("/purchasereport")
    public ResponseEntity<List<PurchaseOrder>> getPurchaseReport(@RequestParam(value = "vendorName", required = false) String vendorName)
    {
        Optional<List<PurchaseOrder>> orders = purchaseOrderServiceImp.findAll(vendorName);
        return orders.map(purchaseOrders -> ResponseEntity.ok().body(purchaseOrders)).orElseGet(() -> ResponseEntity.ok().body(new ArrayList<>()));

    }

    @GetMapping("/paymentreport")
    public ResponseEntity<List<PaymentReceipts>> getPaymentReport(@RequestParam(value = "vendorName", required = false) String vendorName)
    {
        Optional<List<PaymentReceipts>> orders = paymentService.findAll(vendorName);
        return orders.map(purchaseOrders -> ResponseEntity.ok().body(purchaseOrders)).orElseGet(() -> ResponseEntity.ok().body(new ArrayList<>()));

    }
}
