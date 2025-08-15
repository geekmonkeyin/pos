package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.PaymentReceipts;
import com.gkmonk.pos.model.purchase.PurchaseOrder;
import com.gkmonk.pos.services.ImageDBServiceImpl;
import com.gkmonk.pos.services.PaymentServiceImpl;
import com.gkmonk.pos.utils.DateUtils;
import com.gkmonk.pos.utils.StringUtils;
import com.gkmonk.pos.utils.WhatsappUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

@RestController
@RequestMapping("/v1")
public class PaymentController {

    @Autowired
    private PaymentServiceImpl paymentService;
    @Autowired
    private ImageDBServiceImpl imageDBService;

    @PostMapping("/uploadPayment")
    public ResponseEntity<String> uploadPayment(
            @RequestParam("screenshot") MultipartFile screenshot,
            @RequestParam("paymentTo") String paymentTo,
            @RequestParam("vendorName") String vendorName,
            @RequestParam("amount") Double amount,
            @RequestParam("date") String date,
            @RequestParam("remarks") String remarks,
            @RequestParam("whatsappNumber") String whatsappNumber) throws IOException {

        PaymentReceipts  paymentReceipts =  new PaymentReceipts(paymentTo, vendorName, amount, date, whatsappNumber,remarks);
        if(screenshot !=null ){
            String id = imageDBService.saveImages(screenshot.getInputStream(),screenshot.getOriginalFilename());
            paymentReceipts.setImageId(id);
        }
        paymentService.saveReceipt(paymentReceipts);
        WhatsappUtils.sendPaymentReceipt(paymentReceipts.getVendorName(), paymentReceipts.getWhatsappNumber(), paymentReceipts.getAmount()
                ,paymentReceipts.getDate(),paymentReceipts.getRemarks());
        // Return a success response
        return ResponseEntity.ok("Payment details uploaded successfully.");
    }

    @GetMapping("/vendor/payment/add")
    public ModelAndView paymentReport(){
        ModelAndView modelAndView = new ModelAndView("paymentreport");
        modelAndView.setViewName("paymentreport");
        return modelAndView;
    }

    @GetMapping("/payment/reports")
    public ModelAndView fetchReport(){
        ModelAndView modelAndView = new ModelAndView("paymentreport");
        List<PaymentReceipts> receipts =  paymentService.findAllReceipts();
        modelAndView.addObject("receipts", receipts);
        modelAndView.setViewName("paymentallreport");
        return modelAndView;
    }

    @GetMapping("/payment/filterReport")
    public ResponseEntity<List<PaymentReceipts>> fetchFilteredReport(
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String paymentTo,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        vendorName = StringUtils.updateVendorName(vendorName);
        paymentTo = StringUtils.updateVendorName(paymentTo);
        minAmount = StringUtils.updateMinAmount(minAmount);
        maxAmount = StringUtils.updateMaxAmount(maxAmount);
        startDate = StringUtils.updateStartDate(startDate);
        endDate = StringUtils.updateEndDate(endDate);

        List<PaymentReceipts> receipts = paymentService.findFilteredReceipts(vendorName, paymentTo,minAmount, maxAmount, startDate, endDate);
         if (receipts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(receipts);
    }

    @PostMapping("/vendor/payment/uploadCSV")
    public ResponseEntity<String> uploadPaymentCSV(@RequestParam("vendorName") String vendorName, @RequestParam("file") MultipartFile file) {

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
                if( "Sale".equalsIgnoreCase(txnType)) {
                    continue; // Skip if not a purchase transaction
                }
                String paymentTo = fields[14].trim();
                // Process the extracted fields (e.g., save to database or log)
                System.out.println("Date: " + date + ", InvoiceNo: " + invoiceNo + ", Amount: " + amount);
                PaymentReceipts paymentReceipts = new PaymentReceipts();
                paymentReceipts.setDate(date);
                paymentReceipts.setVendorName(vendorName);
                paymentReceipts.setInvoiceId(invoiceNo);
                paymentReceipts.setId(invoiceNo);
                paymentReceipts.setAmount(Double.parseDouble(amount));
                paymentReceipts.setPaymentTo(paymentTo);
                paymentService.saveReceipt(paymentReceipts);
                //purchaseOrderList.add(purchaseOrder);
            }

            return ResponseEntity.ok("CSV processed successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        }

    }






    
}