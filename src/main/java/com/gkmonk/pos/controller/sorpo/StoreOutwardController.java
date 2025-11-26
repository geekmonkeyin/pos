package com.gkmonk.pos.controller.sorpo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/stores/outward")
public class StoreOutwardController {

    @GetMapping("")
    public ModelAndView getOutwardPage() {
        ModelAndView modelAndView = new ModelAndView("outwards");
        return modelAndView;
    }

}
