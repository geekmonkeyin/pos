package com.gkmonk.pos.controller.sorpo;

import com.gkmonk.pos.model.sorpo.Store;
import com.gkmonk.pos.services.sorpo.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/stores")
public class StoreController {

    @Autowired
    private StoreServiceImpl storeService;
    @GetMapping("")
    public ModelAndView getStores() {
        return new ModelAndView("stores");
    }

    @GetMapping("/add")
    public ModelAndView addStores() {
        return new ModelAndView("addstore");
    }

    @PostMapping("/addStore")
    public ResponseEntity<?> addStore(
            @RequestParam String storeName,
            @RequestParam String location,
            @RequestParam Double rental,
            @RequestParam Double commissionPercent,
            @RequestParam String gstNo,
            @RequestParam(required = false) String gstState,
            @RequestParam String contactPerson,
            @RequestParam String pocPhone,
            @RequestParam(required = false) String pocEmail,
            @RequestParam("agreementPdf") MultipartFile agreementPdf,
            @RequestParam(required = false) String agreementValidTill,
            @RequestParam(required = false) String notes
    ) {

        try {
            Store store = new Store();
            store.setGstNo(gstNo);
            store.setTakeRate(commissionPercent);
            store.setOutputGstRate(18.0);
            store.setName(storeName);
            store.setAddress(location);
            store.setRental(rental);
            store.setId(storeName);
            //store.setAgreementId(agreementPdfID);
            storeService.addNewStore(store);

            return ResponseEntity.ok(store);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest()
                    .body("Error while saving store: " + ex.getMessage());
        }
    }



}
