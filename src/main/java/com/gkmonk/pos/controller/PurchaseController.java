package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.purchase.PurchaseOrder;
import com.gkmonk.pos.services.purchaseorder.PurchaseOrderServiceImp;
import com.gkmonk.pos.utils.DateUtils;
import com.gkmonk.pos.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/vendor/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseOrderServiceImp purchaseOrderService;


    @GetMapping("/order")
    public ModelAndView purchaseOrder() {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase");
        return model;
    }

    @PostMapping("/update")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
        try {
            // Logic to update the purchase order (e.g., using a service layer)
            purchaseOrderService.save(purchaseOrder);
            return ResponseEntity.ok(purchaseOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/reports")
    public ModelAndView purchaseReport() {


        ModelAndView model = new ModelAndView();
        model.setViewName("purchasereport");
        return model;
    }

    @GetMapping("/filterReport")
    public ResponseEntity<List<PurchaseOrder>> purchaseFilterReport(@RequestParam(required = false) String vendorName,
                                                       @RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String endDate) {

        vendorName = StringUtils.updateVendorName(vendorName);
        startDate = StringUtils.updateStartDate(startDate);
        endDate = StringUtils.updateEndDate(endDate);

        Optional<List<PurchaseOrder>> result =  purchaseOrderService.findByCriteria(vendorName, startDate, endDate);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/uploadCSV")
    public ResponseEntity<String> uploadPurchaseCSV(@RequestParam("vendorName") String vendorName, @RequestParam("file") MultipartFile file) {
        // Logic to process the uploaded CSV for purchases

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
            String line;
            boolean isHeader = true;
            //remove double quotes from
            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header row
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 4) {
                    return ResponseEntity.badRequest().body("Invalid CSV format");
                }

                String date = DateUtils.convertToDBFormat(fields[0].trim());
                String invoiceNo = fields[2].trim();
                String amount = fields[3].trim();
                String txnType = fields[1].trim();
                if( !"Sale".equalsIgnoreCase(txnType)) {
                    continue; // Skip if not a purchase transaction
                }
                // Process the extracted fields (e.g., save to database or log)
                System.out.println("Date: " + date + ", InvoiceNo: " + invoiceNo + ", Amount: " + amount);
                PurchaseOrder purchaseOrder = new PurchaseOrder();
                purchaseOrder.setOrderDate(date);
                purchaseOrder.setVendorName(vendorName);
                purchaseOrder.setOrderId(invoiceNo);
                purchaseOrder.setTotalAmount(Double.parseDouble(amount));
                purchaseOrderService.save(purchaseOrder);
                //purchaseOrderList.add(purchaseOrder);
            }

            return ResponseEntity.ok("CSV processed successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }

    }

}
