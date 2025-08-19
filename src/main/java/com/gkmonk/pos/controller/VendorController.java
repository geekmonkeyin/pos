package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.Vendor;
import com.gkmonk.pos.services.vendor.VendorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/v1/vendor")
public class VendorController {

    @Autowired
    private VendorServiceImpl vendorService;

    @GetMapping("")
    public ModelAndView getVendorPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("VendorManagement");
        return model;
    }

    @GetMapping("/create")
    public ModelAndView createVendorPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("createvendor");
        return model;
    }

    @PostMapping("/createVendor")
    public ResponseEntity<String> createVendor(@RequestBody Vendor vendor) {
        try {
            // Logic to save the vendor (e.g., using a service layer)
            vendorService.saveVendor(vendor);

            return ResponseEntity.ok("Vendor created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create vendor. Please try again." + e.getMessage());
        }
    }


    @GetMapping("/search/{vendorName}")
    public ResponseEntity<List<Vendor>> searchVendors(@PathVariable String vendorName) {
        if (vendorName == null || vendorName.length() < 3) {
            return ResponseEntity.badRequest().build(); // Return 400 if vendorName is invalid
        }

        List<Vendor> vendors = vendorService.searchByName(vendorName);
        if (vendors.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no vendors found
        }

        return ResponseEntity.ok(vendors); // Return 200 with the list of vendors
    }

    @GetMapping("/update")
    public ModelAndView updateVendorPage() {
        ModelAndView model = new ModelAndView();
        model.setViewName("updateVendor");
        return model;
    }

    @GetMapping("/bulkupdate")
    public ModelAndView bulkUpdate() {
        ModelAndView model = new ModelAndView();
        model.setViewName("vendorbulkupdate");
        return model;
    }

}
