package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.Inventory;
import com.gkmonk.pos.services.InventoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/report/shopify")
public class StockReportController{

    @Autowired
    private InventoryServiceImpl inventoryService;

    @GetMapping("stockmismatchreport")
    public ModelAndView  getStockMismatchReport() {

        //fetch all the inventory from db where shopifystock and quantity not matching
        Optional<List<Inventory>> inventoryList = inventoryService.findMismatchedStock();
        ModelAndView model = new ModelAndView();
        model.setViewName("stockmismatchreport");
        model.addObject("mismatched",inventoryList.get());

        return model;
    }


}
