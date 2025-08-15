package com.gkmonk.pos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/storage")
public class StorageController {

    @GetMapping("")
    public ModelAndView getStoragePage() {
        ModelAndView modelAndView = new ModelAndView("storage");
        modelAndView.setViewName("storagevisualreport");
        return modelAndView;
    }

}
